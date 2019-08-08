package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.model.User;

import rx.Completable;

public interface MainContract {
    interface Presenter {
        AnalyticsModule.Analytics getAnalytics();

        void setEventPagerAdapterModel(EventPagerAdapterContract.Model eventPagerAdapterModel);

        User getUserInfo();
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
        ApplicationComponent getApplicationComponent();
        void refreshEventPager();
    }
}
