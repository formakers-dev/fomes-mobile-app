package com.appbee.appbeemobile.network;

import android.support.annotation.NonNull;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UserService extends AbstractAppBeeService {

    private static final String TAG = UserService.class.getSimpleName();
    private final UserAPI userAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public UserService(UserAPI userAPI, LocalStorageHelper localStorageHelper) {
        this.userAPI = userAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public Observable<String> signIn(@NonNull String googleIdToken, @NonNull User user) {
        return userAPI.signIn(googleIdToken, user)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Completable updateRegistrationToken(String registrationToken) {
        return userAPI.update(localStorageHelper.getAccessToken(), new User(registrationToken))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
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
