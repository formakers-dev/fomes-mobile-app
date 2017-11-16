package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class AppStatService extends AbstractAppBeeService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private StatAPI statAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppStatService(StatAPI statAPI, LocalStorageHelper localStorageHelper) {
        this.statAPI = statAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public Observable<Boolean> sendShortTermStats(List<ShortTermStat> shortTermStatList) {
        final String accessToken = localStorageHelper.getAccessToken();

        if (!shortTermStatList.isEmpty()) {
            return statAPI.sendShortTermStats(accessToken, shortTermStatList);
        } else {
            return Observable.just(true);
        }
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
