package com.formakers.fomes.common.network.api;

import com.formakers.fomes.model.User;

import java.util.HashMap;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.Single;

public interface UserAPI {
    // Return : FomesToken
    @POST("/user/signup")
    Single<String> signUp(@Header("x-id-token") String googleIdToken, @Body User user);

    // Return : FomesToken
    @POST("/user/signIn")
    Observable<String> signIn(@Header("x-id-token") String googleIdToken, @Body User user);

    @POST("/user")
    Observable<Void> update(@Header("x-access-token") String accessToken, @Body User user);

    @POST("/user/wishlist")
    Observable<Void> postWishList(@Header("x-access-token") String accessToken, @Body HashMap<String, Object> wishListMap);

    @Deprecated
    @GET("/user/verifyInvitationCode/{code}")
    Observable<Void> verifyInvitationCode(@Path("code") String code);

    @GET("/user/verifyToken")
    Observable<Void> verifyToken(@Header("x-access-token") String accessToken);
}