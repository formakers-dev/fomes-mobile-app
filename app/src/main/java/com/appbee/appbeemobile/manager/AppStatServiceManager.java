package com.appbee.appbeemobile.manager;

import android.util.Log;

import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;
import com.appbee.appbeemobile.model.UserApps;
import com.appbee.appbeemobile.network.HTTPService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppStatServiceManager {
    private static final String TAG = AppStatServiceManager.class.getSimpleName();
    private static final String TEST_USER_ID = "testUser";
    private StatManager statManager;
    private HTTPService httpService;

    @Inject
    public AppStatServiceManager(StatManager statManager, HTTPService httpService) {
        this.statManager = statManager;
        this.httpService = httpService;
    }

    public void sendAppList() {
        final UserApps userApps = new UserApps(TEST_USER_ID, statManager.getAppList());

        httpService.sendAppInfoList(userApps.getUserId(), userApps).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Success to send appList");
                } else {
                    Log.d(TAG, "Fail to send appList");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }

    public void sendDetailUsageStatsByEvent() {
        final List<UsageStatEvent> usageStatEventsList = statManager.getDetailUsageEvents();
        httpService.sendDetailUsageStatsByEvent(TEST_USER_ID, usageStatEventsList).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Success to send DetailUsageStatsByEvent");
                } else {
                    Log.d(TAG, "Fail to send DetailUsageStatsByEvent");
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }

    public void sendDailyUsageStats() {
        final List<DailyUsageStat> userAppDailyUsageStatsForYear = statManager.getUserAppDailyUsageStatsForYear();
        httpService.sendDailyUsageStats(TEST_USER_ID, userAppDailyUsageStatsForYear).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Success to send dailyUsageStats");
                } else {
                    Log.d(TAG, "Fail to send dailyUsageStats");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }
}
