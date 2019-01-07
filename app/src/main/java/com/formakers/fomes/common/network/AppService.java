package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.AppAPI;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.AppInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class AppService {

    private AppAPI appAPI;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public AppService(AppAPI appAPI, SharedPreferencesHelper sharedPreferencesHelper) {
        this.appAPI = appAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    public Single<AppInfo> requestAppInfo(String packageName) {
        return Observable.defer(() -> appAPI.getAppInfo(sharedPreferencesHelper.getAccessToken(), packageName)
                .subscribeOn(Schedulers.io()))
                .toSingle();
    }

}
