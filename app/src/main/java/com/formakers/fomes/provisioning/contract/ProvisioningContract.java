package com.formakers.fomes.provisioning.contract;

import android.support.annotation.StringRes;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.dagger.ApplicationComponent;

import rx.Completable;

public interface ProvisioningContract {
    interface Presenter {
        void updateUserInfo(String game, Integer birth, Integer job, String gender);
        void updateNickNameToUser(String nickName);
        void setProvisioningProgressStatus(int status);

        String getUserNickName();

        void emitNextPageEvent();
        void emitFilledUpEvent(BaseFragment fragment, boolean isEnable);
        void emitNeedToGrantEvent();
        void emitStartActivityAndFinishEvent(Class<?> destActivity);

        Completable requestVerifyUserToken();
        Completable requestVerifyUserNickName(String nickName);
        Completable requestUpdateUser();

        boolean hasUsageStatsPermission();
        boolean isSelected(BaseFragment fragment);
        boolean isProvisiongProgress();

        int registerSendDataJob();
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
        void setNextButtonVisibility(boolean isVisible);
        void setNextButtonText(int stringResId);
        ApplicationComponent getApplicationComponent();
        void startActivityAndFinish(Class<?> destActivity);
        void showToast(String toastMessage);
        boolean isSelectedFragement(BaseFragment fragment);
    }
}
