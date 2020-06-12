package com.formakers.fomes.more;


import com.formakers.fomes.common.mvp.BaseView;

public interface MenuListContract {
    interface Presenter {
        void bindUserInfo();
        void bindCompletedBetaTestsCount();
    }

    interface View extends BaseView<Presenter> {
        void setUserInfo(String email, String nickName);
        void setCompletedBetaTestsCount(int count);
    }
}
