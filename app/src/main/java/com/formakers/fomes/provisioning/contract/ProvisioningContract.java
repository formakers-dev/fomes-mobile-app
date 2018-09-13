package com.formakers.fomes.provisioning.contract;

import android.support.annotation.DrawableRes;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.dagger.ApplicationComponent;

import rx.Completable;

public interface ProvisioningContract {
    interface Presenter {
        void updateDemographicsToUser(Integer birth, String job, String gender);
        void updateLifeGameToUser(String game);
        void updateNickNameToUser(String nickName);
        void setProvisioningProgressStatus(int status);

        void emitNextPageEvent();
        void emitFilledUpEvent(boolean isEnable);
        void emitGrantedEvent(boolean isGranted);
        void emitAlmostCompletedEvent(boolean isAlmostCompleted);

        Completable requestUpdateUser();

        boolean hasUsageStatsPermission();
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
        void setIconImage(@DrawableRes int drawableResId);
        void setNextButtonVisibility(boolean isVisible);
        ApplicationComponent getApplicationComponent();
        void startActivityAndFinish(Class<?> destActivity);
        void showToast(String toastMessage);
    }
}
