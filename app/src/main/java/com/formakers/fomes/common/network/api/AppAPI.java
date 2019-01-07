package com.formakers.fomes.common.network.api;

import com.formakers.fomes.model.AppInfo;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface AppAPI {
    @GET("/apps/{packageName}")
    Observable<AppInfo> getAppInfo(@Header("x-access-token") String accessToken, @Path("packageName") String packageName);
}
