package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.util.AppBeeConstants.API_RESPONSE_CODE;

import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
import retrofit2.Response;
import rx.Observer;
import rx.schedulers.Schedulers;

public class AppService {
    private final AppAPI appAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppService(AppAPI appAPI, LocalStorageHelper localStorageHelper) {
        this.appAPI = appAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public void getInfos(List<String> packageNames, AppInfosServiceCallback appInfosServiceCallback) {
        appAPI.getInfo(localStorageHelper.getAccessToken(), packageNames).subscribeOn(Schedulers.io()).subscribe(
                new Observer<Response<List<AppInfo>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            appInfosServiceCallback.onFail(String.valueOf(((HttpException) e).code()));
                        } else {
                            appInfosServiceCallback.onFail(API_RESPONSE_CODE.NOTFOUND);
                        }
                    }

                    @Override
                    public void onNext(Response<List<AppInfo>> listResponse) {
                        if (listResponse.isSuccessful()) {
                            appInfosServiceCallback.onSuccess(listResponse.body());
                        } else {
                            appInfosServiceCallback.onFail(API_RESPONSE_CODE.FORBIDDEN);
                        }
                    }
                }
        );
    }

    public void postUncrawledApps(List<String> uncrawledPackageNameList) {
        appAPI.postUncrawledApps(localStorageHelper.getAccessToken(), uncrawledPackageNameList)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public interface AppInfosServiceCallback {
        void onSuccess(List<AppInfo> result);

        void onFail(String resultCode);
    }
}
