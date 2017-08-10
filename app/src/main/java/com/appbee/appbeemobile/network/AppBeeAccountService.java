package com.appbee.appbeemobile.network;

import android.util.Log;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppBeeAccountService {

    private static final String TAG = AppBeeAccountService.class.getSimpleName();
    private UserAPI userAPI;

    @Inject
    public AppBeeAccountService(UserAPI userAPI) {
        this.userAPI = userAPI;
    }

    public void signIn(String token, SignInResultCallback signInResultCallback) {
        userAPI.signInUser(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
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
}
