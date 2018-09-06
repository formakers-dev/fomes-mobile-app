package com.formakers.fomes.provisioning.presenter;

import android.content.Intent;

import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class LoginPresenter implements LoginContract.Presenter {

    @Inject GoogleSignInAPIHelper googleSignInAPIHelper;
    @Inject UserService userService;
    @Inject LocalStorageHelper localStorageHelper;

    private LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // TODO : ActivityComponent로 변환후 변경 필요
    // temporary code for test
    LoginPresenter(LoginContract.View view, GoogleSignInAPIHelper googleSignInAPIHelper, UserService userService, LocalStorageHelper localStorageHelper) {
        this.view = view;
        this.googleSignInAPIHelper = googleSignInAPIHelper;
        this.userService = userService;
        this.localStorageHelper = localStorageHelper;
    }

    @Override
    public boolean requestSignUpBy(Intent googleUserData) {
        GoogleSignInResult result = googleSignInAPIHelper.getSignInResult(googleUserData);

        if (result == null || !result.isSuccess()) {
            return false;
        }

        GoogleSignInAccount account = result.getSignInAccount();
        userService.signUp(account.getIdToken(), new User(account.getId(), account.getEmail(), localStorageHelper.getRegistrationToken()))
                .observeOn(Schedulers.io())
                .subscribe(fomesToken -> {
                    // TODO : 리팩토링 고려 필요
                    localStorageHelper.setAccessToken(fomesToken);
                    localStorageHelper.setUserId(account.getId());
                    localStorageHelper.setEmail(account.getEmail());

                    this.view.startActivityAndFinish(ProvisioningActivity.class);
                }, e -> this.view.showToast("가입에 실패하였습니다. 재시도 고고"));

        return true;
    }

    @Override
    public Intent getGoogleSignInIntent() {
        return googleSignInAPIHelper.getSignInIntent();
    }

    LoginContract.View getView() {
        return this.view;
    }
}
