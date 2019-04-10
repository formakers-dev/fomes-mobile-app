package com.formakers.fomes.helper;

import android.support.annotation.NonNull;

import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.EventStat;
import com.formakers.fomes.model.ShortTermStat;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private static final String TAG = "AppUsageDataHelper";
    public static final int DEFAULT_APP_USAGE_DURATION_DAYS = 7;

    private final AndroidNativeHelper androidNativeHelper;
    private final AppStatService appStatService;
    private final TimeHelper timeHelper;
    private final SharedPreferencesHelper SharedPreferencesHelper;

    @Inject
    public AppUsageDataHelper(AndroidNativeHelper androidNativeHelper,
                              AppStatService appStatService,
                              SharedPreferencesHelper SharedPreferencesHelper,
                              TimeHelper timeHelper) {
        this.androidNativeHelper = androidNativeHelper;
        this.appStatService = appStatService;
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

        Log.d(TAG, "shortTermStats Size=" + shortTermStatList.size() + " from=" + new Date(from));
        return appStatService.sendShortTermStats(shortTermStatList)
                .observeOn(Schedulers.io())
                .doOnCompleted(() -> SharedPreferencesHelper.setLastUpdateShortTermStatTimestamp(to));
    }

    // for send??
    public List<AppUsage> getAppUsage() {
        return getAppUsages(30);
    }

    public List<AppUsage> getAppUsages(int durationDays) {
        long endTimestamp = timeHelper.getCurrentTime();
        long startTimestamp = endTimestamp - (durationDays * 24 * 60 * 60 * 1000L);

        List<ShortTermStat> shortTermStats = getShortTermStats(startTimestamp, endTimestamp);
        List<AppUsage> appUsages = convertToAppUsage(shortTermStats);

        return appUsages;
    }

    // 현재 시간부터 지정한 기간까지의 앱 누적 사용량을 리턴
    public List<AppUsage> getAppUsagesFor(int days) {
        long endTimestamp = timeHelper.getCurrentTime();
        long startTimestamp = endTimestamp - (days * 24 * 60 * 60 * 1000L);

        List<ShortTermStat> shortTermStats = getShortTermStats(startTimestamp, endTimestamp);
        List<AppUsage> appUsages = convertToAppUsage(shortTermStats);

        // TODO : 이거 왜 했더라..?? 없어도 되지 않을까?
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
        Map<String, AppUsage> map = new HashMap<>();

        String key;
        AppUsage value;

        for (ShortTermStat shortTermStat : shortTermStatList) {
            // 날짜 + 패키지네임
            key = DateUtil.getDateStringFromTimestamp(shortTermStat.getStartTimeStamp())
                    + shortTermStat.getPackageName();

            if (map.containsKey(key)) {
                value = map.get(key);
                value.setTotalUsedTime(value.getTotalUsedTime() + shortTermStat.getTotalUsedTime());
            } else {
                value = new AppUsage(shortTermStat.getPackageName(), shortTermStat.getTotalUsedTime())
                        .setDate(DateUtil.getDateWithoutTime(new Date(shortTermStat.getStartTimeStamp())));
            }
            map.put(key, value);
        }

        return Lists.newArrayList(map.values());
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
}