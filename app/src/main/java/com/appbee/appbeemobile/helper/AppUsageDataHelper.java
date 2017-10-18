package com.appbee.appbeemobile.helper;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.NativeLongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

@Singleton
public class AppUsageDataHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    static final boolean ASC = true;
    static final boolean DESC = false;
    private static final long MILLISECONDS_OF_THREE_MONTHS = 7884000000L; // 365 * 24 * 60 * 60 * 1000 / 4
    private static final long MILLISECONDS_OF_TWO_YEARS = 63072000000L;

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

    public List<LongTermStat> getLongTermStats() {
        Map<String, LongTermStat> dailyUsageStatMap = new LinkedHashMap<>();

        long endTime = timeHelper.getCurrentTime();
        long startTime = endTime - MILLISECONDS_OF_THREE_MONTHS;

        List<UsageStats> usageStatsList = appBeeAndroidNativeHelper.getUsageStats(UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime);

        long minFirstStartedStatTimeStamp = Long.MAX_VALUE;
        for (UsageStats stats : usageStatsList) {
            minFirstStartedStatTimeStamp = Math.min(minFirstStartedStatTimeStamp, stats.getFirstTimeStamp());
            if (stats.getTotalTimeInForeground() > 0) {
                String packageName = stats.getPackageName();
                String usedLastDate = DATE_FORMAT.format(stats.getLastTimeUsed());
                long totalUsedTime = stats.getTotalTimeInForeground();
                String mapKey = packageName + usedLastDate;

                LongTermStat stat = dailyUsageStatMap.get(mapKey);
                if (stat != null) {
                    stat.setTotalUsedTime(stat.getTotalUsedTime() + totalUsedTime);
                } else {
                    stat = new LongTermStat(packageName, usedLastDate, totalUsedTime);
                    dailyUsageStatMap.put(mapKey, stat);
                }
            }
        }

        List<LongTermStat> longTermStatList = new ArrayList<>(dailyUsageStatMap.values());
        Collections.sort(longTermStatList, (o1, o2) -> Long.valueOf(o2.getTotalUsedTime()).compareTo(o1.getTotalUsedTime()));
        return longTermStatList;
    }


    public List<NativeLongTermStat> getLongTermStatsFor3Months() {
        long endTime = timeHelper.getCurrentTime();
        long startTime = endTime - MILLISECONDS_OF_THREE_MONTHS;

        List<UsageStats> usageStatsList = appBeeAndroidNativeHelper.getUsageStats(UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime);

        List<NativeLongTermStat> nativeLongTermStatList = new ArrayList<>();
        for (UsageStats stats : usageStatsList) {
            nativeLongTermStatList.add(new NativeLongTermStat(stats));
        }

        return nativeLongTermStatList;
    }

    public List<NativeLongTermStat> getNativeLongTermStatsFor2Years() {
        long endTime = timeHelper.getCurrentTime();
        long startTime = endTime - MILLISECONDS_OF_TWO_YEARS;
        List<UsageStats> usageStatsList = appBeeAndroidNativeHelper.getUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);

        List<NativeLongTermStat> nativeLongTermStatList = new ArrayList<>();
        for (UsageStats usageStats : usageStatsList) {
            nativeLongTermStatList.add(new NativeLongTermStat(usageStats));
        }

        return nativeLongTermStatList;
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