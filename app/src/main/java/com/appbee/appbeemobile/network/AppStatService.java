package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.helper.LocalStorageHelper;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppStatService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private AppUsageDataHelper appUsageDataHelper;
    private StatAPI StatAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppStatService(AppUsageDataHelper appUsageDataHelper, StatAPI StatAPI, LocalStorageHelper localStorageHelper) {
        this.appUsageDataHelper = appUsageDataHelper;
        this.StatAPI = StatAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public void sendAppList() {
        StatAPI.sendAppInfoList(localStorageHelper.getAccessToken(),
                appUsageDataHelper.getAppList()).enqueue(new Callback<Boolean>() {
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

    public void sendEventStats() {
        final List<EventStat> eventStats = appUsageDataHelper.getEventStats();
        StatAPI.sendEventStats(localStorageHelper.getAccessToken(), eventStats).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Success to send EventStats");
                } else {
                    Log.d(TAG, "Fail to send EventStats");
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }

    public void sendLongTermStats() {
        final List<LongTermStat> longTermStats = appUsageDataHelper.getLongTermStats();
        StatAPI.sendLongTermStats(localStorageHelper.getAccessToken(), longTermStats).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Success to send LongTermStats");
                } else {
                    Log.d(TAG, "Fail to send LongTermStats");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }

    public void sendShortTermStats() {
        List<ShortTermStat> shortTermStats = appUsageDataHelper.getShortTermStats();
        StatAPI.sendShortTermStats(localStorageHelper.getAccessToken(), shortTermStats).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Success to send shortTermStats");
                } else {
                    Log.d(TAG, "Fail to send shortTermStats");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d(TAG, "failure!!! t=" + t.toString());
            }
        });
    }
}
