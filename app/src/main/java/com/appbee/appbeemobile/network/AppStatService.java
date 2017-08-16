package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.util.AppBeeConstants;
import com.appbee.appbeemobile.util.TimeUtil;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

public class AppStatService {
    private static final String TAG = AppStatService.class.getSimpleName();
    private AppUsageDataHelper appUsageDataHelper;
    private StatAPI StatAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppStatService(AppUsageDataHelper appUsageDataHelper, StatAPI StatAPI, LocalStorageHelper localStorageHelper) {
        this.appUsageDataHelper = appUsageDataHelper;
        this.StatAPI = StatAPI;
        this.localStorageHelper = localStorageHelper;
    }

    public void sendAppList(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendAppInfoList(localStorageHelper.getAccessToken(), appUsageDataHelper.getAppList())
                .subscribeOn(Schedulers.io())
                .subscribe(new StatObserver(appStatServiceCallback));
    }

    public void sendEventStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendEventStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getEventStats(getStartTime()))
                .subscribeOn(Schedulers.io())
                .subscribe(new StatObserver(appStatServiceCallback));
    }

    public void sendLongTermStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendLongTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getLongTermStats())
                .subscribeOn(Schedulers.io())
                .subscribe(new StatObserver(appStatServiceCallback));
    }

    public Observable sendShortTermStats(AppStatServiceCallback appStatServiceCallback) {
        StatAPI.sendShortTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getShortTermStats(getStartTime()))
            .subscribeOn(Schedulers.io())
            .subscribe(new StatObserver(appStatServiceCallback) {
                @Override
                public void onNext(Response<Boolean> booleanResponse) {
                    if(booleanResponse.isSuccessful()){
                        appStatServiceCallback.onSuccess();
                        localStorageHelper.setLastUsageTime(TimeUtil.getCurrentTime());
                    }else {
                        appStatServiceCallback.onFail(String.valueOf(booleanResponse.code()));
                    }
                }
            });
        return null;
    }

    long getStartTime() {
        long lastUsageTime = localStorageHelper.getLastUsageTime();
        if (lastUsageTime == 0L) {
            return TimeUtil.getCurrentTime() - 7*24*60*60*1000;
        } else {
            return lastUsageTime;
        }
    }

    private class StatObserver implements Observer<Response<Boolean>> {
        protected AppStatServiceCallback appStatServiceCallback;

        StatObserver(AppStatServiceCallback appStatServiceCallback) {
            this.appStatServiceCallback = appStatServiceCallback;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            appStatServiceCallback.onFail(AppBeeConstants.API_RESPONSE_CODE.FORBIDDEN);
        }

        @Override
        public void onNext(Response<Boolean> booleanResponse) {
            if(booleanResponse.isSuccessful()){
                appStatServiceCallback.onSuccess();
            }else {
                appStatServiceCallback.onFail(String.valueOf(booleanResponse.code()));
            }
        }
    }
}
