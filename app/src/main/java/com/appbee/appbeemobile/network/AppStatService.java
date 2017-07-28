package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.EventStat;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppStatService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private static final String TEST_USER_ID = "testUser";
    private StatManager statManager;
    private StatAPI StatAPI;

    @Inject
    public AppStatService(StatManager statManager, StatAPI StatAPI) {
        this.statManager = statManager;
        this.StatAPI = StatAPI;
    }

    public void sendAppList() {
        StatAPI.sendAppInfoList(TEST_USER_ID, statManager.getAppList()).enqueue(new Callback<Boolean>() {
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
        StatAPI.sendEventStats(TEST_USER_ID, eventStats).enqueue(new Callback<Boolean>() {
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
        StatAPI.sendLongTermStats(TEST_USER_ID, longTermStats).enqueue(new Callback<Boolean>() {
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
        StatAPI.sendShortTermStats(TEST_USER_ID, shortTermStats).enqueue(new Callback<Boolean>() {
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
