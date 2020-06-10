package com.formakers.fomes.more;


import com.formakers.fomes.common.mvp.BaseView;

public interface MenuListContract {
    interface Presenter {
        void bindUserInfo();
    }

    interface View extends BaseView<Presenter> {
        void setUserInfo(String email, String nickName);
    }
}
