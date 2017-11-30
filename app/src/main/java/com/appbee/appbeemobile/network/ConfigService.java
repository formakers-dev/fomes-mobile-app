package com.appbee.appbeemobile.network;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.schedulers.Schedulers;

@Singleton
public class ConfigService {
    private ConfigAPI configAPI;

    @Inject
    public ConfigService(ConfigAPI configAPI) {
        this.configAPI = configAPI;
    }

    public long getAppVersion() {
        return configAPI.getAppVersion().subscribeOn(Schedulers.io()).toBlocking().value();
    }
}
