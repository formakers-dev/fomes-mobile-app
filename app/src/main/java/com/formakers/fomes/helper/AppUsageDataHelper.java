package com.formakers.fomes.helper;

import android.support.annotation.NonNull;

import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.DailyStatSummary;
import com.formakers.fomes.model.EventStat;
import com.formakers.fomes.model.ShortTermStat;
import com.formakers.fomes.model.StatKey;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.repository.helper.AppRepositoryHelper;
import com.formakers.fomes.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.schedulers.Schedulers;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

@Singleton
public class AppUsageDataHelper {
    public static final int DEFAULT_APP_USAGE_DURATION_DAYS = 7;

    private final AndroidNativeHelper androidNativeHelper;
    private final AppStatService appStatService;
    private final AppRepositoryHelper appRepositoryHelper;
    private final TimeHelper timeHelper;
    private final SharedPreferencesHelper SharedPreferencesHelper;

    @Inject
    public AppUsageDataHelper(AndroidNativeHelper androidNativeHelper,
                              AppStatService appStatService,
                              AppRepositoryHelper appRepositoryHelper,
                              SharedPreferencesHelper SharedPreferencesHelper,
                              TimeHelper timeHelper) {
        this.androidNativeHelper = androidNativeHelper;
        this.appStatService = appStatService;
        this.appRepositoryHelper = appRepositoryHelper;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.timeHelper = timeHelper;
    }

    @NonNull
    List<ShortTermStat> getShortTermStats(long startTime, long endTime) {
        List<EventStat> eventStats = androidNativeHelper.getUsageStatEvents(startTime, endTime);
        List<ShortTermStat> shortTermStats = new ArrayList<>();

        EventStat beforeForegroundEvent = null;

        for (EventStat eventStat : eventStats) {
            switch (eventStat.getEventType()) {
                case MOVE_TO_FOREGROUND:
                    beforeForegroundEvent = eventStat;
                    break;

                case MOVE_TO_BACKGROUND:
                    if (beforeForegroundEvent != null && eventStat.getPackageName().equals(beforeForegroundEvent.getPackageName())) {
                        shortTermStats.add(createShortTermStat(eventStat.getPackageName(), beforeForegroundEvent.getEventTime(), eventStat.getEventTime()));
                        beforeForegroundEvent = null;
                    }
                    break;
            }
        }

        return shortTermStats;
    }

    private ShortTermStat createShortTermStat(String packageName, long startTimestamp, long endTimestamp) {
        return new ShortTermStat(packageName, startTimestamp, endTimestamp, endTimestamp - startTimestamp);
    }

    public Completable sendShortTermStats() {
        final long from = SharedPreferencesHelper.getLastUpdateShortTermStatTimestamp();
        final long to = timeHelper.getStatBasedCurrentTime();
        final List<ShortTermStat> shortTermStatList = getShortTermStats(from, to);

        return appStatService.sendShortTermStats(shortTermStatList)
                .observeOn(Schedulers.io())
                .doOnCompleted(() -> SharedPreferencesHelper.setLastUpdateShortTermStatTimestamp(to));
    }

    @Deprecated
    public Completable sendAppUsages() {
        final long to = timeHelper.getStatBasedCurrentTime();
        deleteOldAppUsage();
        updateAppUsage(SharedPreferencesHelper.getLastUpdateAppUsageTimestamp(), to);

        return appStatService.sendAppUsages(appRepositoryHelper.getAppUsages())
                .observeOn(Schedulers.io())
                .doOnCompleted(() -> SharedPreferencesHelper.setLastUpdateAppUsageTimestamp(to));
    }

    private void updateAppUsage(long from, long to) {
        final List<ShortTermStat> shortTermStatList = getShortTermStats(from, to);
        appRepositoryHelper.updateAppUsages(getDailyStatSummary(shortTermStatList));
    }

    private void deleteOldAppUsage() {
        final int currentDate = Integer.parseInt(DateUtil.getDateFromTimestamp(timeHelper.getCurrentTime()));
        appRepositoryHelper.deleteAppUsages(DateUtil.calBeforeDate(currentDate, 30));
    }

    List<DailyStatSummary> getDailyStatSummary(List<ShortTermStat> shortTermStatList) {
        List<DailyStatSummary> dailyStatSummaryList = new ArrayList<>();

        Map<StatKey, Long> map = new HashMap<>();

        for (ShortTermStat shortTermStat : shortTermStatList) {
            StatKey key;
            if (DateUtil.calDateDiff(shortTermStat.getStartTimeStamp(), shortTermStat.getEndTimeStamp()) == 0) {
                key = new StatKey(shortTermStat.getPackageName(), DateUtil.getDateFromTimestamp(shortTermStat.getStartTimeStamp()));
                mergeTotalUsedTimeByStatKey(map, key, shortTermStat.getTotalUsedTime());
            } else if (DateUtil.calDateDiff(shortTermStat.getStartTimeStamp(), shortTermStat.getEndTimeStamp()) == 1) {
                String startDate = DateUtil.getDateFromTimestamp(shortTermStat.getStartTimeStamp());
                String endDate = DateUtil.getDateFromTimestamp(shortTermStat.getEndTimeStamp());

                key = new StatKey(shortTermStat.getPackageName(), startDate);
                long firstTotalUsedTime = DateUtil.getTimestampFromDate(endDate) - shortTermStat.getStartTimeStamp();
                mergeTotalUsedTimeByStatKey(map, key, firstTotalUsedTime);

                key = new StatKey(shortTermStat.getPackageName(), endDate);
                long secondTotalUsedTime = shortTermStat.getTotalUsedTime() - firstTotalUsedTime;
                mergeTotalUsedTimeByStatKey(map, key, secondTotalUsedTime);
            }
            // FIXME : 시작일 ~ 종료일 1일 초과인 경우 처리
        }

        for (StatKey statKey : map.keySet()) {
            long value = map.get(statKey);
            dailyStatSummaryList.add(new DailyStatSummary(statKey.getPackageName(), Integer.parseInt(statKey.getDate()), value));
        }

        return dailyStatSummaryList;
    }

    // 현재 시간부터 지정한 기간까지의 앱 누적 사용량을 리턴
    public List<AppUsage> getAppUsagesFor(int days) {
        long endTimestamp = timeHelper.getCurrentTime();
        long startTimestamp = endTimestamp - (days * 24 * 60 * 60 * 1000L);

        List<ShortTermStat> shortTermStats = getShortTermStats(startTimestamp, endTimestamp);
        List<AppUsage> appUsages = convertToAppUsage(shortTermStats);

        // 사용시간 순 정렬
        Collections.sort(appUsages, (o1, o2) -> {
            if (o1.getTotalUsedTime() == o2.getTotalUsedTime()) {
                return o1.getPackageName().compareTo(o2.getPackageName());
            } else {
                return (int) (o2.getTotalUsedTime() - o1.getTotalUsedTime());
            }
        });

        return appUsages;
    }

    // ShortTermStats 사용시간 누적 (리듀스) 해서 AppUsage로 바꿈
    private List<AppUsage> convertToAppUsage(List<ShortTermStat> shortTermStatList) {
        Map<String, Long> map = new HashMap<>();

        for (ShortTermStat shortTermStat : shortTermStatList) {
            if (map.containsKey(shortTermStat.getPackageName())) {
                map.put(shortTermStat.getPackageName(), map.get(shortTermStat.getPackageName()) + shortTermStat.getTotalUsedTime());
            } else {
                map.put(shortTermStat.getPackageName(), shortTermStat.getTotalUsedTime());
            }
        }

        List<AppUsage> result = new ArrayList<>();
        for (String packageName : map.keySet()) {
            result.add(new AppUsage(packageName, map.get(packageName)));
        }

        return result;
    }

    /******************************** AppBee Legacy **/
    // 1주일 치 단기통계데에터 가져오기
    @Deprecated
    public List<ShortTermStat> getWeeklyStatSummaryList() {
        long endTimestamp = timeHelper.getCurrentTime();
        long startTimestamp = endTimestamp - (7L * 24 * 60 * 60 * 1000);    // 1주일 전

        List<ShortTermStat> shortTermStatList = summaryShortTermStat(getShortTermStats(startTimestamp, endTimestamp));

        // 사용시간 순 정렬
        Collections.sort(shortTermStatList, (o1, o2) -> {
            if (o1.getTotalUsedTime() == o2.getTotalUsedTime()) {
                return o1.getPackageName().compareTo(o2.getPackageName());
            } else {
                return (int) (o2.getTotalUsedTime() - o1.getTotalUsedTime());
            }
        });

        return shortTermStatList;
    }

    // ShortTermStats 사용시간 누적 - 리듀스
    @Deprecated
    private List<ShortTermStat> summaryShortTermStat(List<ShortTermStat> shortTermStatList) {
        Map<String, Long> map = new HashMap<>();

        for (ShortTermStat shortTermStat : shortTermStatList) {
            if (map.containsKey(shortTermStat.getPackageName())) {
                map.put(shortTermStat.getPackageName(), map.get(shortTermStat.getPackageName()) + shortTermStat.getTotalUsedTime());
            } else {
                map.put(shortTermStat.getPackageName(), shortTermStat.getTotalUsedTime());
            }
        }

        List<ShortTermStat> result = new ArrayList<>();
        for (String key : map.keySet()) {
            shortTermStatList.add(new ShortTermStat(key, 0L, 0L, map.get(key)));
        }

        return result;
    }

    /************************** End of AppBee Legacy **/

    private void mergeTotalUsedTimeByStatKey(Map<StatKey, Long> map, StatKey statKey, long totalUsedTime) {
        if (map.get(statKey) != null) {
            map.put(statKey, map.get(statKey) + totalUsedTime);
        } else {
            map.put(statKey, totalUsedTime);
        }
    }
}