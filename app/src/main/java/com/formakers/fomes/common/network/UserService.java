package com.formakers.fomes.common.network;

import androidx.annotation.NonNull;

import com.formakers.fomes.common.network.helper.APIHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.model.AppInfo;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.util.Log;

import java.util.HashMap;
import java.util.List;

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
    private final APIHelper apiHelper;

    @Inject
    public UserService(UserAPI userAPI, SharedPreferencesHelper SharedPreferencesHelper, APIHelper apiHelper) {
        this.userAPI = userAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.apiHelper = apiHelper;
    }

    public Single<User> signUp(@NonNull String googleIdToken, @NonNull User user) {
        return userAPI.signUp(googleIdToken, user)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Observable<User> signIn(@NonNull String googleIdToken) {
        return userAPI.signIn(googleIdToken)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Completable updateUser(User user, String versionName) {
        User userWithOtherInfo = user.setAppVersion(versionName)
                .setDevice(new User.DeviceInfo());
        return updateUser(userWithOtherInfo);
    }

    @Deprecated
    public Completable updateUser(User user) {
        return Observable.defer(() -> userAPI.update(SharedPreferencesHelper.getAccessToken(), user))
                .doOnCompleted(() -> Log.d(TAG, "updateUser) Completed!"))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable notifyActivated() {
        return Observable.defer(() -> userAPI.notifyActivated(SharedPreferencesHelper.getAccessToken()))
                .doOnCompleted(() -> Log.d(TAG, "notifyActivated) Completed!"))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable updateRegistrationToken(String registrationToken) {
        return Observable.defer(() -> userAPI.updateNotificationToken(SharedPreferencesHelper.getAccessToken(), new User().setRegistrationToken(registrationToken)))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable updateUserInfo(User userInfo) {
        return Observable.defer(() -> userAPI.updateUserInfo(SharedPreferencesHelper.getAccessToken(), userInfo))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable requestSaveAppToWishList(String packageName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("packageName", packageName);

        return Observable.defer(() -> userAPI.postWishList(SharedPreferencesHelper.getAccessToken(), map))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable requestRemoveAppFromWishList(String packageName) {
        return Observable.defer(() -> userAPI.deleteWishList(SharedPreferencesHelper.getAccessToken(), packageName))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Observable<List<AppInfo>> requestWishList() {
        return Observable.defer(() -> userAPI.getWishList(SharedPreferencesHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken());
    }

    public Completable verifyToken() {
        return userAPI.verifyToken(SharedPreferencesHelper.getAccessToken())
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io()).toCompletable();
    }

    public Completable verifyNickName(String nickName) {
        return Observable.defer(() -> userAPI.verifyNickName(SharedPreferencesHelper.getAccessToken(), nickName)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken()))
                .toCompletable();
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
