package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.EventLog;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface EventLogAPI {

    @POST("/event-logs")
    Observable<Void> postEventLog(@Header("x-access-token") String accessToken, @Body EventLog eventLog);

}