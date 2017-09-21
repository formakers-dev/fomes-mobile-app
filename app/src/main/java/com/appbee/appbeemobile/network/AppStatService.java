package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AnalysisResult;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.google.common.collect.Lists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void sendLongTermStatsFor2Years() {
        StatAPI.sendLongTermStatsYearly(localStorageHelper.getAccessToken(), appUsageDataHelper.getNativeLongTermStatsFor2Years())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response)), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }

    public void sendLongTermStatsFor3Months() {
        StatAPI.sendLongTermStatsMonthly(localStorageHelper.getAccessToken(), appUsageDataHelper.getLongTermStatsFor3Months())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response)), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }

    public void sendShortTermStats() {
        StatAPI.sendShortTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getShortTermStats(getStartTime()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response)), error -> {
                    if (error instanceof HttpException) {
                        Log.d(TAG, String.valueOf(((HttpException) error).code()));
                    }
                });
    }

    public void sendAnalysisResult(AnalysisResult analysisResult) {
        StatAPI.sendAnalysisResult(localStorageHelper.getAccessToken(), analysisResult)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response)), error -> {
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
        Set<String> usedPackageNameSet = new HashSet<>();

        for (LongTermStat stat : appUsageDataHelper.getLongTermStats()) {
            usedPackageNameSet.add(stat.getPackageName());
        }

        return Lists.newArrayList(usedPackageNameSet);
    }
}
