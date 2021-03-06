package com.formakers.fomes.provisioning.login;

import android.content.Intent;

import com.formakers.fomes.common.mvp.BaseView;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import rx.Single;
import rx.Subscription;

public interface LoginContract {
    interface Presenter {
        void signUpOrSignIn(GoogleSignInResult googleSignInResult);
        Single<GoogleSignInResult> googleSilentSignIn();

        Intent getGoogleSignInIntent();
        GoogleSignInResult convertGoogleSignInResult(Intent googleUserData);

        boolean isProvisioningProgress();

        void unsubscribe();

        void init();
    }

    interface View extends BaseView<Presenter> {
        void showToast(String toastMessage);
        void startActivityAndFinish(Class<?> destActivity);
        void hideFomesLogoAndShowLoginView();

        void addToCompositeSubscription(Subscription subscription);
    }
}
