package com.appbee.appbeemobile.manager;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.Context;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.DetailUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public class StatManager {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    @Inject
    SystemServiceBridge systemServiceBridge;

    public StatManager(Context context) {
        ((AppBeeApplication)context.getApplicationContext()).getComponent().inject(this);
    }

    public List<DetailUsageStat> getDetailUsageStats() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        long startTime = endTime - 1000*60*60*24*7;

        List<UsageStatEvent> usageStatEvents = systemServiceBridge.getUsageStatEvents(startTime, endTime);
        List<DetailUsageStat> detailUsageStats = new ArrayList<>();

        String previousForegroundPackageName = null;
        long previousTimeStamp = 0L;

        for(UsageStatEvent usageStatEvent : usageStatEvents) {
            if(usageStatEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                previousForegroundPackageName = usageStatEvent.getPackageName();
                previousTimeStamp = usageStatEvent.getTimeStamp();
            } else if(usageStatEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                if(usageStatEvent.getPackageName().equals(previousForegroundPackageName)) {
                    long period = usageStatEvent.getTimeStamp() - previousTimeStamp;
                    detailUsageStats.add(new DetailUsageStat(usageStatEvent.getPackageName(), previousTimeStamp, usageStatEvent.getTimeStamp(), period));
                }
            }
        }

        return detailUsageStats;
    }

    public Map<String, DailyUsageStat> getUserAppDailyUsageStatsForYear() {
        Map<String, DailyUsageStat> dailyUsageStatMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = systemServiceBridge.getUsageStats(startTime, endTime);

        for (UsageStats stats : usageStatsList) {
            if(stats.getTotalTimeInForeground() > 0) {
                String packageName = stats.getPackageName();
                String usedLastDate = DATE_FORMAT.format(stats.getLastTimeUsed());
                long totalUsedTime = stats.getTotalTimeInForeground();
                String mapKey = packageName + usedLastDate;

                DailyUsageStat stat = dailyUsageStatMap.get(mapKey);
                if (stat != null) {
                    stat.setTotalUsedTime(stat.getTotalUsedTime() + totalUsedTime);
                } else {
                    stat = new DailyUsageStat(packageName, usedLastDate, totalUsedTime);
                    dailyUsageStatMap.put(mapKey, stat);
                }
            }
        }

        return dailyUsageStatMap;
    }

    public List<AppInfo> getAppList() {
        return systemServiceBridge.getInstalledLaunchableApps();
    }
}
