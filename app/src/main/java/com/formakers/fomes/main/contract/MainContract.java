package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.model.User;

import rx.Completable;
import rx.Single;

public interface MainContract {
    interface Presenter {
        Single<User> requestUserInfo();
        Completable requestVerifyAccessToken();
        void startEventBannerAutoSlide();
        void stopEventBannerAutoSlide();
        boolean checkRegisteredSendDataJob();
        void sendEventLog(String code);
        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
        void showNextEventBanner();
    }
}
