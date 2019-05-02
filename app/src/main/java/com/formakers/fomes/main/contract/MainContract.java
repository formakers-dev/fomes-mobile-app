package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.model.User;

import java.util.List;

import rx.Completable;
import rx.Single;

public interface MainContract {
    interface Presenter {
        Single<User> requestUserInfo();
        Completable requestVerifyAccessToken();
        void sendEventLog(String code);
        void requestPromotions();

        int registerSendDataJob();
        boolean checkRegisteredSendDataJob();

        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
        void setPromotionViews(List<Post> promotions);
    }
}
