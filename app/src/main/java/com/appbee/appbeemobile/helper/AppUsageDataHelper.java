package com.appbee.appbeemobile.helper;

import android.app.usage.UsageStats;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.util.AppBeeConstants.CHARACTER_TYPE;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

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

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;
import static com.appbee.appbeemobile.util.AppBeeConstants.Category;

@Singleton
public class AppUsageDataHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private static final String GAME_CATEGORY_GROUP_KEY = "GAME_GROUP";
    private static final String MUSIC_VIDEO_CATEGORY_GROUP_KEY = "MV_GROUP";
    private static final String PERSONALIZATION_CATEGORY_GROUP_KEY = "PERSONALIZATION_GROUP";
    private static final String PHOTOGRAPHY_CATEGORY_GROUP_KEY = "PHOTOGRAPHY_GROUP";
    static final boolean ASC = true;
    static final boolean DESC = false;
    private static final long MILLISECONDS_OF_THREE_MONTHS = 7884000000L; // 365 * 24 * 60 * 60 * 1000 / 4

    private final AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    private final LocalStorageHelper localStorageHelper;
    private final AppRepositoryHelper appRepositoryHelper;
    private final TimeHelper timeHelper;

    @Inject
    public AppUsageDataHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper, LocalStorageHelper localStorageHelper, AppRepositoryHelper appRepositoryHelper, TimeHelper timeHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.localStorageHelper = localStorageHelper;
        this.appRepositoryHelper = appRepositoryHelper;
        this.timeHelper = timeHelper;
    }

    public List<ShortTermStat> getShortTermStats(long startTime) {
        long endTime = timeHelper.getCurrentTime();

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
        long endTime = timeHelper.getCurrentTime();
        return appBeeAndroidNativeHelper.getUsageStatEvents(startTime, endTime);
    }

    private ShortTermStat createDetailUsageStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new ShortTermStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
    }

    public List<LongTermStat> getLongTermStats() {
        Map<String, LongTermStat> dailyUsageStatMap = new LinkedHashMap<>();

        long endTime = timeHelper.getCurrentTime();
        long startTime = endTime - MILLISECONDS_OF_THREE_MONTHS;

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

    public int getAppUsageAverageMinutesPerDay(List<LongTermStat> longTermStatList) {
        long totalUsedTime = 0L;

        for (LongTermStat item : longTermStatList) {
            totalUsedTime += item.getTotalUsedTime();
        }

        totalUsedTime = totalUsedTime / 1000 / 60;
        long mobileTotalUsedDay = timeHelper.getMobileTotalUsedDay(localStorageHelper.getMinStartedStatTimeStamp());
        return (int) (totalUsedTime / mobileTotalUsedDay);
    }

    @NonNull
    public Map<String, Long> getLongTermStatsSummary() {
        Map<String, Long> map = new HashMap<>();

        for (LongTermStat longTermStat : getLongTermStats()) {
            if (map.get(longTermStat.getPackageName()) == null) {
                map.put(longTermStat.getPackageName(), longTermStat.getTotalUsedTime());
            } else {
                map.put(longTermStat.getPackageName(), map.get(longTermStat.getPackageName()) + longTermStat.getTotalUsedTime());
            }
        }

        return map;
    }

    public ArrayList<String> getMostInstalledCategoryGroups(int count) {
        Map<String, Integer> appCountMap = combineInstalledAppCountByCategoryGroup(appRepositoryHelper.getAppCountMapByCategory());
        Map<String, Integer> sortedAppCountMap = sortByValue(appCountMap, DESC);
        return getSubList(sortedAppCountMap, count);
    }

    public ArrayList<String> getLeastInstalledCategoryGroups(int count) {
        Map<String, Integer> appCountMap = combineInstalledAppCountByCategoryGroup(appRepositoryHelper.getAppCountMapByCategory());
        Map<String, Integer> sortedAppCountMap = sortByValue(appCountMap, ASC);
        return getSubList(sortedAppCountMap, count);
    }

    private ArrayList<String> getSubList(Map<String, Integer> sortedAppCountMap, int count) {
        int mapSize = sortedAppCountMap.size();
        return new ArrayList<>(Lists.newArrayList(sortedAppCountMap.keySet()).subList(0, Math.min(count, mapSize)));
    }

    public long getAppCountByCategoryId(String categoryId) {
        return appRepositoryHelper.getAppCountByCategoryId(categoryId);
    }

    public List<AppInfo> getSortedUsedAppsByTotalUsedTime() {
        return appRepositoryHelper.getSortedUsedAppsByTotalUsedTime();
    }

    public Map<String, Long> getSortedCategoriesByUsedTime() {
        return sortByValue(combineUsedTimeByCategoryGroup(appRepositoryHelper.getUsedTimeMapByCategory()), DESC);
    }

    Map<String, Long> combineUsedTimeByCategoryGroup(Map<String, Long> usedTimeMapByCategory) {
        Map<String, Long> map = new HashMap<>();

        for (String key : usedTimeMapByCategory.keySet()) {
            String groupKey = getCategoryGroupIdForCategoryId(key);
            map.put(groupKey, Optional.fromNullable(map.get(groupKey)).or(0L) + usedTimeMapByCategory.get(key));
        }

        return map;
    }

    Map<String, Integer> combineInstalledAppCountByCategoryGroup(Map<String, Integer> usedTimeMapByCategory) {
        Map<String, Integer> map = new HashMap<>();

        for (String key : usedTimeMapByCategory.keySet()) {
            String groupKey = getCategoryGroupIdForCategoryId(key);
            map.put(groupKey, Optional.fromNullable(map.get(groupKey)).or(0) + usedTimeMapByCategory.get(key));
        }

        return map;
    }

    private String getCategoryGroupIdForCategoryId(String categoryId) {
        switch (categoryId) {
            case "/store/apps/category/GAME":
            case "/store/apps/category/GAME_EDUCATIONAL":
            case "/store/apps/category/GAME_WORD":
            case "/store/apps/category/GAME_ROLE_PLAYING":
            case "/store/apps/category/GAME_BOARD":
            case "/store/apps/category/GAME_SPORTS":
            case "/store/apps/category/GAME_SIMULATION":
            case "/store/apps/category/GAME_ARCADE":
            case "/store/apps/category/GAME_ACTION":
            case "/store/apps/category/GAME_ADVENTURE":
            case "/store/apps/category/GAME_MUSIC":
            case "/store/apps/category/GAME_RACING":
            case "/store/apps/category/GAME_STRATEGY":
            case "/store/apps/category/GAME_CARD":
            case "/store/apps/category/GAME_CASINO":
            case "/store/apps/category/GAME_CASUAL":
            case "/store/apps/category/GAME_TRIVIA":
            case "/store/apps/category/GAME_PUZZLE":
                return Category.GAME.categoryId;
            case "/store/apps/category/FAMILY":
            case "/store/apps/category/FAMILY_EDUCATION":
            case "/store/apps/category/FAMILY_BRAINGAMES":
            case "/store/apps/category/FAMILY_ACTION":
            case "/store/apps/category/FAMILY_PRETEND":
            case "/store/apps/category/FAMILY_MUSICVIDEO":
            case "/store/apps/category/FAMILY_CREATE":
                return Category.FAMILY.categoryId;
            default:
                return categoryId;
        }
    }

    public int getCharacterType() {
        final Map<String, Integer> aggregatedMap = combineAppCountByBest5CategoryGroup(appRepositoryHelper.getAppCountMapByCategory());

        if (aggregatedMap == null || aggregatedMap.isEmpty()) {
            return CHARACTER_TYPE.ETC;
        }
        Map<String, Integer> sortedMap = sortByValue(aggregatedMap, DESC);

        final String topCategoryId = sortedMap.keySet().iterator().next();

        switch (topCategoryId) {
            case GAME_CATEGORY_GROUP_KEY:
                return CHARACTER_TYPE.GAMER;
            case MUSIC_VIDEO_CATEGORY_GROUP_KEY:
                return CHARACTER_TYPE.QUEEN;
            case PHOTOGRAPHY_CATEGORY_GROUP_KEY:
                return CHARACTER_TYPE.POISON;
            case PERSONALIZATION_CATEGORY_GROUP_KEY:
                return CHARACTER_TYPE.SOUL;
        }
        return CHARACTER_TYPE.ETC;
    }

    Map<String, Integer> combineAppCountByBest5CategoryGroup(Map<String, Integer> categoryMap) {
        Map<String, Integer> map = new HashMap<>();

        for (String key : categoryMap.keySet()) {
            switch (key) {
                case "/store/apps/category/GAME":
                case "/store/apps/category/GAME_EDUCATIONAL":
                case "/store/apps/category/GAME_WORD":
                case "/store/apps/category/GAME_ROLE_PLAYING":
                case "/store/apps/category/GAME_BOARD":
                case "/store/apps/category/GAME_SPORTS":
                case "/store/apps/category/GAME_SIMULATION":
                case "/store/apps/category/GAME_ARCADE":
                case "/store/apps/category/GAME_ACTION":
                case "/store/apps/category/GAME_ADVENTURE":
                case "/store/apps/category/GAME_MUSIC":
                case "/store/apps/category/GAME_RACING":
                case "/store/apps/category/GAME_STRATEGY":
                case "/store/apps/category/GAME_CARD":
                case "/store/apps/category/GAME_CASINO":
                case "/store/apps/category/GAME_CASUAL":
                case "/store/apps/category/GAME_TRIVIA":
                case "/store/apps/category/GAME_PUZZLE":
                    map.put(GAME_CATEGORY_GROUP_KEY, Optional.fromNullable(map.get(GAME_CATEGORY_GROUP_KEY)).or(0) + categoryMap.get(key));
                    break;
                case "/store/apps/category/VIDEO_PLAYERS":
                case "/store/apps/category/MUSIC_AND_AUDIO":
                    map.put(MUSIC_VIDEO_CATEGORY_GROUP_KEY, Optional.fromNullable(map.get(MUSIC_VIDEO_CATEGORY_GROUP_KEY)).or(0) + categoryMap.get(key));
                    break;
                case "/store/apps/category/PHOTOGRAPHY":
                    map.put(PHOTOGRAPHY_CATEGORY_GROUP_KEY, Optional.fromNullable(map.get(PHOTOGRAPHY_CATEGORY_GROUP_KEY)).or(0) + categoryMap.get(key));
                    break;
                case "/store/apps/category/PERSONALIZATION":
                    map.put(PERSONALIZATION_CATEGORY_GROUP_KEY, Optional.fromNullable(map.get(PERSONALIZATION_CATEGORY_GROUP_KEY)).or(0) + categoryMap.get(key));
                    break;
            }
        }

        return map;
    }

    <K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map, boolean isAsc) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()) * (isAsc ? 1 : -1));

        LinkedHashMap<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> item : list) {
            sortedMap.put(item.getKey(), item.getValue());
        }

        return sortedMap;
    }
}