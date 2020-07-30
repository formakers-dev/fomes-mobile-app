package com.formakers.fomes.betatest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.formakers.fomes.common.constant.Feature;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@BetaTestDetailDagger.Scope
public class BetaTestDetailPresenter implements BetaTestDetailContract.Presenter {

    private static final String TAG = "BetaTestDetailPresenter";

    private AnalyticsModule.Analytics analytics;
    private BetaTestService betaTestService;
    private EventLogService eventLogService;
    private FomesUrlHelper fomesUrlHelper;
    private AndroidNativeHelper androidNativeHelper;
    private AppUsageDataHelper appUsageDataHelper;
    private ImageLoader imageLoader;

    private BetaTestDetailContract.View view;

    BetaTest betaTest;

    @Inject
    public BetaTestDetailPresenter(BetaTestDetailContract.View view,
                                   AnalyticsModule.Analytics analytics,
                                   EventLogService eventLogService,
                                   BetaTestService betaTestService,
                                   FomesUrlHelper fomesUrlHelper,
                                   AndroidNativeHelper androidNativeHelper,
                                   AppUsageDataHelper appUsageDataHelper,
                                   ImageLoader imageLoader) {
        this.view = view;
        this.analytics = analytics;
        this.betaTestService = betaTestService;
        this.eventLogService = eventLogService;
        this.fomesUrlHelper = fomesUrlHelper;
        this.androidNativeHelper = androidNativeHelper;
        this.appUsageDataHelper = appUsageDataHelper;
        this.imageLoader = imageLoader;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return null;
    }

    @Override
    public ImageLoader getImageLoader() {
        return this.imageLoader;
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
//                            // 진행상태 체크 (progress)
//                            int total = betaTest.getMissions().size();
//                            int completed = 0;
//
//                            for (Mission mission : betaTest.getMissions()) {
//                                if (mission.isCompleted()) {
//                                     completed++;
//                                }
//                            }
//
//                            betaTest.setTotalItemCount(total);
//                            betaTest.setCompletedItemCount(completed);

                            // 정렬 (order)
                            Collections.sort(betaTest.getRewards().getList(), (o1, o2) -> o1.getOrder() - o2.getOrder());
                            Collections.sort(betaTest.getMissions(), (o1, o2) -> {
                                if (o1.getOrder().equals(o2.getOrder())) {
                                    return o1.getOrder() - o2.getOrder();
                                } else {
                                    return o1.getOrder() - o2.getOrder();
                                }
                            });

                            return betaTest;
                        })
                        .subscribe(betaTest -> {
                            this.betaTest = betaTest;
                            this.view.bind(betaTest);
                        }, e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public Single<Mission> getMissionProgress(String missionId) {
        if (this.betaTest == null) {
            return Single.error(new IllegalArgumentException("BetaTest is null"));
        }

        return this.betaTestService.getMissionProgress(betaTest.getId(), missionId);
    }

    @Override
    public void processMissionItemAction(Mission mission) {
        // TODO : [중복코드] BetaTestHelper 등과 같은 로직으로 공통화 시킬 필요 있음
        String action = mission.getAction();

        if (TextUtils.isEmpty(action)) {
            return;
        }

        Bundle params = new Bundle();
        params.putString(FomesUrlHelper.EXTRA_BETA_TEST_ID, betaTest.getId());
        params.putString(FomesUrlHelper.EXTRA_MISSION_ID, mission.getId());

        String url = getInterpretedUrl(action, params);

        if (FomesConstants.BetaTest.Mission.TYPE_INSTALL.equals(mission.getType())) {
            Intent intent = this.getIntentIfAppIsInstalled(mission.getPackageName());

            if (intent != null) {
                view.startActivity(intent);
                return;
            }
        }

        if (FomesConstants.BetaTest.Mission.TYPE_PLAY.equals(mission.getType())) {
            this.updatePlayTime(mission.getId(), mission.getPackageName())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable(playTime -> {
                        this.view.showToast(DateUtil.convertDurationToString(playTime));
                        return requestToCompleteMission(mission);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> this.view.refreshMission(mission.getId()),
                            e -> {
                                Log.e(TAG, String.valueOf(e));
                                this.view.showToast("플레이 시간이 측정되지 않아요!");
                            });
        } else {
            // below condition logic should be move to URL Manager(or Parser and so on..)
            if (FomesConstants.BetaTest.Mission.ACTION_TYPE_INTERNAL_WEB.equals(mission.getActionType())) {
                view.startSurveyWebViewActivity(mission.getId(), mission.getTitle(), url);
            } else {
                // Default가 딥링크인게 좋을 것 같음... 여러가지 방향으로 구현가능하니까
                view.startByDeeplink(Uri.parse(url));
            }
        }
    }

    @Nullable
    @Override
    public Intent getIntentIfAppIsInstalled(String pacakgeName) {
        return this.androidNativeHelper.getLaunchableIntent(pacakgeName);
    }

    @Override
    public String getInterpretedUrl(String originalUrl, Bundle params) {
        return fomesUrlHelper.interpretUrlParams(originalUrl, params);
    }

    @Override
    public Observable<List<Mission>> getDisplayedMissionList() {
        return getMissionListWithLockingSequence().toList();
    }

    @Override
    public void requestToAttendBetaTest() {
        Log.d(TAG, "requestToAttendBetaTest");
        view.getCompositeSubscription().add(
                betaTestService.postAttendBetaTest(this.betaTest.getId())
                        .observeOn(AndroidSchedulers.mainThread())
//                        .concatWith(completePlayTypeMission())
                        .subscribe(() -> {
                            this.betaTest.setAttended(true);
                            this.view.refreshMissionList();
                        }, e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public Completable requestToCompleteMission(Mission mission) {
        return betaTestService.postCompleteMission(this.betaTest.getId(), mission.getId())
                .doOnCompleted(() -> {
                    mission.setCompleted(true);
                    this.view.refreshMission(mission.getId());
                });
    }

    private Completable completePlayTypeMission() {
        List<Completable> missions = Observable.from(this.betaTest.getMissions())
                .filter(mission -> FomesConstants.BetaTest.Mission.TYPE_PLAY.equals(mission.getType()))
                .map(mission -> betaTestService.postCompleteMission(this.betaTest.getId(), mission.getId())
                        .doOnCompleted(() -> mission.setCompleted(true)))
                .toList().toBlocking().single();

        return Completable.concat(missions);
    }

    @Override
    public Single<Long> updatePlayTime(@NonNull String missionItemId, @NonNull String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            return Feature.CALCULATE_PLAY_TIME ? getPlayTimeAndRefreshMissionView(missionItemId, packageName)
                    : getPlayTime(packageName);
        } else {
            return Single.error(new IllegalArgumentException("packageName is null"));
        }
    }

    private Single<Long> getPlayTime(@NonNull String packageName) {
        return appUsageDataHelper.getUsageTime(packageName, betaTest.getOpenDate().getTime())
                .map(playTime -> {
                    if (playTime > 0) {
                        return playTime;
                    } else {
                        throw new IllegalStateException("playtime is under than 0");
                    }
                })
                .toSingle();
    }

    private Single<Long> getPlayTimeAndRefreshMissionView(@NonNull String missionId, @NonNull String packageName) {
        Single<Mission> findMissionSingle = Observable.from(betaTest.getMissions())
                .filter(mission -> missionId.equals(mission.getId()))
                .toSingle();

        return this.getPlayTime(packageName)
                .zipWith(findMissionSingle, Pair::new)
                .map(pair -> {
                    pair.second.setTotalPlayTime(pair.first);
                    return pair.first;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(playTime -> this.view.refreshMission(missionId));
    }

    private Observable<Mission> getMissionListWithLockingSequence() {
        // 각 미션들의 lock상태를 이전 미션의 필수와 완료 상태에 따라 업데이트
        // 첫 번째 미션인 경우 hidden이나 play 미션아이템의 완료여부에 따라 lock상태 업데이트

        return Observable.from(betaTest.getMissions())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .reduce(new ArrayList<Mission>(), (reducedMissionList, mission) -> {
                    if (reducedMissionList.size() > 0) {
                        Mission lastMission = reducedMissionList.get(reducedMissionList.size() - 1);
                        mission.setLocked(lastMission.isBlockedNextMission());
                    }

                    reducedMissionList.add(mission);

                    return reducedMissionList;
                })
                .flatMap(reducedMissionList -> {
                    // 첫 번째 미션 락에 대한 예외처리
                    if (betaTest.isAttended()) {
                        reducedMissionList.get(0).setLocked(false);
                    }

                    // 가장 먼저 발견된 락 이후로는 모두 락으로 셋팅
                    int i;
                    for (i = 0; i < reducedMissionList.size(); ++i) {
                        if (reducedMissionList.get(i).isLocked()) {
                            break;
                        }
                    }

                    for (; i < reducedMissionList.size(); i++) {
                        reducedMissionList.get(i).setLocked(true);
                    }

                    return Observable.from(reducedMissionList);
                });
    }
}
