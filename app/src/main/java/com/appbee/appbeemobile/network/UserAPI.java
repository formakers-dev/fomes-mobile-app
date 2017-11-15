package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface UserAPI {
    @GET("/user/auth")
    Observable<String> signInUser(@Header("x-id-token") String googleIdToken);

    @POST("/user")
    Observable<Boolean> updateUser(@Header("x-access-token") String accessToken, @Body User user);
}