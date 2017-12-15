package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import javax.inject.Inject;

import rx.Completable;
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

    public String generateAppBeeToken(String googleIdToken) {
        return userAPI.signInUser(googleIdToken)
                .doOnError(this::logError)
                .onErrorReturn(throwable -> "")
                .subscribeOn(Schedulers.io())
                .toBlocking()
                .single();
    }

    public Completable sendUser(User user) {
        return userAPI.updateUser(localStorageHelper.getAccessToken(), user)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }

    public Completable updateRegistrationToken(String registrationToken) {
        return userAPI.updateUser(localStorageHelper.getAccessToken(), new User(registrationToken))
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

    @Override
    protected String getTag() {
        return TAG;
    }
}
