package com.formakers.fomes.network;

import com.formakers.fomes.helper.AppBeeAPIHelper;
import com.formakers.fomes.helper.LocalStorageHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class ConfigService {
    private ConfigAPI configAPI;
    private final LocalStorageHelper localStorageHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public ConfigService(ConfigAPI configAPI, LocalStorageHelper localStorageHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.configAPI = configAPI;
        this.localStorageHelper = localStorageHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    public Single<Long> getAppVersion() {
        return configAPI.getAppVersion()
                .subscribeOn(Schedulers.io())
                .toSingle();
    }

    public Single<List<String>> getExcludePackageNames() {
        return Observable.defer(() -> configAPI.getExcludePackageNames(localStorageHelper.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toSingle();
    }
}
