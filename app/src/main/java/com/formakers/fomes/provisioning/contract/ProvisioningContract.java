package com.formakers.fomes.provisioning.contract;

import com.formakers.fomes.common.mvp.BaseView;

public interface ProvisioningContract {
    interface Presenter {
        void onNextPageEvent();
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
    }
}
