package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface StatAPI {
    @POST("/stats/long")
    Observable<Response<Boolean>> sendLongTermStats(@Header("x-access-token") String accessToken, @Body List<LongTermStat> longTermStats);

    @POST("/stats/event")
    Observable<Response<Boolean>> sendEventStats(@Header("x-access-token") String accessToken, @Body List<EventStat> eventStats);

    @POST("/stats/short")
    Observable<Response<Boolean>> sendShortTermStats(@Header("x-access-token") String accessToken, @Body List<ShortTermStat> shortTermStats);
}
