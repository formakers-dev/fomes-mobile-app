package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface UserAPI {
    @GET("/user/auth")
    Call<String> signInUser(@Header("x-id-token") String idToken);

    @POST("/user/apps")
    Observable<Response<Boolean>> sendAppInfoList(@Header("x-appbee-number") String userId, @Body List<NativeAppInfo> nativeAppInfos);

    @POST("/user")
    Observable<Response<Boolean>> sendUser(@Body User user);
}