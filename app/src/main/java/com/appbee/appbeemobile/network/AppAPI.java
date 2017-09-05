package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.AppInfo;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface AppAPI {
    @POST("/apps/info")
    Observable<Response<List<AppInfo>>> getInfo(@Body List<String> packageNames);

    @POST("/apps/uncrawled")
    Observable<Response<Boolean>> postUncrawledApps(@Body List<String> packageNames);
}