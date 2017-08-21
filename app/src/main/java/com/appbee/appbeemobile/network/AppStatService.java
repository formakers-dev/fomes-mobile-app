package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
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

    public void sendEventStats(ServiceCallback serviceCallback) {
        StatAPI.sendEventStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getEventStats(getStartTime()))
                .subscribeOn(Schedulers.io())
                .subscribe(new BooleanResponseObserver(serviceCallback));
    }

    public void sendLongTermStats(ServiceCallback serviceCallback) {
        StatAPI.sendLongTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getLongTermStats())
                .subscribeOn(Schedulers.io())
                .subscribe(new BooleanResponseObserver(serviceCallback));
    }

    public Observable sendShortTermStats(ServiceCallback serviceCallback) {
        StatAPI.sendShortTermStats(localStorageHelper.getAccessToken(), appUsageDataHelper.getShortTermStats(getStartTime()))
            .subscribeOn(Schedulers.io())
            .subscribe(new BooleanResponseObserver(serviceCallback) {
                @Override
                public void onNext(Response<Boolean> booleanResponse) {
                    if(booleanResponse.isSuccessful()){
                        this.serviceCallback.onSuccess();
                        localStorageHelper.setLastUsageTime(TimeUtil.getCurrentTime());
                    }else {
                        this.serviceCallback.onFail(String.valueOf(booleanResponse.code()));
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

    public List<String> getUsedPackageNameList() {
        List<String> usedPackageNameList = new ArrayList<>();

        for(LongTermStat stat : appUsageDataHelper.getLongTermStats()) {
            usedPackageNameList.add(stat.getPackageName());
        }

        return usedPackageNameList;
    }
}
