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
import retrofit2.http.Path;

public interface StatAPI {
    @POST("/apps/{userId}")
    Call<Boolean> sendAppInfoList(@Header("x-access-token") String accessToken, @Path("userId") String userId, @Body List<AppInfo> appInfos);

    @POST("/stats/long/{userId}")
    Call<Boolean> sendLongTermStats(@Header("x-access-token") String accessToken, @Path("userId") String userId, @Body List<LongTermStat> longTermStats);

    @POST("/stats/event/{userId}")
    Call<Boolean> sendEventStats(@Header("x-access-token") String accessToken, @Path("userId") String userId, @Body List<EventStat> eventStats);

    @POST("/stats/short/{userId}")
    Call<Boolean> sendShortTermStats(@Header("x-access-token") String accessToken, @Path("userId") String userId, @Body List<ShortTermStat> shortTermStats);
}
