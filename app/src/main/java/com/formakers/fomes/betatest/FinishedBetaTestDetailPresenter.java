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

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@FinishedBetaTestDetailDagger.Scope
class FinishedBetaTestDetailPresenter implements FinishedBetaTestDetailContract.Presenter {

    private static final String TAG = "FinishedBetaTestDetailPresenter";

    private FinishedBetaTestDetailContract.View view;
    private AnalyticsModule.Analytics analytics;
    private ImageLoader imageLoader;
    private BetaTestService betaTestService;
    private FomesUrlHelper fomesUrlHelper;
    private AndroidNativeHelper androidNativeHelper;
    private FinishedBetaTestAwardPagerAdapterContract.Model finishedBetaTestAwardPagerModel;

    @Inject
    public FinishedBetaTestDetailPresenter(FinishedBetaTestDetailContract.View view,
                                           AnalyticsModule.Analytics analytics,
                                           ImageLoader imageLoader,
                                           BetaTestService betaTestService,
                                           FomesUrlHelper fomesUrlHelper,
                                           AndroidNativeHelper androidNativeHelper) {
        this.view = view;
        this.analytics = analytics;
        this.imageLoader = imageLoader;
        this.betaTestService = betaTestService;
        this.fomesUrlHelper = fomesUrlHelper;
        this.androidNativeHelper = androidNativeHelper;
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
    public void requestAwardRecords(String betaTestId) {
        this.betaTestService.getAwardRecords(betaTestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(awardRecords -> {
                    this.finishedBetaTestAwardPagerModel.addAll(awardRecords);
                    this.view.refreshAwardPagerView();
                }, e -> {
                    Log.e(TAG, "requestAwardRecordOfBest) " + e);
                    if (e instanceof NoSuchElementException) {
                        this.view.hideAwardsView();
                    }
                });
    }

    @Override
    public void requestEpilogueAndAwards(String betaTestId) {
        this.betaTestService.getEpilogue(betaTestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(epilogue -> {
                    this.view.bindEpilogueView(epilogue);
                    requestAwardRecords(betaTestId);
                }, e -> {
                    Log.e(TAG, String.valueOf(e));
                    if (e instanceof HttpException) {
                        HttpException httpException = (HttpException) e;
                        if (httpException.code() == 404) {
                            this.view.disableEpilogueView();
                            this.view.bindAwardRecordsWithRewardItems();
                        }
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
