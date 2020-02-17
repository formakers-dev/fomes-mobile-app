package com.formakers.fomes.common.helper;

import androidx.annotation.NonNull;

import com.formakers.fomes.common.model.AppUsage;
import com.formakers.fomes.common.model.EventStat;
import com.formakers.fomes.common.model.ShortTermStat;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.util.Log;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;

@Singleton
public class AppUsageDataHelper {
    private static final String TAG = "AppUsageDataHelper";
    public static final int DEFAULT_APP_USAGE_DURATION_DAYS = 30;

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

    /*** start of ShortTermStats ***/
    public Observable<ShortTermStat> getShortTermStats() {
        final long from = SharedPreferencesHelper.getLastUpdateShortTermStatTimestamp();
        final long to = timeHelper.getStatBasedCurrentTime();

        Log.d(TAG, "shortTermStats from=" + new Date(from));
        return getShortTermStats(from, to).subscribeOn(Schedulers.io());
    }

    @NonNull
    Observable<ShortTermStat> getShortTermStats(long startTime, long endTime) {

        return Observable.create(emitter -> androidNativeHelper.getUsageStatEvents(startTime, endTime).onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .toList()
                .subscribe(stats -> {
                    EventStat beforeForegroundEvent = null;

                    for (EventStat eventStat : stats) {
                        switch (eventStat.getEventType()) {
                            case MOVE_TO_FOREGROUND:
                                beforeForegroundEvent = eventStat;
                                break;

                            case MOVE_TO_BACKGROUND:
                                if (beforeForegroundEvent != null && eventStat.getPackageName().equals(beforeForegroundEvent.getPackageName())) {
                                    String packageName = eventStat.getPackageName();
                                    String versionName = this.androidNativeHelper.getVersionName(packageName);

                                    ShortTermStat shortTermStat = new ShortTermStat(packageName, beforeForegroundEvent.getEventTime(), (eventStat.getEventTime()))
                                            .setVersionName(versionName);

                                    emitter.onNext(shortTermStat);
                                    beforeForegroundEvent = null;
                                }
                                break;
                        }
                    }

                    emitter.onCompleted();
                },  e -> {
                    Log.e(TAG, String.valueOf(e));
                    emitter.onError(e);
                }));

        // 아래처럼 리팩토링 가능할듯... 좀 더 다듬긴해야함. groupBy 관련 에러 (Only one Subscriber allowed!)
//        return eventStats.groupBy(eventStat -> eventStat.getPackageName())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.io())
//                    .flatMap(stringEventStatGroupedObservable -> {
//                        Observable<EventStat> foregroundEventStats = stringEventStatGroupedObservable.filter(eventStat -> eventStat.getEventType() == MOVE_TO_FOREGROUND);
//                        Observable<EventStat> backgroundEventStats = stringEventStatGroupedObservable.filter(eventStat -> eventStat.getEventType() == MOVE_TO_BACKGROUND);
//
//                        return foregroundEventStats.zipWith(backgroundEventStats, (Func2<EventStat, EventStat, Pair>) Pair::new)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(Schedulers.io())
//                                .map(eventStatPair -> {
//                                    EventStat foregroundEventStat = (EventStat) eventStatPair.first;
//                                    EventStat backgroundEventStat = (EventStat) eventStatPair.second;
//
//                                    String packageName = foregroundEventStat.getPackageName();
//                                    String versionName = this.androidNativeHelper.getVersionName(packageName);
//
//                                    return new ShortTermStat(packageName,
//                                            foregroundEventStat.getEventTime(), backgroundEventStat.getEventTime())
//                                            .setVersionName(versionName);
//                                });
//                    })
    }

    /*** end of ShortTermStats ***/

    /*** start of AppUsages ***/
    // for send??
    public Observable<AppUsage> getAppUsages() {
        return getAppUsages(DEFAULT_APP_USAGE_DURATION_DAYS).subscribeOn(Schedulers.io());
    }

    Observable<AppUsage> getAppUsages(int durationDays) {
        Log.d(TAG, "getAppUsages(" + durationDays + ")");
        long endTimestamp = timeHelper.getCurrentTime();
        long startTimestamp = endTimestamp - (durationDays * 24 * 60 * 60 * 1000L);

        return getShortTermStats(startTimestamp, endTimestamp)
                .observeOn(Schedulers.io())
                .toList()
                .flatMap(shortTermStats -> Observable.from(AppUsage.createListFromShortTermStats(shortTermStats)));
    }

    /*** end of AppUsages ***/

    /*** etc ***/

    public Observable<Long> getUsageTime(@NonNull String packageName, long from) {
        return getShortTermStats(from, timeHelper.getCurrentTime())
                .observeOn(Schedulers.io())
                .filter(shortTermStat -> packageName.equals(shortTermStat.getPackageName()))
                .reduce(0L, (totalUsedTime, shortTermStat) -> {
                    totalUsedTime += shortTermStat.getTotalUsedTime();
                    return totalUsedTime;
                })
                .doOnNext(totalUsedTime -> Log.i(TAG, "getUsageTime) packageName=" + packageName + " totalUsedTime=" + totalUsedTime));
    }
}