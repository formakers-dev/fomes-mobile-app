package com.formakers.fomes.provisioning.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.dagger.ApplicationComponent;

import rx.Completable;

public interface ProvisioningContract {
    interface Presenter {
        void updateDemographicsToUser(Integer birth, String job, String gender);
        void updateLifeGameToUser(String game);
        void updateNickNameToUser(String nickName);
        void emitNextPageEvent();
        void emitFilledUpEvent(boolean isEnable);
        void emitGrantedEvent(boolean isGranted);
        Completable requestUpdateUser();
        boolean hasUsageStatsPermission();
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
        void setNextButtonVisibility(boolean isVisible);
        ApplicationComponent getApplicationComponent();
        void startActivityAndFinish(Class<?> destActivity);
    }
}
