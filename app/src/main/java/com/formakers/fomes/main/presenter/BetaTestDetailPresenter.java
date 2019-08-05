package com.formakers.fomes.main.presenter;

import android.net.Uri;
import android.text.TextUtils;

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

@BetaTestDetailActivityScope
public class BetaTestDetailPresenter implements BetaTestDetailContract.Presenter {

    private static final String TAG = "BetaTestDetailPresenter";

    private AnalyticsModule.Analytics analytics;
    private BetaTestService betaTestService;
    private EventLogService eventLogService;
    private FomesUrlHelper fomesUrlHelper;

    private BetaTestDetailContract.View view;

    private BetaTest betaTest;

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
        view.getCompositeSubscription().add(
                eventLogService.sendEventLog(new EventLog().setCode(code).setRef(ref))
                        .subscribe(() -> { }, e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public void load(String id) {
        view.getCompositeSubscription().add(
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
        view.getCompositeSubscription().add(
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
    public void processMissionItemAction(Mission.MissionItem missionItem) {
        String action = missionItem.getAction();

        if (TextUtils.isEmpty(action)) {
            return;
        }

        String url = getInterpretedUrl(action);
        Uri uri = Uri.parse(url);

        // below condition logic should be move to URL Manager(or Parser and so on..)
        if ("internal_web".equals(missionItem.getActionType())
                || (uri.getQueryParameter("internal_web") != null
                        && uri.getQueryParameter("internal_web").equals("true"))) {
            view.startWebViewActivity(missionItem.getTitle(), url);
        } else {
            // Default가 딥링크인게 좋을 것 같음... 여러가지 방향으로 구현가능하니까
            view.startByDeeplink(Uri.parse(url));
        }
    }

    @Override
    public String getInterpretedUrl(String originalUrl) {
        return fomesUrlHelper.interpretUrlParams(originalUrl);
    }
}
