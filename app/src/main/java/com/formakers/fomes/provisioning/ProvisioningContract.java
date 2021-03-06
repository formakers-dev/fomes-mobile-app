package com.formakers.fomes.provisioning;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.view.BaseFragment;

import rx.Completable;

public interface ProvisioningContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();

        void setUserInfo(String game, Integer birth, Integer job, String gender);
        void updateNickNameToUser(String nickName);
        void setProvisioningProgressStatus(int status);

        String getUserNickName();

        void emitNextPageEvent();
        void emitFilledUpEvent(BaseFragment fragment, boolean isEnable);
        void emitNeedToGrantEvent();
        void emitStartActivityAndFinishEvent(Class<?> destActivity);

        Completable requestVerifyUserToken();
        Completable requestVerifyUserNickName(String nickName);
        Completable requestToUpdateUserInfo();

        boolean hasUsageStatsPermission();
        boolean isSelected(BaseFragment fragment);
        boolean isProvisiongProgress();
        void checkGrantedOnPermissionFragment();
    }

    interface View extends BaseView<Presenter> {
        void nextPage();
        void setNextButtonVisibility(boolean isVisible);
        void setNextButtonText(int stringResId);
        void startActivityAndFinish(Class<?> destActivity);
        void showToast(String toastMessage);
        boolean isSelectedFragement(BaseFragment fragment);
    }
}
