package com.appbee.appbeemobile.helper;

import android.app.usage.UsageStats;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.util.TimeUtil;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

@Singleton
public class AppUsageDataHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private static final int FROM_YEAR_FOR_LONG_TERM_STAT = 2;
    static final boolean ASC = true;
    static final boolean DESC = false;

    private final AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    private final LocalStorageHelper localStorageHelper;

    private final AppRepositoryHelper appRepositoryHelper;

    @Inject
    public AppUsageDataHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper, LocalStorageHelper localStorageHelper, AppRepositoryHelper appRepositoryHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.localStorageHelper = localStorageHelper;
        this.appRepositoryHelper = appRepositoryHelper;
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

        List<LongTermStat> longTermStatList = new ArrayList<>(dailyUsageStatMap.values());
        Collections.sort(longTermStatList, (o1, o2) -> Long.valueOf(o2.getTotalUsedTime()).compareTo(o1.getTotalUsedTime()));
        return longTermStatList;
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

    public int getAppUsageAverageHourPerDay(List<LongTermStat> longTermStatList) {
        long totalUsedTime = 0L;

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

    @NonNull
    public Map<String, Long> getLongTermStatsSummary() {
        Map<String, Long> map = new HashMap<>();

        for (LongTermStat longTermStat : getLongTermStats()) {
            if(map.get(longTermStat.getPackageName()) == null){
                map.put(longTermStat.getPackageName(), longTermStat.getTotalUsedTime());
            } else {
                map.put(longTermStat.getPackageName(), map.get(longTermStat.getPackageName()) + longTermStat.getTotalUsedTime());
            }
        }

        return map;
    }

    Map<String, Integer> sortByValue(Map<String, Integer> map, boolean isAsc) {
        List<Map.Entry<String, Integer>> categorylist = new ArrayList<>(map.entrySet());
        Collections.sort(categorylist, (o1, o2) -> o1.getValue().compareTo(o2.getValue()) * (isAsc ? 1 : -1));

        LinkedHashMap<String, Integer> sortedCategoryMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> item : categorylist) {
            sortedCategoryMap.put(item.getKey(), item.getValue());
        }

        return sortedCategoryMap;
    }

    public ArrayList<String> getMostInstalledCategories(int count) {
        Map<String, Integer> sortedAppCountMap = sortByValue(appRepositoryHelper.getAppCountMapByCategory(), DESC);
        int mapSize = sortedAppCountMap.size();
        return new ArrayList<>(Lists.newArrayList(sortedAppCountMap.keySet()).subList(0, Math.min(count, mapSize)));
    }

    public ArrayList<String> getLeastInstalledCategories(int count) {
        Map<String, Integer> sortedAppCountMap = sortByValue(appRepositoryHelper.getAppCountMapByCategory(), ASC);
        int mapSize = sortedAppCountMap.size();
        return new ArrayList<>(Lists.newArrayList(sortedAppCountMap.keySet()).subList(0, Math.min(count, mapSize)));
    }

    public long getAppCountByCategoryId(String categoryId) {
        return appRepositoryHelper.getAppCountByCategoryId(categoryId);
    }
}