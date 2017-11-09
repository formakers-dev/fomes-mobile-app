package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.google.common.collect.Lists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class AppStatService extends AbstractAppBeeService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private AppUsageDataHelper appUsageDataHelper;
    private StatAPI statAPI;
    private final LocalStorageHelper localStorageHelper;
    private TimeHelper timeHelper;

    @Inject
    public AppStatService(AppUsageDataHelper appUsageDataHelper, StatAPI statAPI, LocalStorageHelper localStorageHelper, TimeHelper timeHelper) {
        this.appUsageDataHelper = appUsageDataHelper;
        this.statAPI = statAPI;
        this.localStorageHelper = localStorageHelper;
        this.timeHelper = timeHelper;
    }

    public Observable<Boolean> sendShortTermStats(long startTime) {
        final String accessToken = localStorageHelper.getAccessToken();
        final long endTime = Math.max(timeHelper.getCurrentTime() - 300000L, startTime);
        final List<ShortTermStat> shortTermStatList = appUsageDataHelper.getShortTermStats(startTime, endTime);

        if (!shortTermStatList.isEmpty()) {
            return statAPI.sendShortTermStats(accessToken, endTime, shortTermStatList);
        } else {
            return Observable.just(true);
        }
    }

    public Observable<Long> getLastUpdateStatTimestamp() {
        return statAPI.getLastUpdateStatTimestamp(localStorageHelper.getAccessToken()).subscribeOn(Schedulers.io());
    }

    public List<String> getUsedPackageNameList() {
        Set<String> usedPackageNameSet = new HashSet<>();

        for (ShortTermStat stat : appUsageDataHelper.getShortTermStats(0L)) {
            usedPackageNameSet.add(stat.getPackageName());
        }

        return Lists.newArrayList(usedPackageNameSet);
    }

    public Observable<List<ShortTermStat>> getShortTermStats() {
        long startTimeStamp = timeHelper.getCurrentTime() - TimeHelper.MILLISECONDS_OF_MONTH;
        return statAPI.getShortTermStats(localStorageHelper.getAccessToken(), startTimeStamp).subscribeOn(Schedulers.io());
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
