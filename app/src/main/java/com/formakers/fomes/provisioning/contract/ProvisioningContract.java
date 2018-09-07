package com.formakers.fomes.provisioning.contract;

import com.formakers.fomes.common.mvp.BaseView;

public interface ProvisioningContract {
    interface Presenter {
        void onNextPageEvent();
        void updateDemographicsToUser(Integer birth, String job, String gender);
        void updateLifeGameToUser(String game);
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
    }
}
