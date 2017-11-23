package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
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

    public void postUncrawledApps(List<String> uncrawledPackageNameList) {
        appAPI.postUncrawledApps(localStorageHelper.getAccessToken(), uncrawledPackageNameList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response)), this::logError);
    }

    public Observable<Boolean> sendAppUsages(List<AppUsage> appUsageList) {
        return appAPI.postUsages(localStorageHelper.getAccessToken(), appUsageList)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<AppInfo>> getAppInfo(List<String> packageNameList) {
        return appAPI.getAppInfos(localStorageHelper.getAccessToken(), packageNameList)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}
