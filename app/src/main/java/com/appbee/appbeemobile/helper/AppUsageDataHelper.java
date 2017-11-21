package com.appbee.appbeemobile.helper;

import android.support.annotation.NonNull;

import com.appbee.appbeemobile.model.DailyStatSummary;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.StatKey;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.util.DateUtil;
import com.appbee.appbeemobile.util.FormatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

@Singleton
public class AppUsageDataHelper {
    private final AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    private final AppStatService appStatService;
    private final AppService appService;
    private final AppRepositoryHelper appRepositoryHelper;
    private final TimeHelper timeHelper;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public AppUsageDataHelper(AppBeeAndroidNativeHelper appBeeAndroidNativeHelper,
                              AppStatService appStatService,
                              AppService appService,
                              AppRepositoryHelper appRepositoryHelper,
                              LocalStorageHelper localStorageHelper,
                              TimeHelper timeHelper) {
        this.appBeeAndroidNativeHelper = appBeeAndroidNativeHelper;
        this.appStatService = appStatService;
        this.appService = appService;
        this.appRepositoryHelper = appRepositoryHelper;
        this.localStorageHelper = localStorageHelper;
        this.timeHelper = timeHelper;
    }

    @NonNull
    List<ShortTermStat> getShortTermStats(long startTime, long endTime) {
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
                        shortTermStats.add(createShortTermStat(eventStat.getPackageName(), beforeForegroundEvent.getEventTime(), eventStat.getEventTime()));
                        beforeForegroundEvent = null;
                    }
                    break;
            }
        }

        return shortTermStats;
    }

    private ShortTermStat createShortTermStat(String packageName, long startTimeStamp, long endTimeStamp) {
        return new ShortTermStat(packageName, startTimeStamp, endTimeStamp, endTimeStamp - startTimeStamp);
    }

    public void sendShortTermStatAndAppUsages(SendDataCallback callback) {
        final int currentDate = Integer.parseInt(FormatUtil.getDateFromTimestamp(timeHelper.getCurrentTime()));
        appRepositoryHelper.deleteAppUsages(DateUtil.calBeforeDate(currentDate, 30));

        final long lastUpdateStatTimestamp = localStorageHelper.getLastUpdateStatTimestamp();
        final long statBasedEndTime = timeHelper.getStatBasedCurrentTime();
        final List<ShortTermStat> shortTermStatList = getShortTermStats(lastUpdateStatTimestamp, statBasedEndTime);

        appRepositoryHelper.updateTotalUsedTime(getDailyStatSummary(shortTermStatList));

        Observable.merge(
                appStatService.sendShortTermStats(shortTermStatList),
                appService.sendAppUsages(appRepositoryHelper.getAppUsages())
        ).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .all(result -> true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    localStorageHelper.setLastUpdateStatTimestamp(statBasedEndTime);
                    callback.onSuccess();
                }, error -> {
                    appStatService.logError(error);
                    callback.onFail();
                });
    }

    List<DailyStatSummary> getDailyStatSummary(List<ShortTermStat> shortTermStatList) {
        List<DailyStatSummary> dailyStatSummaryList = new ArrayList<>();

        Map<StatKey, Long> map = new HashMap<>();

        for (ShortTermStat shortTermStat : shortTermStatList) {
            StatKey key;
            if (DateUtil.calDateDiff(shortTermStat.getStartTimeStamp(), shortTermStat.getEndTimeStamp()) == 0) {
                key = new StatKey(shortTermStat.getPackageName(), FormatUtil.getDateFromTimestamp(shortTermStat.getStartTimeStamp()));
                mergeTotalUsedTimeByStatKey(map, key, shortTermStat.getTotalUsedTime());
            } else if (DateUtil.calDateDiff(shortTermStat.getStartTimeStamp(), shortTermStat.getEndTimeStamp()) == 1) {
                String startDate = FormatUtil.getDateFromTimestamp(shortTermStat.getStartTimeStamp());
                String endDate = FormatUtil.getDateFromTimestamp(shortTermStat.getEndTimeStamp());

                key = new StatKey(shortTermStat.getPackageName(), startDate);
                long firstTotalUsedTime = FormatUtil.getTimestampFromDate(endDate) - shortTermStat.getStartTimeStamp();
                mergeTotalUsedTimeByStatKey(map, key, firstTotalUsedTime);

                key = new StatKey(shortTermStat.getPackageName(), endDate);
                long secondTotalUsedTime = shortTermStat.getTotalUsedTime() - firstTotalUsedTime;
                mergeTotalUsedTimeByStatKey(map, key, secondTotalUsedTime);
            }
        }

        for (StatKey statKey : map.keySet()) {
            long value = map.get(statKey);
            dailyStatSummaryList.add(new DailyStatSummary(statKey.getPackageName(), Integer.parseInt(statKey.getDate()), value));
        }

        return dailyStatSummaryList;
    }

    private void mergeTotalUsedTimeByStatKey(Map<StatKey, Long> map, StatKey statKey, long totalUsedTime) {
        if (map.get(statKey) != null) {
            map.put(statKey, map.get(statKey) + totalUsedTime);
        } else {
            map.put(statKey, totalUsedTime);
        }
    }

    public interface SendDataCallback {
        void onSuccess();

        void onFail();
    }
}