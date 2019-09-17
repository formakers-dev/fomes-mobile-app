package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.FinishedBetaTestContract;
import com.formakers.fomes.main.contract.FinishedBetaTestListAdapterContract;
import com.formakers.fomes.main.dagger.FinishedBetaTestDagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@FinishedBetaTestDagger.Scope
public class FinishedBetaTestPresenter implements FinishedBetaTestContract.Presenter {

    public static String TAG = "FinishedBetaTestPresenter";

    private BetaTestService betaTestService;
    private EventLogService eventLogService;
    private AnalyticsModule.Analytics analytics;

    private FinishedBetaTestListAdapterContract.Model adapterModel;
    private FinishedBetaTestContract.View view;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private List<BetaTest> finishedList = new ArrayList<>();

    @Inject
    public FinishedBetaTestPresenter(FinishedBetaTestContract.View view, BetaTestService betaTestService, EventLogService eventLogService,AnalyticsModule.Analytics analytics) {
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

                    Comparator<BetaTest> comparator = (o1, o2) -> compareByCloseDate(o1, o2, new Date());
                    Collections.sort(betaTests, comparator);

                    finishedList.clear();
                    finishedList.addAll(betaTests);
                    updateDisplayedList(filterCompletedList(finishedList, view.isNeedAppliedCompletedFilter()));

                    Log.v(TAG, "load) onSuccess = " + betaTests);
                })
                .doOnError(e -> {
                    Log.e(TAG, "load) onError e=" + e);
                    view.showEmptyView();
                });
    }

    private int compareByCloseDate(BetaTest betaTest1, BetaTest betaTest2, Date currentDate) {
        Long betaTestDiff1 = Math.abs(currentDate.getTime() - betaTest1.getCloseDate().getTime());
        Long betaTestDiff2 = Math.abs(currentDate.getTime() - betaTest2.getCloseDate().getTime());

        return betaTestDiff1.compareTo(betaTestDiff2);
    }

    @Override
    public void applyCompletedFilter(boolean isNeedFilter) {
        updateDisplayedList(filterCompletedList(finishedList, isNeedFilter));
    }

    private List<BetaTest> filterCompletedList(List<BetaTest> originalList, boolean isFilteredCompleted) {
        if (isFilteredCompleted) {
            return Observable.from(originalList)
                    .observeOn(Schedulers.io())
                    .filter(BetaTest::isCompleted)
                    .toList().toSingle().toBlocking().value();
        } else {
            return originalList;
        }
    }

    private void updateDisplayedList(List<BetaTest> betaTests) {
        adapterModel.clear();
        adapterModel.addAll(betaTests);

        view.refresh();

        if (betaTests.isEmpty()) {
            view.showEmptyView();
        } else {
            view.showListView();
        }
    }

    @Override
    public BetaTest getItem(int position) {
        return (BetaTest) this.adapterModel.getItem(position);
    }

    @Override
    public void sendEventLog(String code, String ref) {
        compositeSubscription.add(
                eventLogService.sendEventLog(new EventLog().setCode(code).setRef(ref))
                        .subscribe(() -> Log.d(TAG, "sendEventLog) onSuccess = Event log is sent successfully!!"),
                                (e) -> Log.e(TAG, "sendEventLog) onError e=" + e))
        );
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }
}
