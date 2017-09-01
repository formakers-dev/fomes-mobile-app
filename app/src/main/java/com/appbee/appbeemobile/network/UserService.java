package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
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

    public void signIn(String token, SignInResultCallback signInResultCallback) {
        userAPI.signInUser(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Success to send appList");
                } else {
                    Log.d(TAG, "Fail to send appList");
                }
                signInResultCallback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                signInResultCallback.onFail();
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }

    public void sendAppList(ServiceCallback serviceCallback) {
        userAPI.sendAppInfoList(localStorageHelper.getAccessToken(), appUsageDataHelper.getAppList())
                .subscribeOn(Schedulers.io())
                .subscribe(new BooleanResponseObserver(serviceCallback));
    }

    public void sendUser(User user) {
        userAPI.sendUser(user)
                .subscribeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response.code())), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }
}
