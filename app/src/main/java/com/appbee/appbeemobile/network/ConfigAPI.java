package com.appbee.appbeemobile.network;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Single;

public interface ConfigAPI {
    @GET("/config/version")
    Single<Long> getAppVersion();

    @GET("/config/excludePackageNames")
    Single<List<String>> getExcludePackageNames(@Header("x-access-token") String accessToken);
}
