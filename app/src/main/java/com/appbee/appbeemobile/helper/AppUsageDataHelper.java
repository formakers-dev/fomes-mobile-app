package com.appbee.appbeemobile.helper;

import android.support.annotation.NonNull;

import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.ShortTermStat;

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
    private final TimeHelper timeHelper;

    @Inject
    public AppUsageDataHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper, TimeHelper timeHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.timeHelper = timeHelper;
    }

    public List<ShortTermStat> getShortTermStats(long startTime) {
        return getShortTermStats(startTime, timeHelper.getCurrentTime());
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
                        shortTermStats.add(createDetailUsageStat(eventStat.getPackageName(), beforeForegroundEvent.getEventTime(), eventStat.getEventTime()));
                        beforeForegroundEvent = null;
                    }
                    break;
            }
        }

        return shortTermStats;
    }

    private ShortTermStat createDetailUsageStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new ShortTermStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
    }

    public Observable<List<NativeAppInfo>> getAppList() {
        return appBeeAndroidNativeHelper.getInstalledLaunchableApps()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .toList();
    }

    @NonNull
    public Map<String, Long> getShortTermStatsTimeSummary() {
        Map<String, Long> map = new HashMap<>();

        for (ShortTermStat shortTermStat : getShortTermStats(0L)) {
            if (map.get(shortTermStat.getPackageName()) == null) {
                map.put(shortTermStat.getPackageName(), shortTermStat.getTotalUsedTime());
            } else {
                map.put(shortTermStat.getPackageName(), map.get(shortTermStat.getPackageName()) + shortTermStat.getTotalUsedTime());
            }
        }

        return map;
    }
}