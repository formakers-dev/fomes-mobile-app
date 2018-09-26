package com.formakers.fomes.common.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.formakers.fomes.helper.AppBeeAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.common.network.api.UserAPI;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class UserService extends AbstractAppBeeService {

    private static final String TAG = "UserService";
    private final UserAPI userAPI;
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public UserService(UserAPI userAPI, SharedPreferencesHelper SharedPreferencesHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.userAPI = userAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    public Single<String> signUp(@NonNull String googleIdToken, @NonNull User user) {
        return userAPI.signUp(googleIdToken, user)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> signIn(@NonNull String googleIdToken, @NonNull User user) {
        return userAPI.signIn(googleIdToken, user)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Completable updateUser(User user) {
        return Observable.defer(() -> userAPI.update(SharedPreferencesHelper.getAccessToken(), user))
                .doOnCompleted(() -> Log.d(TAG, "updateUser) Completed!"))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable updateUserWithoutRefreshToken(User user) {
        return Observable.defer(() -> userAPI.update(SharedPreferencesHelper.getAccessToken(), user))
                .doOnCompleted(() -> Log.d(TAG, "updateUser) Completed!"))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable updateRegistrationToken(String registrationToken) {
        return Observable.defer(() -> userAPI.update(SharedPreferencesHelper.getAccessToken(), new User(registrationToken)))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable verifyInvitationCode(String code) {
        return userAPI.verifyInvitationCode(code)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }

    public Completable verifyToken() {
        return userAPI.verifyToken(SharedPreferencesHelper.getAccessToken())
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io()).toCompletable();
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
