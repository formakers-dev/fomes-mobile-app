package com.formakers.fomes.settings;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.mvp.BaseView;

public interface MyInfoContract {
    interface Presenter {
        void loadUserInfoUpdateVersion();
        void loadUserInfo();
        void updateUserInfo(User filledUserInfo);
        boolean isUpdated(User filledUserInfo);
    }

    interface View extends BaseView<Presenter> {
        void bind(User userInfo);
        void showToast(String message);
        void showDuplicatedNickNameWarning();
    }
}
