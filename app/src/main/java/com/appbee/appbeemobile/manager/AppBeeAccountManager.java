package com.appbee.appbeemobile.manager;

import android.util.Log;

import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserAPI;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppBeeAccountManager {

    private static final String TAG = AppBeeAccountManager.class.getSimpleName();
    private UserAPI userAPI;

    @Inject
    public AppBeeAccountManager(UserAPI userAPI) {
        this.userAPI = userAPI;
    }

    public void signIn(User user, SignInResultCallback signInResultCallback) {
        userAPI.signInUser(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Success to send appList");
                } else {
                    Log.d(TAG, "Fail to send appList");
                }
                signInResultCallback.onSuccess();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                signInResultCallback.onFail();
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }
}
