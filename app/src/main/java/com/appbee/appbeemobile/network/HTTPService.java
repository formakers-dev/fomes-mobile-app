package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.DetailUsageStat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HTTPService {
    @POST("detailUsageStats")
    Call<Boolean> sendDetailUsageStat(@Body List<DetailUsageStat> detailUsageStats);
}
