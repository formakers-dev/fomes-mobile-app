package com.formakers.fomes.provisioning.presenter;

import android.content.Intent;

import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.util.FomesConstants;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class LoginPresenter implements LoginContract.Presenter {

    @Inject GoogleSignInAPIHelper googleSignInAPIHelper;
    @Inject UserService userService;
    @Inject SharedPreferencesHelper SharedPreferencesHelper;

    private LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // TODO : ActivityComponent로 변환후 변경 필요
    // temporary code for test
    LoginPresenter(LoginContract.View view, GoogleSignInAPIHelper googleSignInAPIHelper, UserService userService, SharedPreferencesHelper SharedPreferencesHelper) {
        this.view = view;
        this.googleSignInAPIHelper = googleSignInAPIHelper;
        this.userService = userService;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
    }

    @Override
    public boolean requestSignUpBy(Intent googleUserData) {
        GoogleSignInResult result = googleSignInAPIHelper.getSignInResult(googleUserData);

        if (result == null || !result.isSuccess()) {
            return false;
        }

        GoogleSignInAccount account = result.getSignInAccount();
        userService.signUp(account.getIdToken(), new User(account.getId(), account.getEmail(), SharedPreferencesHelper.getRegistrationToken()))
                .observeOn(Schedulers.io())
                .subscribe(fomesToken -> {
                    // TODO : 리팩토링 고려 필요
                    SharedPreferencesHelper.setAccessToken(fomesToken);
                    SharedPreferencesHelper.setUserId(account.getId());
                    SharedPreferencesHelper.setEmail(account.getEmail());
                    // TODO : 로그인 완료 상태로 체크되어야 하는게 맞지않을까?????
                    SharedPreferencesHelper.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO);

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
