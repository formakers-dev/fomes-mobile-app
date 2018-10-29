package com.formakers.fomes.common.network;

import android.support.annotation.NonNull;

import com.formakers.fomes.helper.APIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.util.Log;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class UserService extends AbstractService {
    private static final String TAG = "UserService";

    private final UserAPI userAPI;
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final APIHelper APIHelper;

    @Inject
    public UserService(UserAPI userAPI, SharedPreferencesHelper SharedPreferencesHelper, APIHelper APIHelper) {
        this.userAPI = userAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.APIHelper = APIHelper;
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
                .compose(APIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable updateRegistrationToken(String registrationToken) {
        return Observable.defer(() -> userAPI.update(SharedPreferencesHelper.getAccessToken(), new User(registrationToken)))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable requestSaveAppToWishList(String packageName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("packageName", packageName);

        return Observable.defer(() -> userAPI.postWishList(SharedPreferencesHelper.getAccessToken(), map))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken())
                .toCompletable();
    }

    @Deprecated
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
