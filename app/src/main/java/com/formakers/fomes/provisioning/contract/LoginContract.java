package com.formakers.fomes.provisioning.contract;

import android.content.Intent;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.dagger.ApplicationComponent;

public interface LoginContract {
    interface Presenter {
        boolean requestSignUpBy(Intent googleUserData);
        Intent getGoogleSignInIntent();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
        void showToast(String toastMessage);
        void startActivityAndFinish(Class<?> destActivity);
    }
}
