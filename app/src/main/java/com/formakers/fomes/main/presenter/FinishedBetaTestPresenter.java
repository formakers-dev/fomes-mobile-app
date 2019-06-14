package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.FinishedBetaTestContract;
import com.formakers.fomes.main.contract.FinishedBetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.scope.FinishedBetaTestFragmentScope;

import java.util.List;

import javax.inject.Inject;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@FinishedBetaTestFragmentScope
public class FinishedBetaTestPresenter implements FinishedBetaTestContract.Presenter {

    public static String TAG = "FinishedBetaTestPresenter";

    private BetaTestService betaTestService;
    private EventLogService eventLogService;
    private AnalyticsModule.Analytics analytics;

    private FinishedBetaTestListAdapterContract.Model adapterModel;
    private FinishedBetaTestContract.View view;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public FinishedBetaTestPresenter(FinishedBetaTestContract.View view, BetaTestService betaTestService, EventLogService eventLogService, UserDAO userDAO, AnalyticsModule.Analytics analytics) {
        this.view = view;
        this.betaTestService = betaTestService;
        this.eventLogService = eventLogService;
        this.analytics = analytics;
    }

    @Override
    public void setAdapterModel(FinishedBetaTestListAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return this.analytics;
    }

    @Override
    public void initialize() {
        compositeSubscription.add(load()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> view.showLoading())
                .doAfterTerminate(() -> view.hideLoading())
                .toCompletable()
                .subscribe(() -> { }, e -> Log.e(TAG, String.valueOf(e))));
    }

    @Override
    public Single<List<BetaTest>> load() {
        return betaTestService.getFinishedBetaTestList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(betaTests -> {
                    if (betaTests == null || betaTests.isEmpty()) {
                        throw new IllegalStateException("Empty List");
                    }

                    adapterModel.clear();
                    adapterModel.addAll(betaTests);

                    view.refresh();
                    view.showListView();

                    Log.v(TAG, String.valueOf(betaTests));
                })
                .doOnError(e -> {
                    Log.e(TAG, "doOnError e=" + e);
                    view.showEmptyView();
                });
    }

    @Override
    public BetaTest getItem(int position) {
        return (BetaTest) this.adapterModel.getItem(position);
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
