package com.formakers.fomes.betatest;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;

import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.subscriptions.CompositeSubscription;

public interface BetaTestDetailContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();

        void setAdapterModel(MissionListAdapterContract.Model adapterModel);

        void sendEventLog(String code, String ref);

        void load(String id);

        Single<Mission> getMissionProgress(String missionId);
        void updateMissionProgress(String missionId);

        void processMissionItemAction(Mission missionItem);
        String getInterpretedUrl(String originalUrl, Bundle params);

        Observable<List<Mission>> getDisplayedMissionList();
        void requestToAttendBetaTest();
        Completable requestToCompleteMission(Mission mission);

        Single<Long> updatePlayTime(@NonNull String missionItemId, @NonNull String packageName);
        Intent getIntentIfAppIsInstalled(String packageName);

        void displayMissionList();
        void displayMission(String missionId);

        boolean isPlaytimeFeatureEnabled();

        void increasePlayTimeErrorCount();
        boolean isLimitPlayTimeErrorCount();
        void initPlayTimeErrorCount();
    }

    interface View extends BaseView<Presenter> {
        void bind(BetaTest betaTest);
        void showLoading();
        void hideLoading();
        void showToast(String message);
        void refreshMissionList();
        void refreshMissionBelowAllChanged(int missionPosition);

        android.view.View inflate(@LayoutRes int layoutResId);
        void startSurveyWebViewActivity(String missionId, String title, String url);
        void startByDeeplink(Uri deeplinkUri);
        void startActivity(Intent intent);

        // TODO : BaseView 로 이동 고려
        CompositeSubscription getCompositeSubscription();

        void showPlayTimeZeroPopup();
        void showPlayTimeErrorPopup(String missionId, String title, String url);
    }
}
