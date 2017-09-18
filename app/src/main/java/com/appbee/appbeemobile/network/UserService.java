package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UserService {

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

    public Observable<String> signIn(String token) {
        return userAPI.signInUser(token).subscribeOn(Schedulers.io());
    }

    public void sendAppList() {
        appUsageDataHelper.getAppList().subscribe(nativeAppInfoList ->
                userAPI.sendAppInfoList(localStorageHelper.getUUID(), nativeAppInfoList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(response -> Log.d(TAG, String.valueOf(response.code())), error -> {
                            if (error instanceof HttpException) {
                                Log.d(TAG, String.valueOf(((HttpException) error).code()));
                            }
                        }));
    }

    public void sendUser(User user) {
        userAPI.sendUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response.code())), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }
}
