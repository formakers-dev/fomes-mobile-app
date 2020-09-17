package com.formakers.fomes.settings;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.mvp.BaseView;

public interface MyInfoContract {
    interface Presenter {
        void loadUserInfo();
        void updateUserInfo(String nickName, Integer birthday, Integer job, String gender, String lifeApp, String monthlyPayment);
        boolean isUpdated(String nickName, Integer birthday, Integer job, String gender, String lifeApp, String monthlyPayment);
    }

    interface View extends BaseView<Presenter> {
        void bind(User userInfo);
        void showToast(String message);
        void showDuplicatedNickNameWarning();
    }
}
