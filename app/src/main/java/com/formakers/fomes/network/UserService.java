package com.formakers.fomes.network;

import android.support.annotation.NonNull;

import com.formakers.fomes.helper.AppBeeAPIHelper;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.api.UserAPI;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UserService extends AbstractAppBeeService {

    private static final String TAG = "UserService";
    private final UserAPI userAPI;
    private final LocalStorageHelper localStorageHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public UserService(UserAPI userAPI, LocalStorageHelper localStorageHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.userAPI = userAPI;
        this.localStorageHelper = localStorageHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    public Observable<String> signIn(@NonNull String googleIdToken, @NonNull User user) {
        return userAPI.signIn(googleIdToken, user)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Completable updateRegistrationToken(String registrationToken) {
        return Observable.defer(() -> userAPI.update(localStorageHelper.getAccessToken(), new User(registrationToken)))
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
        return userAPI.verifyToken(localStorageHelper.getAccessToken())
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io()).toCompletable();
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
