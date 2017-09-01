package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.helper.TimeHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.schedulers.Schedulers;

public class AppStatService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private AppUsageDataHelper appUsageDataHelper;
    private StatAPI StatAPI;
    private final LocalStorageHelper localStorageHelper;
    private TimeHelper timeHelper;

    @Inject
    public AppStatService(AppUsageDataHelper appUsageDataHelper, StatAPI StatAPI, LocalStorageHelper localStorageHelper, TimeHelper timeHelper) {
        this.appUsageDataHelper = appUsageDataHelper;
        this.StatAPI = StatAPI;
        this.localStorageHelper = localStorageHelper;
        this.timeHelper = timeHelper;
    }

    public void sendEventStats() {
        StatAPI.sendEventStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getEventStats(getStartTime()))
                .subscribeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response.code())), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }

    public void sendLongTermStats() {
        StatAPI.sendLongTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getLongTermStats())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response.code())), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }

    public void sendShortTermStats() {
        StatAPI.sendShortTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getShortTermStats(getStartTime()))
                .subscribeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response.code())), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }

    long getStartTime() {
        long lastUsageTime = localStorageHelper.getLastUsageTime();
        if (lastUsageTime == 0L) {
            return timeHelper.getCurrentTime() - 604800000L; // 604800000 = 7 * 24 * 60 * 60 * 1000 ( milliseconds / 1 week );
        } else {
            return lastUsageTime;
        }
    }

    public List<String> getUsedPackageNameList() {
        List<String> usedPackageNameList = new ArrayList<>();

        for (LongTermStat stat : appUsageDataHelper.getLongTermStats()) {
            usedPackageNameList.add(stat.getPackageName());
        }

        return usedPackageNameList;
    }
}
