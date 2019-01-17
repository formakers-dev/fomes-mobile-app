package com.formakers.fomes.provisioning.contract;

import android.content.Intent;

import com.formakers.fomes.common.mvp.BaseView;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import rx.Single;

public interface LoginContract {
    interface Presenter {
        void signUpOrSignIn(GoogleSignInResult googleSignInResult);
        Single<GoogleSignInResult> googleSilentSignIn();
        int registerSendDataJob();
        void registerPublicNotificationTopic();

        Intent getGoogleSignInIntent();
        GoogleSignInResult convertGoogleSignInResult(Intent googleUserData);

        boolean isProvisioningProgress();

        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void showToast(String toastMessage);
        void startActivityAndFinish(Class<?> destActivity);
    }
}
