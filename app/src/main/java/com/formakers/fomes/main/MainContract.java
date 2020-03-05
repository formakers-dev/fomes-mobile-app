package com.formakers.fomes.main;

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

        void bindUserInfo();
        Completable requestVerifyAccessToken();
        void sendEventLog(String code);
        void requestPromotions();
        int getPromotionCount();
        String getInterpretedUrl(String originalUrl);

        int registerSendDataJob();
        boolean checkRegisteredSendDataJob();

        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void setUserInfoToNavigationView(String email, String nickName);
        void refreshEventPager();
    }
}
