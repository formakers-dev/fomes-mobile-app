package com.formakers.fomes.helper;

import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.EventStat;
import com.formakers.fomes.model.ShortTermStat;

import org.assertj.core.util.DateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppUsageDataHelperTest {
    private AppUsageDataHelper subject;

    @Captor
    ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);

    @Captor
    ArgumentCaptor<Long> endTimeCaptor = ArgumentCaptor.forClass(Long.class);

    private AndroidNativeHelper mockAndroidNativeHelper;
    private AppStatService mockAppStatService;
    private TimeHelper mockTimeHelper;
    private SharedPreferencesHelper mockSharedPreferencesHelper;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        this.mockAndroidNativeHelper = mock(AndroidNativeHelper.class);
        this.mockAppStatService = mock(AppStatService.class);
        this.mockTimeHelper = mock(TimeHelper.class);
        this.mockSharedPreferencesHelper = mock(SharedPreferencesHelper.class);
        subject = new AppUsageDataHelper(mockAndroidNativeHelper, mockAppStatService, mockSharedPreferencesHelper, mockTimeHelper);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void getShortTermStats호출시_앱사용정보를_시간대별로_조회하여_리턴한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1100L));
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L, 9999L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageA", 1000L, 1100L);
    }


    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행된경우_A앱의_실행시간은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1250L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1300L));
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L, 9999L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageB", 1100L, 1250L);
    }

    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_기록이_종료된_경우_A앱의_사용기록은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1300L));
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L, 9999L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertThat(shortTermStats.get(0).getPackageName()).isEqualTo("packageB");
    }

    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행되고_B앱이_떠있는상태에서_C앱이_실행될경우_AB앱의_사용시간은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_FOREGROUND, 1200L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1250L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_BACKGROUND, 1375L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1400L));
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L, 9999L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageC", 1200L, 1375L);
    }

    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_A앱이_다시실행되는경우_기존시작시간은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1300L));
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L, 9999L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageA", 1100L, 1300L);
    }

    @Test
    public void getShortTermStats호출시_여러앱이_FOREGROUND기록만있고_종료기록이없는경우_전체데이터를_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_FOREGROUND, 1300L));
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L, 9999L);

        assertThat(shortTermStats.size()).isEqualTo(0);
    }

    @Test
    public void getShortTermStats호출시_파라미터로_넘어온_조회시작시간을_기준으로_통계데이터를_조회한다() throws Exception {
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        subject.getShortTermStats(200L, 300L);

        verify(mockAndroidNativeHelper).getUsageStatEvents(startTimeCaptor.capture(), endTimeCaptor.capture());
        assertThat(startTimeCaptor.getValue()).isEqualTo(200L);
        assertThat(endTimeCaptor.getValue()).isEqualTo(300L);
    }

    private void assertConfirmDetailUsageStat(ShortTermStat shortTermStat, String packageName, long startTimeStamp, long endTimeStamp) {
        assertThat(shortTermStat.getPackageName()).isEqualTo(packageName);
        assertThat(shortTermStat.getStartTimeStamp()).isEqualTo(startTimeStamp);
        assertThat(shortTermStat.getEndTimeStamp()).isEqualTo(endTimeStamp);
        assertThat(shortTermStat.getTotalUsedTime()).isEqualTo(endTimeStamp - startTimeStamp);
    }

    @Test
    public void sendShortTermStat호출시_단기통계저장API를_호출한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        when(mockSharedPreferencesHelper.getLastUpdateShortTermStatTimestamp()).thenReturn(0L);
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(10L);
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mockAppStatService.sendShortTermStats(any(List.class))).thenReturn(Completable.complete());

        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        subject.sendShortTermStats().subscribe(testSubscriber);

        verify(mockAppStatService).sendShortTermStats(any(List.class));
        verify(mockSharedPreferencesHelper).setLastUpdateShortTermStatTimestamp(eq(10L));
        testSubscriber.assertCompleted();
    }

    @Test
    public void sendShortTermStat처리중_단기통계저장API_에러발생시_Callback의_onFail을_호출한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        when(mockSharedPreferencesHelper.getLastUpdateShortTermStatTimestamp()).thenReturn(0L);
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(0L);
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mockAppStatService.sendShortTermStats(any(List.class))).thenReturn(Completable.error(new Throwable()));

        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        subject.sendShortTermStats().subscribe(testSubscriber);

        testSubscriber.assertError(Throwable.class);
    }

    @Test
    public void getAppUsage_호출시__날짜별_앱_누적사용시간을_리턴한다() {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1554768000000L);   //2019-04-09

        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1554768000000L));   // 2019-04-09
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1554768001000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1554336000000L));   // 2019-04-04
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1554336002000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1554768005000L));   // 2019-04-09
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1554768006000L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_FOREGROUND, 1554508800000L));   // 2019-04-06
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_BACKGROUND, 1554508803000L));
        mockEventStatList.add(new EventStat("packageD", MOVE_TO_FOREGROUND, 1554163200000L));   // 2019-04-02
        mockEventStatList.add(new EventStat("packageD", MOVE_TO_BACKGROUND, 1554163204000L));
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<AppUsage> actualList = subject.getAppUsage();

        sortAppUsage(actualList);
        System.out.println(actualList);

        assertAppUsage(actualList.get(0), DateUtil.parse("2019-04-02"), "packageD", 4000L);
        assertAppUsage(actualList.get(1), DateUtil.parse("2019-04-04"), "packageA", 2000L);
        assertAppUsage(actualList.get(2), DateUtil.parse("2019-04-06"), "packageC", 3000L);
        assertAppUsage(actualList.get(3), DateUtil.parse("2019-04-09"), "packageA", 2000L);


        System.out.println(actualList);

    }

    @Test
    public void getAppUsagesFor_호출시__현재시간부터_지정한기간까지의_앱_누적_사용량을_리턴한다() {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1512950400000L);
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1250L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1400L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_FOREGROUND, 1200L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_BACKGROUND, 1375L));

        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong()))
                .thenReturn(mockEventStatList);

        List<AppUsage> result = subject.getAppUsagesFor(7);

        // 7일 동안의 데이터를 가져왔니?
        verify(mockAndroidNativeHelper).getUsageStatEvents(startTimeCaptor.capture(), endTimeCaptor.capture());
        assertThat(endTimeCaptor.getValue() - startTimeCaptor.getValue()).isEqualTo(7 * 24 * 60 * 60 * 1000L);

        // 앱 누적 사용량 제대로 리턴했니?
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getPackageName()).isEqualTo("packageB");
        assertThat(result.get(0).getTotalUsedTime()).isEqualTo(300L);
        assertThat(result.get(1).getPackageName()).isEqualTo("packageA");
        assertThat(result.get(1).getTotalUsedTime()).isEqualTo(250L);
        assertThat(result.get(2).getPackageName()).isEqualTo("packageC");
        assertThat(result.get(2).getTotalUsedTime()).isEqualTo(175L);
    }

    @Ignore
    @Test
    public void getWeeklyStatSummaryList호출시_app별_사용시간_합계를_구한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1512950400000L); // 2017-12-11
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1250L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1400L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_FOREGROUND, 1200L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_BACKGROUND, 1375L));
        // B 300
        // A 250
        // C 175
        when(mockAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> weeklyStatSummaryList = subject.getWeeklyStatSummaryList();

        assertThat(weeklyStatSummaryList.size()).isEqualTo(3);
        assertThat(weeklyStatSummaryList.get(0).getPackageName()).isEqualTo("packageB");
        assertThat(weeklyStatSummaryList.get(0).getTotalUsedTime()).isEqualTo(300L);
        assertThat(weeklyStatSummaryList.get(1).getPackageName()).isEqualTo("packageA");
        assertThat(weeklyStatSummaryList.get(1).getTotalUsedTime()).isEqualTo(250L);
        assertThat(weeklyStatSummaryList.get(2).getPackageName()).isEqualTo("packageC");
        assertThat(weeklyStatSummaryList.get(2).getTotalUsedTime()).isEqualTo(175L);

    }

    private void assertAppUsage(AppUsage appUsage, Date date, String packageName, long totalUsedTime) {
        assertThat(appUsage.getDate()).isEqualTo(date);
        assertThat(appUsage.getPackageName()).isEqualTo(packageName);
        assertThat(appUsage.getTotalUsedTime()).isEqualTo(totalUsedTime);
    }

    private void sortAppUsage(List<AppUsage> appUsages) {
        Collections.sort(appUsages, (o1, o2) -> {
            boolean isSameDate = o1.getDate().equals(o2.getDate());

            if (isSameDate) {
                return o1.getPackageName().compareTo(o2.getPackageName());
            } else {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }
}