package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.helper.APIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.model.ShortTermStat;
import com.formakers.fomes.common.network.api.StatAPI;
import com.formakers.fomes.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class AppStatService extends AbstractService {
    private static final String TAG = "AppStatService";
    private final StatAPI statAPI;
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final APIHelper APIHelper;

    @Inject
    public AppStatService(StatAPI statAPI, SharedPreferencesHelper SharedPreferencesHelper, APIHelper APIHelper) {
        this.statAPI = statAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.APIHelper = APIHelper;
    }

    public Completable sendShortTermStats(List<ShortTermStat> shortTermStatList) {
        final String accessToken = SharedPreferencesHelper.getAccessToken();

        if (!shortTermStatList.isEmpty()) {
            return Observable.defer(() -> statAPI.sendShortTermStats(accessToken, shortTermStatList))
                    .doOnError(this::logError)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .compose(APIHelper.refreshExpiredToken())
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
                .compose(APIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Observable<RecentReport> requestRecentReport(String categoryId, User user) {
        return Observable.defer(() -> statAPI.getRecentReport(SharedPreferencesHelper.getAccessToken(), categoryId, user))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken());
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
