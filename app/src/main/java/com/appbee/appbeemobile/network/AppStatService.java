package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.util.AppBeeConstants;
import com.appbee.appbeemobile.util.TimeUtil;

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
        StatAPI.sendAppInfoList(localStorageHelper.getAccessToken(), appUsageDataHelper.getAppList()).enqueue(new StatAPICallback(appStatServiceCallback));
    }

    public void sendEventStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendEventStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getEventStats(getStartTime())).enqueue(new StatAPICallback(appStatServiceCallback));
    }

    public void sendLongTermStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendLongTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getLongTermStats()).enqueue(new StatAPICallback(appStatServiceCallback));
    }

    public void sendShortTermStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendShortTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getShortTermStats(getStartTime())).enqueue(new ShortTermStatAPICallback(appStatServiceCallback));
    }

    long getStartTime() {
        long lastUsageTime = localStorageHelper.getLastUsageTime();
        if (lastUsageTime == 0L) {
            return TimeUtil.getCurrentTime() - 7*24*60*60*1000;
        } else {
            return lastUsageTime;
        }
    }

    private class StatAPICallback implements Callback<Boolean> {
        private AppStatServiceCallback appStatServiceCallback;

        StatAPICallback(AppStatServiceCallback appStatServiceCallback) {
            this.appStatServiceCallback = appStatServiceCallback;
        }

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
            appStatServiceCallback.onFail(AppBeeConstants.API_RESPONSE_CODE.FORBIDDEN);
        }
    }

    private class ShortTermStatAPICallback extends StatAPICallback {
        ShortTermStatAPICallback(AppStatServiceCallback appStatServiceCallback) {
            super(appStatServiceCallback);
        }

        @Override
        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            super.onResponse(call, response);
            if(response.isSuccessful()) {
                localStorageHelper.setLastUsageTime(TimeUtil.getCurrentTime());
            }
        }
    }
}
