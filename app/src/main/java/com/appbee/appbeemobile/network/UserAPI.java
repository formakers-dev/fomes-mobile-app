package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserAPI {
    @POST("/user")
    Call<String> signInUser(@Header("x-access-token") String accessToken, @Body User user);
}
