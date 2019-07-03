package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.dagger.scope.BetaTestDetailActivityScope;

import java.util.Collections;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@BetaTestDetailActivityScope
public class BetaTestDetailPresenter implements BetaTestDetailContract.Presenter {

    private static final String TAG = "BetaTestDetailPresenter";

    private AnalyticsModule.Analytics analytics;
    private BetaTestService betaTestService;

    private BetaTest betaTest;
    private BetaTestDetailContract.View view;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public BetaTestDetailPresenter(BetaTestDetailContract.View view, AnalyticsModule.Analytics analytics, BetaTestService betaTestService) {
        this.view = view;
        this.analytics = analytics;
        this.betaTestService = betaTestService;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return null;
    }

    @Override
    public void sendEventLog(String code, String ref) {

    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void load(String id) {
        compositeSubscription.add(
                this.betaTestService.getDetailBetaTest(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(() -> this.view.showLoading())
                        .doAfterTerminate(() -> this.view.hideLoading())
                        .subscribe(betaTest -> {
                            this.betaTest = betaTest;

                            Collections.sort(betaTest.getRewards().getList(), (o1, o2) -> o1.getOrder() - o2.getOrder());
                            Collections.sort(betaTest.getMissions(), (o1, o2) -> o1.getOrder() - o2.getOrder());

                            for (Mission mission : betaTest.getMissions()) {
                                Collections.sort(mission.getItems(), ((o1, o2) -> o1.getOrder() - o2.getOrder()));
                            }

                            this.view.bind(betaTest);
                        }, e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public void requestCompleteMissionItem(String id) {
        compositeSubscription.add(
                this.betaTestService.postCompleteBetaTest(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((x) -> this.view.unlockMissions(), e -> Log.e(TAG, String.valueOf(e)))
        );
    }
}
