package com.appbee.appbeemobile.network;

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

    public Observable<String> signIn(String googleIdToken) {
        return userAPI.signInUser(googleIdToken)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> sendUser(User user) {
        return userAPI.updateUser(localStorageHelper.getAccessToken(), user)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
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
