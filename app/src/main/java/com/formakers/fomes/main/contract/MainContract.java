package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.model.User;

import rx.Completable;
import rx.Single;

public interface MainContract {
    interface Presenter {
        AnalyticsModule.Analytics getAnalytics();

        void setAdapterModel(EventPagerAdapterContract.Model adapterModel);

        Single<User> requestUserInfo();
        Completable requestVerifyAccessToken();
        void sendEventLog(String code);
        void requestPromotions();
        int getPromotionCount();

        int registerSendDataJob();
        boolean checkRegisteredSendDataJob();

        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
        void refreshEventPager();
    }
}
