package com.formakers.fomes.main;

import android.os.Bundle;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;

import rx.Completable;

public interface MainContract {
    interface Presenter {
        // Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();

        void setEventPagerAdapterModel(EventPagerAdapterContract.Model eventPagerAdapterModel);

        Completable requestVerifyAccessToken();
        void sendEventLog(String code);
        void requestPromotions();
        int getPromotionCount();
        String getInterpretedUrl(String originalUrl);

        int registerSendDataJob();
        boolean checkRegisteredSendDataJob();
        void checkNeedToShowMigrationDialog();

        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void refreshEventPager();
        void showMigrationNoticeDialog(Bundle migrationNoticeDialogBundle, android.view.View.OnClickListener onClickListener);
        void moveToPlayStore();
    }
}
