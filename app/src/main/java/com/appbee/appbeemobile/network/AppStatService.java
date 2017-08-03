package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.util.AppBeeConstants;

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

    public void sendAppList(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendAppInfoList(localStorageHelper.getAccessToken(), appUsageDataHelper.getAppList()).enqueue(getStatCallback(appStatServiceCallback));
    }

    public void sendEventStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendEventStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getEventStats()).enqueue(getStatCallback(appStatServiceCallback));
    }

    public void sendLongTermStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendLongTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getLongTermStats()).enqueue(getStatCallback(appStatServiceCallback));
    }

    public void sendShortTermStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendShortTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getShortTermStats()).enqueue(getStatCallback(appStatServiceCallback));
    }

    private Callback<Boolean> getStatCallback(AppStatServiceCallback appStatServiceCallback) {
        return new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()) {
                    appStatServiceCallback.onSuccess();
                } else {
                    appStatServiceCallback.onFail(String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d(TAG, "failure!!! t=" + t.toString());
                appStatServiceCallback.onFail(AppBeeConstants.API_RESPONSE_CODE.FORBIDDEN);
            }
        };
    }
}
