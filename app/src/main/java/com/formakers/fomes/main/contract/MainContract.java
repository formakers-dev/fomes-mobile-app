package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.model.User;

import rx.Single;

public interface MainContract {
    interface Presenter {
        Single<User> requestUserInfo();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
    }
}
