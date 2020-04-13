package com.formakers.fomes.betatest;


import android.content.Intent;
import android.net.Uri;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;

import java.util.List;

import rx.Observable;
import rx.Single;
import rx.subscriptions.CompositeSubscription;

public interface BetaTestDetailContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();

        void sendEventLog(String code, String ref);

        void load(String id);

        Single<Mission> refreshMissionProgress(String missionId);

        void processMissionItemAction(Mission missionItem);
        String getInterpretedUrl(String originalUrl);

        Observable<List<Mission>> getDisplayedMissionList();
        void requestToAttendBetaTest();

        Single<Long> updatePlayTime(@NonNull String missionItemId, @NonNull String packageName);
    }

    interface View extends BaseView<Presenter> {
        void bind(BetaTest betaTest);
        void showLoading();
        void hideLoading();
        void refreshMissionList();
        void refreshMissionItem(String missionItemId);

        android.view.View inflate(@LayoutRes int layoutResId);
        void startSurveyWebViewActivity(String missionId, String title, String url);
        void startByDeeplink(Uri deeplinkUri);
        void startActivity(Intent intent);

        // TODO : BaseView 로 이동 고려
        CompositeSubscription getCompositeSubscription();
    }
}
