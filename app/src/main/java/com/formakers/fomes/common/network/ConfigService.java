package com.formakers.fomes.common.network;

import com.formakers.fomes.common.helper.APIHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.network.api.ConfigAPI;

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
    private final APIHelper APIHelper;

    @Inject
    public ConfigService(ConfigAPI configAPI, SharedPreferencesHelper SharedPreferencesHelper, APIHelper APIHelper) {
        this.configAPI = configAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.APIHelper = APIHelper;
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
                .compose(APIHelper.refreshExpiredToken())
                .toSingle();
    }
}
