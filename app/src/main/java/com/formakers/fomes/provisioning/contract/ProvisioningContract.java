package com.formakers.fomes.provisioning.contract;

import com.formakers.fomes.common.mvp.BaseView;

public interface ProvisioningContract {
    interface Presenter {
        void onNextPageEvent();
        void setUserInfo(Integer birth, String job, String gender);
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
    }
}
