package com.appbee.appbeemobile.manager;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.DetailUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;
import com.appbee.appbeemobile.util.TimeUtil;

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

    @VisibleForTesting
    final Context context;

    @Inject
    SystemServiceBridge systemServiceBridge;

    public StatManager(Context context) {
        ((AppBeeApplication)context.getApplicationContext()).getComponent().inject(this);
        this.context = context;
    }

    public List<DetailUsageStat> getDetailUsageStats() {
        long endTime = TimeUtil.getCurrentTime();
        long startTime = getStartTime();

        List<UsageStatEvent> usageStatEvents = systemServiceBridge.getUsageStatEvents(startTime, endTime);
        List<DetailUsageStat> detailUsageStats = new ArrayList<>();

        UsageStatEvent beforeForegroundEvent = null;

        for(UsageStatEvent usageStatEvent : usageStatEvents) {
            switch (usageStatEvent.getEventType()) {
                case MOVE_TO_FOREGROUND :
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

        updateEndTimeInSharedPreferences(endTime);

        return detailUsageStats;
    }

    public List<UsageStatEvent> getDetailUsageEvents(){
        long endTime = TimeUtil.getCurrentTime();
        long startTime = getStartTime();
        return systemServiceBridge.getUsageStatEvents(startTime, endTime);
    }

    private long getStartTime() {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_prefereces), Context.MODE_PRIVATE);
        return sharedPref.getLong(context.getString(R.string.shared_prefereces_key_last_usage_time), TimeUtil.getCurrentTime() - 1000*60*60*24*7);
    }

    private void updateEndTimeInSharedPreferences(long endTime) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_prefereces), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.shared_prefereces_key_last_usage_time), endTime);
        editor.commit();
    }

    private DetailUsageStat createDetailUsageStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new DetailUsageStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
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
