package com.formakers.fomes.network;

import com.formakers.fomes.helper.AppBeeAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.network.api.ConfigAPI;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class ConfigService {
    private ConfigAPI configAPI;
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public ConfigService(ConfigAPI configAPI, SharedPreferencesHelper SharedPreferencesHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.configAPI = configAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    public Single<Long> getAppVersion() {
        return configAPI.getAppVersion()
                .subscribeOn(Schedulers.io())
                .toSingle();
    }

    public Single<List<String>> getExcludePackageNames() {
        return Observable.defer(() -> configAPI.getExcludePackageNames(SharedPreferencesHelper.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toSingle();
    }
}
