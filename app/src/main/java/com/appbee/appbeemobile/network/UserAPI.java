package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface UserAPI {
    @GET("/user/auth")
    Observable<String> signInUser(@Header("x-id-token") String googleIdToken);

    @POST("/user/apps")
    Observable<Boolean> sendAppInfoList(@Header("x-access-token") String accessToken, @Body List<NativeAppInfo> nativeAppInfos);

    @POST("/user")
    Observable<Boolean> sendUser(@Body User user);
}