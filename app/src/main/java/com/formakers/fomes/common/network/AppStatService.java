package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.helper.AppBeeAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.model.ShortTermStat;
import com.formakers.fomes.common.network.api.StatAPI;
import com.formakers.fomes.model.User;

import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AppStatService extends AbstractAppBeeService {
    private static final String TAG = "AppStatService";
    private final StatAPI statAPI;
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public AppStatService(StatAPI statAPI, SharedPreferencesHelper SharedPreferencesHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.statAPI = statAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    public Completable sendShortTermStats(List<ShortTermStat> shortTermStatList) {
        final String accessToken = SharedPreferencesHelper.getAccessToken();

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
        return Observable.defer(() -> statAPI.postUsages(SharedPreferencesHelper.getAccessToken(), appUsageList))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Observable<List<AppUsage>> requestAppUsageByCategory(String categoryId) {
        return  Observable.defer(() -> statAPI.getAppUsageByCategory(SharedPreferencesHelper.getAccessToken(), categoryId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<CategoryUsage>> requestCategoryUsage() {
        return  Observable.defer(() -> statAPI.getCategoryUsage(SharedPreferencesHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<CategoryUsage>> requestPeopleCategoryUsage(final String peopleGroupFilter) {
        return  Observable.defer(() -> statAPI.getCategoryUsage(SharedPreferencesHelper.getAccessToken(), peopleGroupFilter))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<RecentReport> requestRecentReport(User user) {
        return Observable.defer(() -> statAPI.getRecentReport(SharedPreferencesHelper.getAccessToken(), user))
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
