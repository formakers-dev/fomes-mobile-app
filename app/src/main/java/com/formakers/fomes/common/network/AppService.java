package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.AppAPI;
import com.formakers.fomes.common.helper.APIHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.model.AppInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class AppService {

    private AppAPI appAPI;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private APIHelper apiHelper;

    @Inject
    public AppService(AppAPI appAPI, SharedPreferencesHelper sharedPreferencesHelper, APIHelper apiHelper) {
        this.appAPI = appAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.apiHelper = apiHelper;
    }

    public Single<AppInfo> requestAppInfo(String packageName) {
        return Observable.defer(() -> appAPI.getAppInfo(sharedPreferencesHelper.getAccessToken(), packageName)
                .subscribeOn(Schedulers.io()))
                .compose(apiHelper.refreshExpiredToken())
                .toSingle();
    }

}
