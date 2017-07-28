package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAPI {
    @POST("/user")
    Call<Boolean> signInUser(@Body User user);
}
