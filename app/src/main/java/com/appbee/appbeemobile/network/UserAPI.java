package com.appbee.appbeemobile.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserAPI {
    @GET("/user/auth")
    Call<String> signInUser(@Header("x-id-token") String idToken);
}