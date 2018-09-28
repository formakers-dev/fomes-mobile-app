package com.formakers.fomes.provisioning.contract;

import android.content.Intent;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import rx.Single;

public interface LoginContract {
    interface Presenter {
        Single<String> requestSignUpBy(GoogleSignInResult googleSignInResult);
        Single<GoogleSignInResult> googleSilentSignIn();

        Intent getGoogleSignInIntent();
        GoogleSignInResult convertGoogleSignInResult(Intent googleUserData);

        boolean isProvisioningProgress();
    }

    interface View extends BaseView<Presenter> {
        void showToast(String toastMessage);
        void startActivityAndFinish(Class<?> destActivity);
    }
}
