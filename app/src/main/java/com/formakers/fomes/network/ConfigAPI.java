package com.formakers.fomes.network;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface ConfigAPI {
    @GET("/config/version")
    Observable<Long> getAppVersion();

    @GET("/config/excludePackageNames")
    Observable<List<String>> getExcludePackageNames(@Header("x-access-token") String accessToken);
}
