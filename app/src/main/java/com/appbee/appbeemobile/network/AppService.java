package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppUsage;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class AppService extends AbstractAppBeeService {
    private static final String TAG = AppService.class.getSimpleName();
    private final AppAPI appAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppService(AppAPI appAPI, LocalStorageHelper localStorageHelper) {
        this.appAPI = appAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public Observable<Boolean> sendAppUsages(List<AppUsage> appUsageList) {
        return appAPI.postUsages(localStorageHelper.getAccessToken(), appUsageList)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
