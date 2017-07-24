package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.DetailUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HTTPService {
    @POST("appInfoList")
    Call<Boolean> sendAppInfoList(@Body List<AppInfo> appInfoList);

    //@POST("{id}/detailUsageStats")
    @POST("detailUsageStats")
    Call<Boolean> sendDetailUsageStat(@Body List<DetailUsageStat> detailUsageStats);

    @POST("dailyUsageStats")
    Call<Boolean> sendDailyUsageStats(@Body Map<String, DailyUsageStat> userAppDailyUsageStatsForYear);

    @POST("dailyUsageStatsByEvent")
    Call<Boolean> sendDetailUsageStatsByEvent(@Body List<UsageStatEvent> detailUsageStatsByEvent);
}
