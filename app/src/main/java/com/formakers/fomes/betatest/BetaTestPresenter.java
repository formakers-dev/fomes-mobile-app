package com.formakers.fomes.betatest;

import android.util.Pair;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@BetaTestDagger.Scope
public class BetaTestPresenter implements BetaTestContract.Presenter {

    public static String TAG = "BetaTestPresenter";

    private BetaTestListAdapterContract.Model betaTestListAdapterModel;

    private BetaTestContract.View view;
    private BetaTestService betaTestService;
    private UserDAO userDAO;
    private User user;
    private EventLogService eventLogService;
    private AnalyticsModule.Analytics analytics;
    private FomesUrlHelper fomesUrlHelper;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public BetaTestPresenter(BetaTestContract.View view, BetaTestService betaTestService, EventLogService eventLogService, UserDAO userDAO, AnalyticsModule.Analytics analytics, FomesUrlHelper fomesUrlHelper) {
        this.view = view;
        this.betaTestService = betaTestService;
        this.eventLogService = eventLogService;
        this.userDAO = userDAO;
        this.analytics = analytics;
        this.fomesUrlHelper = fomesUrlHelper;
    }

    @Override
    public void setAdapterModel(BetaTestListAdapterContract.Model adapterModel) {
        this.betaTestListAdapterModel = adapterModel;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return this.analytics;
    }

    @Override
    public void initialize() {
        compositeSubscription.add(
                userDAO.getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(user -> {
                    this.user = user;
                    view.setUserNickName(user.getNickName());
                })
                .observeOn(Schedulers.io())
                .flatMap(user -> loadToBetaTestList(new Date()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> view.showLoading())
                .doAfterTerminate(() -> view.hideLoading())
                .toCompletable().subscribe(() -> { }, e -> Log.e(TAG, String.valueOf(e))));
    }

    @Override
    public Single<List<BetaTest>> loadToBetaTestList(Date sortingCriteriaDate) {
        return betaTestService.getBetaTestList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(betaTests -> {
                    if (betaTests == null || betaTests.isEmpty()) {
                        throw new IllegalStateException("Empty List");
                    }

                    List<BetaTest> attendableList = new ArrayList<>();
                    List<BetaTest> completedList = new ArrayList<>();

                    for (BetaTest betaTest : betaTests) {
                        if (betaTest.isCompleted()) {
                            completedList.add(betaTest);
                        } else {
                            attendableList.add(betaTest);
                        }
                    }

                    Comparator<BetaTest> comparator = (o1, o2) -> compareByCloseDate(o1, o2, sortingCriteriaDate);

                    Collections.sort(attendableList, comparator);
                    Collections.sort(completedList, comparator);

                    betaTestListAdapterModel.clear();
                    betaTestListAdapterModel.addAll(attendableList);
                    betaTestListAdapterModel.addAll(completedList);

                    view.refreshBetaTestList();
                    view.showBetaTestListView();
                })
                .doOnError(e -> view.showEmptyView());
    }

    @Override
    public void requestBetaTestProgress(String betaTestId) {
        compositeSubscription.add(
                betaTestService.getBetaTestProgress(betaTestId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(newBetaTest -> {
                            int position = betaTestListAdapterModel.getPositionById(newBetaTest.getId());

                            if (position < 0) {
                                throw new IllegalStateException("There isn't the betatest in list. It might be initialized by system.");
                            }

                            return new Pair<>(newBetaTest, position);
                        })
                        .subscribe(betaTestPair -> {
                            BetaTest newBetaTest = betaTestPair.first;
                            int position = betaTestPair.second;

                            BetaTest originalBetaTest = ((BetaTest) betaTestListAdapterModel.getItem(position));

                            // TODO : 로직 최적화 필요. 플래그 사용 지양!
                            boolean isUpdated = false;
                            if (!originalBetaTest.getCompletedItemCount().equals(newBetaTest.getCompletedItemCount())) {
                                originalBetaTest.setCompletedItemCount(newBetaTest.getCompletedItemCount());
                                isUpdated = true;
                            }

                            if (!originalBetaTest.getTotalItemCount().equals(newBetaTest.getTotalItemCount())) {
                                originalBetaTest.setTotalItemCount(newBetaTest.getTotalItemCount());
                                isUpdated = true;
                            }

                            if (isUpdated) {
                                view.refreshBetaTestProgress(position);
                            }
                        }, e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    private int compareByCloseDate(BetaTest betaTest1, BetaTest betaTest2, Date currentDate) {
        Long betaTestDiff1 = Math.abs(currentDate.getTime() - betaTest1.getCloseDate().getTime());
        Long betaTestDiff2 = Math.abs(currentDate.getTime() - betaTest2.getCloseDate().getTime());

        return betaTestDiff1.compareTo(betaTestDiff2);
    }

    @Override
    public BetaTest getBetaTestItem(int position) {
        return (BetaTest) this.betaTestListAdapterModel.getItem(position);
    }

    @Override
    public int getBetaTestPostitionById(String id) {
        return this.betaTestListAdapterModel.getPositionById(id);
    }

    @Override
    public String getInterpretedUrl(String originalUrl) {
        return this.fomesUrlHelper.interpretUrlParams(originalUrl);
    }

    @Override
    public void sendEventLog(String code, String ref) {
        compositeSubscription.add(
                eventLogService.sendEventLog(new EventLog().setCode(code).setRef(ref))
                        .subscribe(() -> Log.d(TAG, "Event log is sent successfully!!"),
                                (e) -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }
}