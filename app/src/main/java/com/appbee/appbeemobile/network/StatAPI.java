package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.NativeLongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface StatAPI {
    @POST("/stats/long/yearly")
    Observable<Boolean> sendLongTermStatsYearly(@Header("x-access-token") String accessToken, @Body List<NativeLongTermStat> longTermStats);

    @POST("/stats/long/monthly")
    Observable<Boolean> sendLongTermStatsMonthly(@Header("x-access-token") String accessToken, @Body List<NativeLongTermStat> longTermStats);

    @POST("/stats/short")
    Observable<Boolean> sendShortTermStats(@Header("x-access-token") String accessToken, @Header("x-last-updated-time") long lastUpdatedTime, @Body List<ShortTermStat> shortTermStats);

    @GET("/stats/short/lastUpdateStatTimestamp")
    Observable<Long> getLastUpdateStatTimestamp(@Header("x-access-token") String accessToken);

    @GET("/stats/analysis/averageUsedMinutesPerDay")
    Observable<Integer> getAverageUsedMinutesPerDay(@Header("x-access-token") String accessToken);
}