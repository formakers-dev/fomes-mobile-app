package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppBeeAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppUsage;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AppStatService extends AbstractAppBeeService {
    private static final String TAG = "AppStatService";
    private final AppAPI appAPI;
    private final StatAPI statAPI;
    private final LocalStorageHelper localStorageHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public AppStatService(AppAPI appAPI, StatAPI statAPI, LocalStorageHelper localStorageHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.appAPI = appAPI;
        this.statAPI = statAPI;
        this.localStorageHelper = localStorageHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    public Completable sendShortTermStats(List<ShortTermStat> shortTermStatList) {
        final String accessToken = localStorageHelper.getAccessToken();

        if (!shortTermStatList.isEmpty()) {
            return Observable.defer(() -> statAPI.sendShortTermStats(accessToken, shortTermStatList))
                    .doOnError(this::logError)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .compose(appBeeAPIHelper.refreshExpiredToken())
                    .toCompletable();
        } else {
            return Completable.complete();
        }
    }

    public Completable sendAppUsages(List<AppUsage> appUsageList) {
        return Observable.defer(() -> appAPI.postUsages(localStorageHelper.getAccessToken(), appUsageList))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
