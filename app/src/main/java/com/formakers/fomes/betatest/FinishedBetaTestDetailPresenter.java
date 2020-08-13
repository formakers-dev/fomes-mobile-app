package com.formakers.fomes.betatest;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@FinishedBetaTestDetailDagger.Scope
class FinishedBetaTestDetailPresenter implements FinishedBetaTestDetailContract.Presenter {

    private static final String TAG = "FinishedBetaTestDetailPresenter";

    private FinishedBetaTestDetailContract.View view;
    private AnalyticsModule.Analytics analytics;
    private ImageLoader imageLoader;
    private BetaTestService betaTestService;
    private FomesUrlHelper fomesUrlHelper;
    private AndroidNativeHelper androidNativeHelper;
    private FirebaseRemoteConfig remoteConfig;

    private BetaTest betaTest;
    private FinishedBetaTestAwardPagerAdapterContract.Model finishedBetaTestAwardPagerModel;

    @Inject
    public FinishedBetaTestDetailPresenter(FinishedBetaTestDetailContract.View view,
                                           AnalyticsModule.Analytics analytics,
                                           ImageLoader imageLoader,
                                           BetaTestService betaTestService,
                                           FomesUrlHelper fomesUrlHelper,
                                           AndroidNativeHelper androidNativeHelper,
                                           FirebaseRemoteConfig remoteConfig) {
        this.view = view;
        this.analytics = analytics;
        this.imageLoader = imageLoader;
        this.betaTestService = betaTestService;
        this.fomesUrlHelper = fomesUrlHelper;
        this.androidNativeHelper = androidNativeHelper;
        this.remoteConfig = remoteConfig;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return analytics;
    }

    @Override
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void setFinishedBetaTestAwardPagerAdapterModel(FinishedBetaTestAwardPagerAdapterContract.Model model) {
        this.finishedBetaTestAwardPagerModel = model;
    }

    @Override
    public void setBetaTest(BetaTest betaTest) {
        this.betaTest = betaTest;
    }

    @Override
    public void requestEpilogueAndAwards(String betaTestId) {
        this.betaTestService.getEpilogue(betaTestId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(epilogue -> this.view.bindEpilogueView(epilogue))
                .observeOn(Schedulers.io())
                .flatMap(epilogue -> this.betaTestService.getAwardRecords(betaTestId))
                .doOnSuccess(awardRecords -> this.finishedBetaTestAwardPagerModel.addAll(awardRecords))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(awardRecords -> this.view.refreshAwardPagerView(),
                        e -> {
                            Log.e(TAG, String.valueOf(e));

                            if (e instanceof HttpException && ((HttpException) e).code() == 404) {
                                this.view.disableEpilogueView();
                                this.view.bindAwardRecordsWithRewardItems(betaTest.getRewards().getList());
                            }

                            if (e instanceof NoSuchElementException ||
                                    betaTest.getRewards().getList() == null
                                    || betaTest.getRewards().getList().size() <= 0) {
                                this.view.hideAwardsView();
                            }
                        });
    }

    @Override
    public void requestRecheckableMissions(String betaTestId) {
        this.betaTestService.getCompletedMissions(betaTestId)
                .toObservable()
                .concatMap(Observable::from)
                .filter(Mission::isRecheckable)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(missions -> {
                    Log.i(TAG, String.valueOf(missions));
                    if (missions != null && missions.size() > 0) {
                        this.view.bindMyAnswersView(missions);
                    }
                }, e -> Log.e(TAG, "getRecheckableMissions) " + e));
    }

    @Override
    public void emitRecheckMyAnswer(Mission mission) {
        this.view.showNoticePopupView(R.string.finished_betatest_recheck_my_answer_title,
                R.string.finished_betatest_recheck_my_answer_popup_subtitle,
                R.drawable.notice_recheck_my_answer,
                R.string.finished_betatest_recheck_my_answer_popup_positive_button_text,
                v -> processMissionItemAction(mission));
    }

    @Override
    public boolean isActivatedPointSystem() {
        return this.remoteConfig.getBoolean(FomesConstants.RemoteConfig.FEATURE_POINT_SYSTEM);
    }

    private void processMissionItemAction(Mission missionItem) {
        // TODO : [중복코드] BetaTestHelper 등과 같은 로직으로 공통화 시킬 필요 있음
        String action = missionItem.getAction();

        if (TextUtils.isEmpty(action)) {
            return;
        }

        String url = fomesUrlHelper.interpretUrlParams(action);
        Uri uri = Uri.parse(url);

        if (FomesConstants.BetaTest.Mission.TYPE_PLAY.equals(missionItem.getType())) {
            Intent intent = this.androidNativeHelper.getLaunchableIntent(missionItem.getPackageName());

            if (intent != null) {
                view.startActivity(intent);
                return;
            }
        }

        // below condition logic should be move to URL Manager(or Parser and so on..)
        if (FomesConstants.BetaTest.Mission.ACTION_TYPE_INTERNAL_WEB.equals(missionItem.getActionType())
                || (uri.getQueryParameter("internal_web") != null
                && uri.getQueryParameter("internal_web").equals("true"))) {
            view.startWebViewActivity(missionItem.getTitle(), url);
        } else {
            // Default가 딥링크인게 좋을 것 같음... 여러가지 방향으로 구현가능하니까
            view.startByDeeplink(Uri.parse(url));
        }
    }
}
