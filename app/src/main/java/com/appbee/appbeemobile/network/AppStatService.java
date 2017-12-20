package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppUsage;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.schedulers.Schedulers;

public class AppStatService extends AbstractAppBeeService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private final AppAPI appAPI;
    private StatAPI statAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppStatService(AppAPI appAPI, StatAPI statAPI, LocalStorageHelper localStorageHelper) {
        this.appAPI = appAPI;
        this.statAPI = statAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public Completable sendShortTermStats(List<ShortTermStat> shortTermStatList) {
        final String accessToken = localStorageHelper.getAccessToken();

        if (!shortTermStatList.isEmpty()) {
            return statAPI.sendShortTermStats(accessToken, shortTermStatList)
                    .doOnError(this::logError)
                    .subscribeOn(Schedulers.io())
                    .toCompletable();
        } else {
            return Completable.complete();
        }
    }

    public Completable sendAppUsages(List<AppUsage> appUsageList) {
        return appAPI.postUsages(localStorageHelper.getAccessToken(), appUsageList)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
