package com.appbee.appbeemobile.manager;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import com.appbee.appbeemobile.model.DailyUsageStat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.USAGE_STATS_SERVICE;

public class StatManager {
    private static final SimpleDateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    private Context context;

    public StatManager(Context context) {
        this.context = context;
    }

    // TODO: 일주일동안 사용정보 가져오기
    public UsageEvents getUserAppUsageInDetail() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        long startTime = endTime - 1000*60*60*24*7;

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        if (usm != null) {
            UsageEvents usageEvents = usm.queryEvents(startTime, endTime);

            while (usageEvents.hasNextEvent()) {
                UsageEvents.Event event = new UsageEvents.Event();
                boolean hasNextEvent = usageEvents.getNextEvent(event);

//                if (hasNextEvent) {
//                    Log.d(TAG, "[" + event.getEventType() + "]" + YEAR_DATE_FORMAT.format(event.getTimeStamp()) + "\t" + event.getPackageName());
//                }
            }
            return usageEvents;
        }

        return null;
    }

    public Map<String, DailyUsageStat> getUserAppDailyUsageStatsForYear() {
        Map<String, DailyUsageStat> dailyUsageStatMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        if (usm != null) {
            List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

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
        }
        return dailyUsageStatMap;
    }
}
