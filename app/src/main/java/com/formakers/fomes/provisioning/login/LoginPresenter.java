package com.formakers.fomes.provisioning.login;

import android.content.Intent;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.MainActivity;
import com.formakers.fomes.provisioning.ProvisioningActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@LoginDagger.Scope
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

        if (account == null) {
            view.showToast("구글 로그인 계정정보를 가져오는 것에 실패하였습니다. 재시도 고고");
            return;
        }

        User userInfo = new User()
                .setName(account.getDisplayName())
                .setEmail(account.getEmail())
                .setRegistrationToken(sharedPreferencesHelper.getUserRegistrationToken());

        Single<User> loginSingle;

        if (this.isProvisioningProgress()) {
            Log.v(TAG, "Provisioning Progress!");
            loginSingle = userService.signUp(account.getIdToken(), userInfo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(user -> view.startActivityAndFinish(ProvisioningActivity.class))
                    .doOnError(e -> {
                        if (e instanceof HttpException) {
                            if (((HttpException) e).code() != UserAPI.StatusCode.ALREADY_SIGN_UP) {
                                view.showToast("가입에 실패하였습니다. 재시도 고고");
                            }
                        }
                    })
                    .onErrorResumeNext(throwable -> {
                        if (throwable instanceof HttpException) {
                            if (((HttpException) throwable).code() == UserAPI.StatusCode.ALREADY_SIGN_UP) {
                                Log.d(TAG, "이미 가입된 유저입니다. 로그인으로 진행됩니다.");
                                this.view.showToast("로그인 되었습니다! 반가워요! 😍");
                                return signIn(account.getIdToken(), true);
                            }
                        }
                        return Single.error(throwable);
                    });
        } else {
            loginSingle = signIn(account.getIdToken(), false);
        }

        compositeSubscription.add(loginSingle.observeOn(Schedulers.io())
                .doOnSuccess(user -> {
                    Log.v(TAG, "login after user=" + user);
                    Log.v(TAG, "login after accessToken=" + user.getAccessToken());
                    userDAO.updateUserInfo(user);
                    sharedPreferencesHelper.setAccessToken(user.getAccessToken());
                    registerForInit();
                })
                .subscribe(fomesToken -> Log.d(TAG, "로그인 성공!"),
                        e -> Log.e(TAG, "로그인 실패! e=" + String.valueOf(e))));
    }

    private Single<User> signIn(String idToken, boolean isSignUp) {
        return userService.signIn(idToken).toSingle()
                .doOnSuccess(user -> sharedPreferencesHelper.setProvisioningProgressStatus(isSignUp ? FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION : FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(user -> view.startActivityAndFinish(MainActivity.class))
                .doOnError(e -> {
                    if (e instanceof HttpException) {
                        if (((HttpException) e).code() == UserAPI.StatusCode.NOT_EXIST_USER) {
                            // TODO : 이거보단 그냥 로컬 스토리지 전체 초기화가 나을 것 같다. 대신 그렇게 하게되면 경고창 띄우기
                            sharedPreferencesHelper.resetProvisioningProgressStatus();
                        }
                    }
                    view.showToast("로그인에 실패하였습니다. 재시도 고고");
                    view.hideFomesLogo();
                    view.showLoginView();
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

    private void registerForInit() {
        Log.d(TAG, "registerForInit");

        int jobRegisteredResult = jobManager.registerSendDataJob(JobManager.JOB_ID_SEND_DATA);
        channelManager.subscribePublicTopic();

        Log.d(TAG, "job registered result=" + jobRegisteredResult);
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void init() {
        if (this.isProvisioningProgress()) {
            this.view.hideFomesLogo();
            this.view.showLoginView();
        } else {
            this.view.addToCompositeSubscription(
                this.googleSilentSignIn()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::signUpOrSignIn, e -> {
                            this.view.hideFomesLogo();
                            this.view.showLoginView();
                        })
            );
        }
    }

    LoginContract.View getView() {
        return this.view;
    }
}
