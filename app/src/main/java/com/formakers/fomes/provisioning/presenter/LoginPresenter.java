package com.formakers.fomes.provisioning.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.util.FomesConstants;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import rx.Single;
import rx.Subscription;
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
    public Single<String> requestSignUpBy(@NonNull GoogleSignInResult googleSignInResult) {
        GoogleSignInAccount account = googleSignInResult.getSignInAccount();

        User userInfo = new User().setUserId(account.getId())
                .setName(account.getDisplayName())
                .setEmail(account.getEmail())
                .setRegistrationToken(sharedPreferencesHelper.getRegistrationToken());

        return userService.signUp(account.getIdToken(), userInfo)
                .observeOn(Schedulers.io())
                .doOnSuccess(fomesToken -> {
                    sharedPreferencesHelper.setAccessToken(fomesToken);
                    userDAO.updateUserInfo(userInfo);
                    jobManaer.registerSendDataJob(JobManager.JOB_ID_SEND_DATA);
                });
    }

    @Override
    public Intent getGoogleSignInIntent() {
        return googleSignInAPIHelper.getSignInIntent();
    }

    @Override
    public GoogleSignInResult convertGoogleSignInResult(Intent googleUserData) {
        GoogleSignInResult result = googleSignInAPIHelper.getSignInResult(googleUserData);

        if (result == null || !result.isSuccess()) {
            return null;
        }

        return result;
    }

    @Override
    public Single<GoogleSignInResult> googleSilentSignIn() {
        return Single.create(emitter -> {
            googleSignInAPIHelper.requestSilentSignInResult()
                    .observeOn(Schedulers.io())
                    .subscribe(googleSignInResult -> {
                        if (googleSignInResult != null && googleSignInResult.isSuccess()) {
                            emitter.onSuccess(googleSignInResult);
                        } else {
                            emitter.onError(new Exception("Silent SignIn Failed!"));
                        }
                    }, e -> emitter.onError(e));
        });
    }

    @Override
    public boolean isProvisioningProgress() {
        return this.sharedPreferencesHelper.getProvisioningProgressStatus()
                != FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED;
    }

    LoginContract.View getView() {
        return this.view;
    }
}
