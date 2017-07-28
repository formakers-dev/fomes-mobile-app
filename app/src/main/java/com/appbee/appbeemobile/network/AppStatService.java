package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.util.AppBeeConstants;
import com.appbee.appbeemobile.util.PropertyUtil;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppStatService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private StatManager statManager;
    private StatAPI StatAPI;
    private final PropertyUtil propertyUtil;

    @Inject
    public AppStatService(StatManager statManager, StatAPI StatAPI, PropertyUtil propertyUtil) {
        this.statManager = statManager;
        this.StatAPI = StatAPI;
        this.propertyUtil = propertyUtil;
    }

    public void sendAppList() {
        StatAPI.sendAppInfoList(propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_ACCESS_TOKEN, null), propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_USER_ID, null)
                , statManager.getAppList()).enqueue(new Callback<Boolean>() {
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
        StatAPI.sendEventStats(propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_ACCESS_TOKEN, null), propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_USER_ID, null), eventStats).enqueue(new Callback<Boolean>() {
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
        StatAPI.sendLongTermStats(propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_ACCESS_TOKEN, null), propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_USER_ID, null), longTermStats).enqueue(new Callback<Boolean>() {
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
        StatAPI.sendShortTermStats(propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_ACCESS_TOKEN, null), propertyUtil.getString(AppBeeConstants.SharedPreference.KEY_USER_ID, null), shortTermStats).enqueue(new Callback<Boolean>() {
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
