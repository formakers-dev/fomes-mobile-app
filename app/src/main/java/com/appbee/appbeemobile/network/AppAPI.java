package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.AppInfo;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface AppAPI {
    @POST("/apps/info")
    Observable<Response<List<AppInfo>>> getInfo(@Header("x-access-token") String accessToken, @Body List<String> packageNames);

    @POST("/apps/uncrawled")
    Observable<Response<Boolean>> postUncrawledApps(@Header("x-access-token") String accessToken, @Body List<String> packageNames);
}