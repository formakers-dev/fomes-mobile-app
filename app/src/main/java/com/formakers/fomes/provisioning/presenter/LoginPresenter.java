package com.formakers.fomes.provisioning.presenter;

import android.content.Intent;

import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.dagger.scope.LoginActivityScope;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@LoginActivityScope
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "LoginPresenter";

    private GoogleSignInAPIHelper googleSignInAPIHelper;
    private UserService userService;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private UserDAO userDAO;
    private JobManager jobManager;
    private ChannelManager channelManager;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private LoginContract.View view;

    @Inject
    LoginPresenter(LoginContract.View view, GoogleSignInAPIHelper googleSignInAPIHelper, UserService userService, SharedPreferencesHelper SharedPreferencesHelper, UserDAO userDAO, JobManager jobManager, ChannelManager channelManager) {
        this.view = view;
        this.googleSignInAPIHelper = googleSignInAPIHelper;
        this.userService = userService;
        this.sharedPreferencesHelper = SharedPreferencesHelper;
        this.userDAO = userDAO;
        this.jobManager = jobManager;
        this.channelManager = channelManager;
    }

    @Override
    public void signUpOrSignIn(GoogleSignInResult googleSignInResult) {
        GoogleSignInAccount account = googleSignInResult.getSignInAccount();

        User userInfo = new User()
                .setName(account.getDisplayName())
                .setEmail(account.getEmail())
                .setRegistrationToken(sharedPreferencesHelper.getUserRegistrationToken());

        Single<String> loginSingle;

        if (this.isProvisioningProgress()) {
            Log.v(TAG, "Provisioning Progress!");
            loginSingle = userService.signUp(account.getIdToken(), userInfo)
                    .observeOn(Schedulers.io())
                    .doOnSuccess(fomesToken -> userDAO.updateUserInfo(userInfo))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(fomesToken -> view.startActivityAndFinish(ProvisioningActivity.class))
                    .doOnError(e -> view.showToast("가입에 실패하였습니다. 재시도 고고"));
        } else {
            loginSingle = userService.signIn(account.getIdToken()).toSingle()
                    .doOnSuccess(fomesToken -> sharedPreferencesHelper.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(fomesToken -> view.startActivityAndFinish(MainActivity.class))
                    .doOnError(e -> view.showToast("로그인에 실패하였습니다. 재시도 고고"));
        }

        compositeSubscription.add(loginSingle.observeOn(Schedulers.io())
                .doOnSuccess(fomesToken -> {
                    sharedPreferencesHelper.setAccessToken(fomesToken);
                    registerForInit();
                })
                .subscribe(fomesToken -> Log.d(TAG, "로그인 성공!"),
                        e -> Log.e(TAG, "로그인 실패! e=" + String.valueOf(e))));
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

    private void registerForInit() {
        Log.d(TAG, "registerForInit");

        int jobRegisteredResult = registerSendDataJob();
        registerPublicNotificationTopic();

        Log.d(TAG, "job registered result=" + jobRegisteredResult);
    }

    @Override
    public int registerSendDataJob() {
        return this.jobManager.registerSendDataJob(JobManager.JOB_ID_SEND_DATA);
    }

    @Override
    public void registerPublicNotificationTopic() {
        channelManager.subscribePublicTopic();
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    LoginContract.View getView() {
        return this.view;
    }
}
