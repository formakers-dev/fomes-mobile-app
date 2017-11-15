package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class AppStatService extends AbstractAppBeeService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private StatAPI statAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppStatService(StatAPI statAPI, LocalStorageHelper localStorageHelper) {
        this.statAPI = statAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public Observable<Boolean> sendShortTermStats(List<ShortTermStat> shortTermStatList, long endTime) {
        final String accessToken = localStorageHelper.getAccessToken();

        if (!shortTermStatList.isEmpty()) {
            return statAPI.sendShortTermStats(accessToken, endTime, shortTermStatList);
        } else {
            return Observable.just(true);
        }
    }

    public Observable<Long> getLastUpdateStatTimestamp() {
        return statAPI.getLastUpdateStatTimestamp(localStorageHelper.getAccessToken()).subscribeOn(Schedulers.io());
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
