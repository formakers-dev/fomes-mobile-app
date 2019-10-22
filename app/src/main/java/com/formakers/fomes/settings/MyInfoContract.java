package com.formakers.fomes.settings;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.mvp.BaseView;

public interface MyInfoContract {
    interface Presenter {
        void loadUserInfo();
    }

    interface View extends BaseView<Presenter> {
        void bind(User userInfo);
    }
}
