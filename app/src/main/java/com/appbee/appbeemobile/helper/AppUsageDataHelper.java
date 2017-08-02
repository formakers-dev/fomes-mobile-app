package com.appbee.appbeemobile.helper;

import android.app.usage.UsageStats;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

@Singleton
public class AppUsageDataHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    private final AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppUsageDataHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper, LocalStorageHelper localStorageHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.localStorageHelper = localStorageHelper;
    }

    public List<ShortTermStat> getShortTermStats() {
        long endTime = TimeUtil.getCurrentTime();
        long startTime = getAnWeekAgo();

        List<EventStat> eventStats = appBeeAndroidNativeHelper.getUsageStatEvents(startTime, endTime);
        List<ShortTermStat> shortTermStats = new ArrayList<>();

        EventStat beforeForegroundEvent = null;

        for(EventStat eventStat : eventStats) {
            switch (eventStat.getEventType()) {
                case MOVE_TO_FOREGROUND :
                    beforeForegroundEvent = eventStat;
                    break;

                case MOVE_TO_BACKGROUND :
                    if(beforeForegroundEvent != null && eventStat.getPackageName().equals(beforeForegroundEvent.getPackageName())) {
                        shortTermStats.add(createDetailUsageStat(eventStat.getPackageName(), beforeForegroundEvent.getTimeStamp(), eventStat.getTimeStamp()));
                        beforeForegroundEvent = null;
                    }
                    break;
            }
        }

        updateEndTimeInSharedPreferences(endTime);

        return shortTermStats;
    }

    public List<EventStat> getEventStats(){
        long endTime = TimeUtil.getCurrentTime();
        long startTime = getAnWeekAgo();
        return appBeeAndroidNativeHelper.getUsageStatEvents(startTime, endTime);
    }

    private long getAnWeekAgo() {
        long lastUsageTime = localStorageHelper.getLastUsageTime();
        if (lastUsageTime <= 0) {
            lastUsageTime = TimeUtil.getCurrentTime() - 1000*60*60*24*7;
        }
        return lastUsageTime;
    }

    private void updateEndTimeInSharedPreferences(long endTime) {
        localStorageHelper.setLastUsageTime(endTime);
    }

    private ShortTermStat createDetailUsageStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new ShortTermStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
    }

    public List<LongTermStat> getLongTermStatsForYear() {
        Map<String, LongTermStat> dailyUsageStatMap = new LinkedHashMap<>();

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = appBeeAndroidNativeHelper.getUsageStats(startTime, endTime);

        for (UsageStats stats : usageStatsList) {
            if(stats.getTotalTimeInForeground() > 0) {
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

        return Observable.from(dailyUsageStatMap.values())
                .toList()
                .toBlocking()
                .single();
    }

    public List<AppInfo> getAppList() {
        return appBeeAndroidNativeHelper.getInstalledLaunchableApps();
    }
}