package com.appbee.appbeemobile.manager;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.USAGE_STATS_SERVICE;

public class StatManager {

    private static final String TAG = StatManager.class.getSimpleName();

    private static final SimpleDateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);
    private static final SimpleDateFormat HOUR_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.KOREA);

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

                if (hasNextEvent) {
                    Log.d(TAG, "[" + event.getEventType() + "]" + YEAR_DATE_FORMAT.format(event.getTimeStamp()) + "\t" + event.getPackageName());
                }
            }
            return usageEvents;
        }

        return usm.queryEvents(startTime, endTime);
    }

    // TODO: 일년간 통계정보 가져오기
    public List<UsageStats> getUserAppUsageStats() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        if (usm != null) {
            List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);

            Log.d(TAG, "size : " + usageStatsList.size());
            for (UsageStats stats : usageStatsList) {
                if(stats.getTotalTimeInForeground() > 0) {
                    Log.d(TAG, "[YEAR] " + stats.getPackageName() + " / " + YEAR_DATE_FORMAT.format(stats.getFirstTimeStamp())
                            + " / "  + YEAR_DATE_FORMAT.format(stats.getLastTimeStamp())
                            + " / " + YEAR_DATE_FORMAT.format(stats.getLastTimeUsed())
                            + " / " + HOUR_DATE_FORMAT.format(stats.getTotalTimeInForeground()));
                }
            }
            return usageStatsList;
        }
        return null;
    }
}
