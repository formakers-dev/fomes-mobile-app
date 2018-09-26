package com.formakers.fomes.provisioning.presenter;

import android.content.Intent;

import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class LoginPresenter implements LoginContract.Presenter {

    @Inject GoogleSignInAPIHelper googleSignInAPIHelper;
    @Inject UserService userService;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject UserDAO userDAO;
    @Inject JobManager jobManaer;

    private LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    // TODO : ActivityComponent로 변환후 변경 필요
    // temporary code for test
    LoginPresenter(LoginContract.View view, GoogleSignInAPIHelper googleSignInAPIHelper, UserService userService, SharedPreferencesHelper SharedPreferencesHelper, UserDAO userDAO, JobManager jobManaer) {
        this.view = view;
        this.googleSignInAPIHelper = googleSignInAPIHelper;
        this.userService = userService;
        this.sharedPreferencesHelper = SharedPreferencesHelper;
        this.userDAO = userDAO;
        this.jobManaer = jobManaer;
    }

    @Override
    public boolean requestSignUpBy(Intent googleUserData) {
        GoogleSignInResult result = googleSignInAPIHelper.getSignInResult(googleUserData);

        if (result == null || !result.isSuccess()) {
            return false;
        }

        GoogleSignInAccount account = result.getSignInAccount();
        User userInfo = new User().setUserId(account.getId())
                .setName(account.getDisplayName())
                .setEmail(account.getEmail())
                .setRegistrationToken(sharedPreferencesHelper.getRegistrationToken());

        userService.signUp(account.getIdToken(), userInfo)
                .observeOn(Schedulers.io())
                .subscribe(fomesToken -> {
                    sharedPreferencesHelper.setAccessToken(fomesToken);
                    userDAO.updateUserInfo(userInfo);
                    jobManaer.registerSendDataJob(JobManager.JOB_ID_SEND_DATA);

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
