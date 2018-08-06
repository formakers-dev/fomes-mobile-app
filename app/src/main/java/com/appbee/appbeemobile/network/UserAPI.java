package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface UserAPI {
    @POST("/user/auth")
    Observable<String> signIn(@Header("x-id-token") String googleIdToken, @Body User user);

    @POST("/user")
    Observable<Void> update(@Header("x-access-token") String accessToken, @Body User user);

    @GET("/user/verifyInvitationCode/{code}")
    Observable<Void> verifyInvitationCode(@Path("code") String code);

    @GET("/user/verifyToken")
    Observable<Void> verifyToken(@Header("x-access-token") String accessToken);
}