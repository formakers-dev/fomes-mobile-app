package com.formakers.fomes.common.network;

import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.helper.TimeHelper;
import com.formakers.fomes.common.model.AppUsage;
import com.formakers.fomes.common.model.ShortTermStat;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.api.StatAPI;
import com.formakers.fomes.common.network.helper.APIHelper;
import com.formakers.fomes.common.network.vo.RecentReport;

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
    private final TimeHelper timeHelper;

    @Inject
    public AppStatService(StatAPI statAPI, SharedPreferencesHelper SharedPreferencesHelper, APIHelper APIHelper, TimeHelper timeHelper) {
        this.statAPI = statAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.APIHelper = APIHelper;
        this.timeHelper = timeHelper;
    }

    public Completable sendShortTermStats(Observable<ShortTermStat> shortTermStats) {
        return Observable.defer(() -> shortTermStats.buffer(500)
                    .flatMap(subShortTermStats -> statAPI.sendShortTermStats(SharedPreferencesHelper.getAccessToken(), subShortTermStats))
                )
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken())
                .toCompletable()
                .doOnCompleted(() -> SharedPreferencesHelper.setLastUpdateShortTermStatTimestamp(timeHelper.getStatBasedCurrentTime()));
    }

    public Completable sendAppUsages(Observable<AppUsage> appUsages) {
        return Observable.defer(() -> appUsages.buffer(500)
                    .flatMap(subAppUsages -> statAPI.postUsages(SharedPreferencesHelper.getAccessToken(), subAppUsages))
                )
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
