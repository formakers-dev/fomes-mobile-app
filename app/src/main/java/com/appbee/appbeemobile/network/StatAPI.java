package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface StatAPI {
    @POST("/apps")
    Call<Boolean> sendAppInfoList(@Header("x-access-token") String accessToken, @Body List<AppInfo> appInfos);

    @POST("/stats/long")
    Call<Boolean> sendLongTermStats(@Header("x-access-token") String accessToken, @Body List<LongTermStat> longTermStats);

    @POST("/stats/event")
    Call<Boolean> sendEventStats(@Header("x-access-token") String accessToken, @Body List<EventStat> eventStats);

    @POST("/stats/short")
    Call<Boolean> sendShortTermStats(@Header("x-access-token") String accessToken, @Body List<ShortTermStat> shortTermStats);
}
