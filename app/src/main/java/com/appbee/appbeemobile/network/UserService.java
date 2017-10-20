package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class UserService extends AbstractAppBeeService {

    private static final String TAG = UserService.class.getSimpleName();
    private final UserAPI userAPI;
    private final AppUsageDataHelper appUsageDataHelper;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public UserService(AppUsageDataHelper appUsageDataHelper, UserAPI userAPI, LocalStorageHelper localStorageHelper) {
        this.appUsageDataHelper = appUsageDataHelper;
        this.userAPI = userAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public Observable<String> signIn(String googleIdToken) {
        return userAPI.signInUser(googleIdToken).subscribeOn(Schedulers.io());
    }

    public void sendAppList() {
        appUsageDataHelper.getAppList().subscribe(nativeAppInfoList ->
                userAPI.sendAppInfoList(localStorageHelper.getAccessToken(), nativeAppInfoList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(response -> Log.d(TAG, String.valueOf(response)), this::logError));
    }

    public void sendUser(User user) {
        userAPI.updateNotificationToken(user)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response)), this::logError);
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
