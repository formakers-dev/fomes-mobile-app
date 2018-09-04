package com.formakers.fomes.network;

import com.formakers.fomes.helper.AppBeeAPIHelper;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.model.ShortTermStat;

import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AppStatService extends AbstractAppBeeService {
    private static final String TAG = "AppStatService";
    private final StatAPI statAPI;
    private final LocalStorageHelper localStorageHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public AppStatService(StatAPI statAPI, LocalStorageHelper localStorageHelper, AppBeeAPIHelper appBeeAPIHelper) {
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
        return Observable.defer(() -> statAPI.postUsages(localStorageHelper.getAccessToken(), appUsageList))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Observable<List<AppUsage>> requestAppUsageByCategory(String categoryId) {
        return  Observable.defer(() -> statAPI.getAppUsageByCategory(localStorageHelper.getAccessToken(), categoryId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<CategoryUsage>> requestCategoryUsage() {
        return  Observable.defer(() -> statAPI.getCategoryUsage(localStorageHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}