package com.appbee.appbeemobile.network;

import retrofit2.http.GET;
import rx.Single;

public interface ConfigAPI {
    @GET("/config/version")
    Single<Long> getAppVersion();
}
