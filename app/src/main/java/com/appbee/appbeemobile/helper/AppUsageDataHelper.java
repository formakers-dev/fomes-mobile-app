package com.appbee.appbeemobile.helper;

import android.support.annotation.NonNull;

import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

@Singleton
public class AppUsageDataHelper {
    private final AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    private final AppStatService appStatService;
    private final AppService appService;
    private final AppRepositoryHelper appRepositoryHelper;
    private final TimeHelper timeHelper;

    @Inject
    public AppUsageDataHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper,
                              AppStatService appStatService,
                              AppService appService,
                              AppRepositoryHelper appRepositoryHelper,
                              TimeHelper timeHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.appStatService = appStatService;
        this.appService = appService;
        this.appRepositoryHelper = appRepositoryHelper;
        this.timeHelper = timeHelper;
    }

    @NonNull
    public List<ShortTermStat> getShortTermStats(long startTime, long endTime) {
        List<EventStat> eventStats = appBeeAndroidNativeHelper.getUsageStatEvents(startTime, endTime);
        List<ShortTermStat> shortTermStats = new ArrayList<>();

        EventStat beforeForegroundEvent = null;

        for (EventStat eventStat : eventStats) {
            switch (eventStat.getEventType()) {
                case MOVE_TO_FOREGROUND:
                    beforeForegroundEvent = eventStat;
                    break;

                case MOVE_TO_BACKGROUND:
                    if (beforeForegroundEvent != null && eventStat.getPackageName().equals(beforeForegroundEvent.getPackageName())) {
                        shortTermStats.add(createShortTermStat(eventStat.getPackageName(), beforeForegroundEvent.getEventTime(), eventStat.getEventTime()));
                        beforeForegroundEvent = null;
                    }
                    break;
            }
        }

        return shortTermStats;
    }

    private ShortTermStat createShortTermStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new ShortTermStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
    }

    public void sendShortTermStatAndAppUsages(SendDataCallback callback) {
        appStatService.getLastUpdateStatTimestamp()
                .observeOn(Schedulers.io())
                .subscribe(lastUpdateStatTimestamp -> {
                    final long statBasedEndTime = timeHelper.getStatBasedCurrentTime();
                    final List<ShortTermStat> shortTermStatList = getShortTermStats(lastUpdateStatTimestamp, statBasedEndTime);

                    appRepositoryHelper.updateTotalUsedTime(getShortTermStatsTimeSummary(shortTermStatList));

                    Observable.merge(
                            appStatService.sendShortTermStats(shortTermStatList, statBasedEndTime),
                            appService.sendAppUsages(appRepositoryHelper.getAppUsages())
                    ).observeOn(Schedulers.io())
                            .all(result -> true)
                            .subscribe(result -> callback.onSuccess(), error -> {
                                appStatService.logError(error);
                                callback.onFail();
                            });
                }, error -> {
                    appStatService.logError(error);
                    callback.onFail();
                });
    }

    @NonNull
    public Map<String, Long> getShortTermStatsTimeSummary(List<ShortTermStat> shortTermStatList) {
        Map<String, Long> map = new HashMap<>();

        for (ShortTermStat shortTermStat : shortTermStatList) {
            if (map.get(shortTermStat.getPackageName()) == null) {
                map.put(shortTermStat.getPackageName(), shortTermStat.getTotalUsedTime());
            } else {
                map.put(shortTermStat.getPackageName(), map.get(shortTermStat.getPackageName()) + shortTermStat.getTotalUsedTime());
            }
        }

        return map;
    }


    public interface SendDataCallback {
        void onSuccess();
        void onFail();
    }
}