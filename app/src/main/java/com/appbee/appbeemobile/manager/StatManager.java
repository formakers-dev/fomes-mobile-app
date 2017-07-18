package com.appbee.appbeemobile.manager;

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

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

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

        UsageStatEvent beforeForegroundEvent = null;

        for(UsageStatEvent usageStatEvent : usageStatEvents) {
            switch (usageStatEvent.getEventType()) {
                case MOVE_TO_FOREGROUND :
                    if(isDifferentBeforeForegroundApp(beforeForegroundEvent, usageStatEvent)) {
                        detailUsageStats.add(createDetailUsageStat(beforeForegroundEvent.getPackageName(), beforeForegroundEvent.getTimeStamp(), usageStatEvent.getTimeStamp()));
                    }

                    beforeForegroundEvent = usageStatEvent;
                    break;

                case MOVE_TO_BACKGROUND :
                    if(beforeForegroundEvent != null && usageStatEvent.getPackageName().equals(beforeForegroundEvent.getPackageName())) {
                        detailUsageStats.add(createDetailUsageStat(usageStatEvent.getPackageName(), beforeForegroundEvent.getTimeStamp(), usageStatEvent.getTimeStamp()));
                        beforeForegroundEvent = null;
                    }
                    break;
            }
        }

        return detailUsageStats;
    }

    private DetailUsageStat createDetailUsageStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new DetailUsageStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
    }

    private boolean isDifferentBeforeForegroundApp(UsageStatEvent beforeForegroundEvent, UsageStatEvent usageStatEvent) {
        return beforeForegroundEvent != null && beforeForegroundEvent.getEventType() == MOVE_TO_FOREGROUND && !usageStatEvent.getPackageName().equals(beforeForegroundEvent.getPackageName());
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
