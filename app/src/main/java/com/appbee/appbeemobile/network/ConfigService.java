package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.schedulers.Schedulers;

@Singleton
public class ConfigService {
    private ConfigAPI configAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public ConfigService(ConfigAPI configAPI, LocalStorageHelper localStorageHelper) {
        this.configAPI = configAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public long getAppVersion() {
        return configAPI.getAppVersion().subscribeOn(Schedulers.io()).toBlocking().value();
    }

    public List<String> getExcludePackageNames() {
        return configAPI.getExcludePackageNames(localStorageHelper.getAccessToken()).subscribeOn(Schedulers.io()).toBlocking().value();
    }
}
