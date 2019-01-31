package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.scope.BetaTestFragmentScope;
import com.formakers.fomes.model.User;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@BetaTestFragmentScope
public class BetaTestPresenter implements BetaTestContract.Presenter {

    public static String TAG = "BetaTestPresenter";

    private BetaTestListAdapterContract.Model betaTestListAdapterModel;

    private BetaTestContract.View view;
    private BetaTestService betaTestService;
    private UserDAO userDAO;
    private User user;
    private EventLogService eventLogService;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public BetaTestPresenter(BetaTestContract.View view, BetaTestService betaTestService, EventLogService eventLogService, UserDAO userDAO) {
        this.view = view;
        this.betaTestService = betaTestService;
        this.eventLogService = eventLogService;
        this.userDAO = userDAO;
    }

    @Override
    public void setAdapterModel(BetaTestListAdapterContract.Model adapterModel) {
        this.betaTestListAdapterModel = adapterModel;
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
                .flatMap(user -> loadToBetaTestList())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> view.showLoading())
                .doAfterTerminate(() -> view.hideLoading())
                .toCompletable().subscribe(() -> { }, e -> Log.e(TAG, String.valueOf(e))));
    }

    @Override
    public Single<List<BetaTest>> loadToBetaTestList() {
        return betaTestService.getBetaTestList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(betaTests -> {

                    if (betaTests == null || betaTests.isEmpty()) {
                        throw new IllegalStateException("Empty List");
                    }

                    // TODO : 정렬 로직이 프레젠터에 있는게 맞을까? 고민되네... Service로 이동 필요
                    Collections.sort(betaTests, (betaTest1, betaTest2) -> {
                        int compareWithIsOpened = Boolean.compare(betaTest1.isOpened(), betaTest2.isOpened());
                        if (compareWithIsOpened != 0)
                            return -1 * compareWithIsOpened;

                        int compareWithIsCompleted = Boolean.compare(betaTest1.isCompleted(), betaTest2.isCompleted());
                        if (compareWithIsCompleted != 0)
                            return (betaTest1.isOpened() ? 1 : -1) * compareWithIsCompleted;

                        return betaTest1.getCloseDate().compareTo(betaTest2.getCloseDate());
                    });

                    betaTestListAdapterModel.clear();
                    betaTestListAdapterModel.addAll(betaTests);
                    view.refreshBetaTestList();
                    view.showBetaTestListView();
                })
                .doOnError(e -> view.showEmptyView());
    }

    @Override
    public BetaTest getBetaTestItem(int position) {
        return (BetaTest) this.betaTestListAdapterModel.getItem(position);
    }

    @Override
    public String getUserEmail() {
        return this.user.getEmail();
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
