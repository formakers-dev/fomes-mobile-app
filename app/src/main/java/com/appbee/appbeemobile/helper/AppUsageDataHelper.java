package com.appbee.appbeemobile.helper;

import android.app.usage.UsageStats;
import android.support.annotation.StringRes;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
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
    private static final int FROM_YEAR_FOR_LONG_TERM_STAT = 2;

    private final AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppUsageDataHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper, LocalStorageHelper localStorageHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.localStorageHelper = localStorageHelper;
    }

    public List<ShortTermStat> getShortTermStats(long startTime) {
        long endTime = TimeUtil.getCurrentTime();

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
                        shortTermStats.add(createDetailUsageStat(eventStat.getPackageName(), beforeForegroundEvent.getTimeStamp(), eventStat.getTimeStamp()));
                        beforeForegroundEvent = null;
                    }
                    break;
            }
        }

        return shortTermStats;
    }

    public List<EventStat> getEventStats(long startTime) {
        long endTime = TimeUtil.getCurrentTime();
        return appBeeAndroidNativeHelper.getUsageStatEvents(startTime, endTime);
    }

    private ShortTermStat createDetailUsageStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new ShortTermStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
    }

    public List<LongTermStat> getLongTermStats() {
        Map<String, LongTermStat> dailyUsageStatMap = new LinkedHashMap<>();

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1 * FROM_YEAR_FOR_LONG_TERM_STAT);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = appBeeAndroidNativeHelper.getUsageStats(startTime, endTime);

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

        localStorageHelper.setMinStartedStatTimeStamp(minFirstStartedStatTimeStamp);

        return Observable.from(dailyUsageStatMap.values())
                .toList()
                .toBlocking()
                .single();
    }

    public List<NativeAppInfo> getAppList() {
        return appBeeAndroidNativeHelper.getInstalledLaunchableApps();
    }

    public @StringRes int getAppCountMessage(int appCount) {
        if (appCount < 100) {
            return R.string.app_count_few_msg;
        } else if (appCount > 300) {
            return R.string.app_count_many_msg;
        } else {
            return R.string.app_count_proper_msg;
        }
    }

    public int getAppUsageAverageHourPerDay() {
        long totalUsedTime = 0L;

        List<LongTermStat> longTermStatList = this.getLongTermStats();

        for (LongTermStat item : longTermStatList) {
            totalUsedTime += item.getTotalUsedTime();
        }

        totalUsedTime = totalUsedTime / 1000 / 60 / 60;
        long mobileTotalUsedDay = TimeUtil.getMobileTotalUsedDay(localStorageHelper.getMinStartedStatTimeStamp());
        return Math.round(totalUsedTime / (float) mobileTotalUsedDay);
    }

    public @StringRes int getAppUsageAverageMessage(int hour) {
        if (hour < 2) {
            return R.string.app_usage_average_time_few_msg;
        } else if (hour > 5) {
            return R.string.app_usage_average_time_many_msg;
        } else {
            return R.string.app_usage_average_time_proper_msg;
        }
    }

}