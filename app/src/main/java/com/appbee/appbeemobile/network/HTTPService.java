package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.model.UserApps;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HTTPService {
    @POST("/user")
    Call<Boolean> insertUser(@Body User user);

    @POST("/apps/{userId}")
    Call<Boolean> sendAppInfoList(@Path("userId") String userId, @Body UserApps userApps);

    @POST("/stats/long/{userId}")
    Call<Boolean> sendDailyUsageStats(@Path("userId") String userId, @Body List<DailyUsageStat> userAppDailyUsageStatsForYear);

    @POST("/stats/short/{userId}")
    Call<Boolean> sendDetailUsageStatsByEvent(@Path("userId") String userId, @Body List<UsageStatEvent> detailUsageStatsByEvent);
}
