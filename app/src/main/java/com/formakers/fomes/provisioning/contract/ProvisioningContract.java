package com.formakers.fomes.provisioning.contract;

import android.support.annotation.DrawableRes;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.dagger.ApplicationComponent;

import rx.Completable;

public interface ProvisioningContract {
    interface Presenter {
        void updateDemographicsToUser(Integer birth, Integer job, String gender);
        void updateLifeGameToUser(String game);
        void updateNickNameToUser(String nickName);
        void setProvisioningProgressStatus(int status);

        void emitNextPageEvent();
        void emitFilledUpEvent(BaseFragment fragment, boolean isEnable);
        void emitNeedToGrantEvent();
        void emitStartActivityAndFinishEvent(Class<?> destActivity);

        Completable requestVerifyUserToken();
        Completable requestUpdateUser();

        boolean hasUsageStatsPermission();
        boolean isSelected(BaseFragment fragment);
        boolean isProvisiongProgress();

        int registerSendDataJob();
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
        void setIconImage(@DrawableRes int drawableResId);
        void setNextButtonVisibility(boolean isVisible);
        void setNextButtonText(int stringResId);
        ApplicationComponent getApplicationComponent();
        void startActivityAndFinish(Class<?> destActivity);
        void showToast(String toastMessage);
        boolean isSelectedFragement(BaseFragment fragment);
    }
}
