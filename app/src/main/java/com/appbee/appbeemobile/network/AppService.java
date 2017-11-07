package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.util.AppBeeConstants.API_RESPONSE_CODE;

import java.util.List;

import javax.inject.Inject;

import retrofit2.HttpException;
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

    public void getInfos(List<String> packageNames, AppInfosServiceCallback appInfosServiceCallback) {
        appAPI.getInfo(localStorageHelper.getAccessToken(), packageNames).subscribeOn(Schedulers.io()).subscribe(appInfosServiceCallback::onSuccess, error -> {
            logError(error);

            final int errorCode = (error instanceof HttpException) ? ((HttpException) error).code() : API_RESPONSE_CODE.NOTFOUND;
            appInfosServiceCallback.onFail(String.valueOf(errorCode));
        });
    }

    public void postUncrawledApps(List<String> uncrawledPackageNameList) {
        appAPI.postUncrawledApps(localStorageHelper.getAccessToken(), uncrawledPackageNameList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> Log.d(TAG, String.valueOf(response)), this::logError);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public interface AppInfosServiceCallback {
        void onSuccess(List<AppInfo> result);

        void onFail(String errorCode);
    }
}
