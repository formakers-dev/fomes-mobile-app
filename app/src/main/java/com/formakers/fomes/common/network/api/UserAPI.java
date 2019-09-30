package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.model.AppInfo;
import com.formakers.fomes.common.model.User;

import java.util.HashMap;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

public interface UserAPI {
    // Return : FomesToken
    @POST("/user/signup")
    Single<String> signUp(@Header("x-id-token") String googleIdToken, @Body User user);

    // Return : FomesToken
    @POST("/user/signIn")
    Observable<String> signIn(@Header("x-id-token") String googleIdToken);

    @POST("/user")
    Observable<Void> update(@Header("x-access-token") String accessToken, @Body User user);

    @PUT("/user/activated")
    Observable<Void> notifyActivated(@Header("x-access-token") String accessToken);

    @PATCH("/user/noti-token")
    Observable<Void> updateNotificationToken(@Header("x-access-token") String accessToken, @Body User newNotiTokenUser);

    @POST("/user/wishlist")
    Observable<Void> postWishList(@Header("x-access-token") String accessToken, @Body HashMap<String, Object> wishListMap);

    @GET("/user/verifyToken")
    Observable<Void> verifyToken(@Header("x-access-token") String accessToken);

    @GET("/user/verify/info")
    Observable<Void> verifyNickName(@Header("x-access-token") String accessToken, @Query("nickName") String nickName);

    @DELETE("/user/wishlist/{packageName}")
    Observable<Void> deleteWishList(@Header("x-access-token") String accessToken, @Path("packageName") String packageName);

    @GET("/user/wishlist")
    Observable<List<AppInfo>> getWishList(@Header("x-access-token") String accessToken);
}