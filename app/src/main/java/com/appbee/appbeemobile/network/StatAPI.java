package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.AnalysisResult;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.NativeLongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface StatAPI {
    @POST("/stats/long/yearly")
    Observable<Response<Boolean>> sendLongTermStatsYearly(@Header("x-appbee-number") String userId, @Body List<NativeLongTermStat> longTermStats);

    @POST("/stats/long/monthly")
    Observable<Response<Boolean>> sendLongTermStatsMonthly(@Header("x-appbee-number") String userId, @Body List<NativeLongTermStat> longTermStats);

    @POST("/stats/event")
    Observable<Response<Boolean>> sendEventStats(@Header("x-appbee-number") String userId, @Body List<EventStat> eventStats);

    @POST("/stats/short")
    Observable<Response<Boolean>> sendShortTermStats(@Header("x-appbee-number") String userId, @Body List<ShortTermStat> shortTermStats);

    @POST("/stats/analysis/result")
    Observable<Response<Boolean>> sendAnalysisResult(@Header("x-appbee-number") String userId, @Body AnalysisResult analysisResult);
}
