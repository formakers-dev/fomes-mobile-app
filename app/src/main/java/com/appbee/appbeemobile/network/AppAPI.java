package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.AppUsage;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface AppAPI {
    @POST("/apps/usages")
    Observable<Boolean> postUsages(@Header("x-access-token") String accessToken, @Body List<AppUsage> appUsages);
}