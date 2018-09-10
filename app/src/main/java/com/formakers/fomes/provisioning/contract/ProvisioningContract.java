package com.formakers.fomes.provisioning.contract;

import com.formakers.fomes.common.mvp.BaseView;

public interface ProvisioningContract {
    interface Presenter {
        void updateDemographicsToUser(Integer birth, String job, String gender);
        void updateLifeGameToUser(String game);
        void updateNickNameToUser(String nickName);
        void emitNextPageEvent();
        void emitFilledUpEvent(boolean isEnable);
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
        void setNextButtonVisibility(boolean isVisible);
    }
}
