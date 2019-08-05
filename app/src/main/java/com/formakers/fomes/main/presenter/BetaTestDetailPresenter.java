package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.FomesUrlHelper;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.dagger.scope.BetaTestDetailActivityScope;

import java.util.Collections;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@BetaTestDetailActivityScope
public class BetaTestDetailPresenter implements BetaTestDetailContract.Presenter {

    private static final String TAG = "BetaTestDetailPresenter";

    private AnalyticsModule.Analytics analytics;
    private BetaTestService betaTestService;
    private EventLogService eventLogService;
    private FomesUrlHelper fomesUrlHelper;

    private BetaTest betaTest;
    private BetaTestDetailContract.View view;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public BetaTestDetailPresenter(BetaTestDetailContract.View view,
                                   AnalyticsModule.Analytics analytics,
                                   EventLogService eventLogService,
                                   BetaTestService betaTestService,
                                   FomesUrlHelper fomesUrlHelper) {
        this.view = view;
        this.analytics = analytics;
        this.betaTestService = betaTestService;
        this.eventLogService = eventLogService;
        this.fomesUrlHelper = fomesUrlHelper;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return null;
    }

    @Override
    public void sendEventLog(String code, String ref) {
        compositeSubscription.add(
                eventLogService.sendEventLog(new EventLog().setCode(code).setRef(ref))
                        .subscribe(() -> { }, e -> Log.e(TAG, String.valueOf(e)))
        );
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
                        .map(betaTest -> {
                            // 진행상태 체크 (progress)
                            int total = 0;
                            int completed = 0;

                            for (Mission mission : betaTest.getMissions()) {
                                total += mission.getItems().size();

                                for (Mission.MissionItem missionItem : mission.getItems()) {
                                    if (missionItem.isCompleted()) {
                                        completed++;
                                    }
                                }
                            }

                            betaTest.setTotalItemCount(total);
                            betaTest.setCompletedItemCount(completed);

                            // 정렬 (order)
                            Collections.sort(betaTest.getRewards().getList(), (o1, o2) -> o1.getOrder() - o2.getOrder());
                            Collections.sort(betaTest.getMissions(), (o1, o2) -> o1.getOrder() - o2.getOrder());

                            for (Mission mission : betaTest.getMissions()) {
                                Collections.sort(mission.getItems(), ((o1, o2) -> o1.getOrder() - o2.getOrder()));
                            }

                            return betaTest;
                        })
                        .subscribe(betaTest -> this.view.bind(betaTest), e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public void requestCompleteMissionItem(String missionItemId) {
        compositeSubscription.add(
                this.betaTestService.postCompleteBetaTest(missionItemId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> this.view.unlockMissions(), e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public Observable<Mission.MissionItem> refreshMissionProgress(String missionId) {
        return this.betaTestService.getMissionProgress(missionId);
    }

    @Override
    public String getInterpretedUrl(String originalUrl) {
        return fomesUrlHelper.interpretUrlParams(originalUrl);
    }
}
