package com.appbee.appbeemobile.manager;

import android.util.Log;

import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.EventStat;
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
        httpService.sendAppInfoList(TEST_USER_ID, statManager.getAppList()).enqueue(new Callback<Boolean>() {
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
        final List<EventStat> eventStats = statManager.getEventStats();
        httpService.sendEventStats(TEST_USER_ID, eventStats).enqueue(new Callback<Boolean>() {
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
        final List<LongTermStat> longTermStats = statManager.getLongTermStatsForYear();
        httpService.sendLongTermStats(TEST_USER_ID, longTermStats).enqueue(new Callback<Boolean>() {
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
        List<ShortTermStat> shortTermStats = statManager.getShortTermStats();
        httpService.sendShortTermStats(TEST_USER_ID, shortTermStats).enqueue(new Callback<Boolean>() {
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
