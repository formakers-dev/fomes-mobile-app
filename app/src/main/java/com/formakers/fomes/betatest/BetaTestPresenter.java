package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.helper.ShareHelper;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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
    private EventLogService eventLogService;
    private AnalyticsModule.Analytics analytics;
    private FomesUrlHelper fomesUrlHelper;
    private ImageLoader imageLoader;
    private Single<String> userNickName;
    private ShareHelper shareHelper;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public BetaTestPresenter(BetaTestContract.View view,
                             BetaTestService betaTestService,
                             EventLogService eventLogService,
                             AnalyticsModule.Analytics analytics,
                             FomesUrlHelper fomesUrlHelper,
                             ImageLoader imageLoader,
                             ShareHelper shareHelper,
                             @Named("userNickName") Single<String> userNickName) {
        this.view = view;
        this.betaTestService = betaTestService;
        this.eventLogService = eventLogService;
        this.userNickName = userNickName;
        this.analytics = analytics;
        this.fomesUrlHelper = fomesUrlHelper;
        this.imageLoader = imageLoader;
        this.shareHelper = shareHelper;
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
    public ImageLoader getImageLoader() {
        return this.imageLoader;
    }

    @Override
    public void initialize() {
        compositeSubscription.add(
            userNickName.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userNickName -> this.view.setUserNickName(userNickName), e -> Log.e(TAG, String.valueOf(e)))
        );

        compositeSubscription.add(
            loadToBetaTestList(new Date())
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
    public void requestBetaTestProgress(final String betaTestId) {
        compositeSubscription.add(
                betaTestService.getBetaTestProgress(betaTestId, false)
                        .observeOn(Schedulers.io())
                        .map(responseVO -> {
                            Log.d(TAG, String.valueOf(responseVO));
                            int position = betaTestListAdapterModel.getPositionById(betaTestId);

                            if (position < 0) {
                                throw new IllegalStateException("There isn't the betatest in list. It might be initialized by system.");
                            }

                            BetaTest originalBetaTest = ((BetaTest) betaTestListAdapterModel.getItem(position));
                            originalBetaTest.setAttended(responseVO.isAttended);
                            originalBetaTest.setCompleted(responseVO.isCompleted);
                            return position;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(position -> view.refreshBetaTestProgress(position), e -> Log.e(TAG, String.valueOf(e)))
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
    public void shareToKaKao(BetaTest betaTest) {
        this.shareHelper.sendBetaTestToKaKao(betaTest);
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }
}
