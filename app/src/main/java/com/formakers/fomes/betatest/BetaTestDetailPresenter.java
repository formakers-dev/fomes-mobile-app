package com.formakers.fomes.betatest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

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

    public static final int PLAY_TIME_ERROR_COUNT_LIMIT = 2;

    private AnalyticsModule.Analytics analytics;
    private BetaTestService betaTestService;
    private EventLogService eventLogService;
    private FomesUrlHelper fomesUrlHelper;
    private AndroidNativeHelper androidNativeHelper;
    private AppUsageDataHelper appUsageDataHelper;
    private ImageLoader imageLoader;
    private FirebaseRemoteConfig remoteConfig;

    private BetaTestDetailContract.View view;
    private MissionListAdapterContract.Model missionListAdapterModel;

    BetaTest betaTest;
    private int playTimeErrorCount = 0;

    @Inject
    public BetaTestDetailPresenter(BetaTestDetailContract.View view,
                                   AnalyticsModule.Analytics analytics,
                                   EventLogService eventLogService,
                                   BetaTestService betaTestService,
                                   FomesUrlHelper fomesUrlHelper,
                                   AndroidNativeHelper androidNativeHelper,
                                   AppUsageDataHelper appUsageDataHelper,
                                   ImageLoader imageLoader,
                                   FirebaseRemoteConfig remoteConfig) {
        this.view = view;
        this.analytics = analytics;
        this.betaTestService = betaTestService;
        this.eventLogService = eventLogService;
        this.fomesUrlHelper = fomesUrlHelper;
        this.androidNativeHelper = androidNativeHelper;
        this.appUsageDataHelper = appUsageDataHelper;
        this.imageLoader = imageLoader;
        this.remoteConfig = remoteConfig;
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
    public void setAdapterModel(MissionListAdapterContract.Model adapterModel) {
        this.missionListAdapterModel = adapterModel;
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
//                            // ???????????? ?????? (progress)
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

                            // ?????? (order)
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
    public void displayMissionList() {
        this.getDisplayedMissionList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(missionList -> {
                    missionListAdapterModel.clear();
                    missionListAdapterModel.addAll(missionList);
                    this.view.refreshMissionList();
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void displayMission(String missionId) {
        this.getDisplayedMissionList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(missionList -> {
                    missionListAdapterModel.clear();
                    missionListAdapterModel.addAll(missionList);
                    this.view.refreshMissionBelowAllChanged(missionListAdapterModel.getPositionById(missionId));
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public boolean isPlaytimeFeatureEnabled() {
        return true;
//        return this.remoteConfig.getBoolean(FomesConstants.RemoteConfig.FEATURE_CALCULATE_PLAYTIME);
    }

    @Override
    public void updateMissionProgress(String missionId) {
        if (this.betaTest == null) {
            return;
        }

        this.betaTestService.getMissionProgress(betaTest.getId(), missionId)
                .observeOn(Schedulers.io())
                .doOnSuccess(mission -> this.missionListAdapterModel.getItemById(missionId).setCompleted(mission.isCompleted()))
                .flatMapObservable(mission -> this.getDisplayedMissionList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(displayedMissionList -> {
                    this.missionListAdapterModel.clear();
                    this.missionListAdapterModel.addAll(displayedMissionList);
                    this.view.refreshMissionBelowAllChanged(this.missionListAdapterModel.getPositionById(missionId));
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void processMissionItemAction(Mission mission) {
        // TODO : [????????????] BetaTestHelper ?????? ?????? ???????????? ????????? ?????? ?????? ??????
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

        if (this.isPlaytimeFeatureEnabled()
                && FomesConstants.BetaTest.Mission.TYPE_PLAY.equals(mission.getType())) {
            this.updatePlayTime(mission.getId(), mission.getPackageName())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable(playTime -> {
                        this.view.showPlayTimeSuccessPopup(DateUtil.convertDurationToString(playTime));
                        return requestToCompleteMission(mission);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(x -> this.missionListAdapterModel.setLoading(mission.getId(), true))
                    .doOnTerminate(() -> this.missionListAdapterModel.setLoading(mission.getId(), false))
                    .subscribe(() -> this.displayMission(mission.getId()),
                            e -> {
                                Log.e(TAG, String.valueOf(e));
                                if (this.isLimitPlayTimeErrorCount()) {
                                    this.view.showPlayTimeErrorPopup(mission.getId(), mission.getTitle(), url);
                                } else {
                                    this.increasePlayTimeErrorCount();
                                    this.view.showPlayTimeZeroPopup();
                                }
                            });
            return;
        }


        // below condition logic should be move to URL Manager(or Parser and so on..)
        if (FomesConstants.BetaTest.Mission.ACTION_TYPE_INTERNAL_WEB.equals(mission.getActionType())) {
            view.startSurveyWebViewActivity(mission.getId(), mission.getTitle(), url);
        } else {
            // Default??? ??????????????? ?????? ??? ??????... ???????????? ???????????? ?????????????????????
            view.startByDeeplink(Uri.parse(url));
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
                            this.displayMissionList();
                        }, e -> Log.e(TAG, String.valueOf(e)))
        );
    }

    @Override
    public Completable requestToCompleteMission(Mission mission) {
        return betaTestService.postCompleteMission(this.betaTest.getId(), mission.getId())
                .doOnCompleted(() -> {
                    mission.setCompleted(true);
                    this.displayMission(mission.getId());
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
//            return Feature.CALCULATE_PLAY_TIME_VIEW ? getPlayTimeAndRefreshMissionView(missionItemId, packageName) : getPlayTime(packageName);
            return getPlayTime(packageName);
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
                        throw new IllegalStateException("playtime is or less than 0");
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
                .doOnSuccess(playTime -> this.displayMission(missionId));
    }

    private Observable<Mission> getMissionListWithLockingSequence() {
        // ??? ???????????? lock????????? ?????? ????????? ????????? ?????? ????????? ?????? ????????????
        // ??? ?????? ????????? ?????? hidden?????? play ?????????????????? ??????????????? ?????? lock?????? ????????????

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
                    // ??? ?????? ?????? ?????? ?????? ????????????
                    if (betaTest.isAttended()) {
                        reducedMissionList.get(0).setLocked(false);
                    }

                    // ?????? ?????? ????????? ??? ???????????? ?????? ????????? ??????
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

    @Override
    public void increasePlayTimeErrorCount() {
        this.playTimeErrorCount++;
    }

    @Override
    public boolean isLimitPlayTimeErrorCount() {
        return this.playTimeErrorCount >= PLAY_TIME_ERROR_COUNT_LIMIT;
    }

    @Override
    public void initPlayTimeErrorCount() {
        this.playTimeErrorCount = 0;
    }
}
