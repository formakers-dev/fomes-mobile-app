package com.formakers.fomes.helper;

import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.DailyStatSummary;
import com.formakers.fomes.model.EventStat;
import com.formakers.fomes.model.ShortTermStat;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.repository.helper.AppRepositoryHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;
import java.util.Collections;
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
import static org.mockito.ArgumentMatchers.anyInt;
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

    private AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;
    private AppStatService mockAppStatService;
    private AppRepositoryHelper mockAppRepositoryHelper;
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

        this.mockAppBeeAndroidNativeHelper = mock(AppBeeAndroidNativeHelper.class);
        this.mockAppStatService = mock(AppStatService.class);
        this.mockAppRepositoryHelper = mock(AppRepositoryHelper.class);
        this.mockTimeHelper = mock(TimeHelper.class);
        this.mockSharedPreferencesHelper = mock(SharedPreferencesHelper.class);
        subject = new AppUsageDataHelper(mockAppBeeAndroidNativeHelper, mockAppStatService, mockAppRepositoryHelper, mockSharedPreferencesHelper, mockTimeHelper);
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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L, 9999L);

        assertThat(shortTermStats.size()).isEqualTo(0);
    }

    @Test
    public void getShortTermStats호출시_파라미터로_넘어온_조회시작시간을_기준으로_통계데이터를_조회한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        subject.getShortTermStats(200L, 300L);

        verify(mockAppBeeAndroidNativeHelper).getUsageStatEvents(startTimeCaptor.capture(), endTimeCaptor.capture());
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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mockAppStatService.sendShortTermStats(any(List.class))).thenReturn(Completable.error(new Throwable()));

        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        subject.sendShortTermStats().subscribe(testSubscriber);

        testSubscriber.assertError(Throwable.class);
    }

    @Test
    public void sendAppUsages호출시_앱통계정보가_DB에_삭제_저장하고_통계저장API를_호출한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        when(mockSharedPreferencesHelper.getLastUpdateAppUsageTimestamp()).thenReturn(0L);
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(10L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mockAppStatService.sendAppUsages(any(List.class))).thenReturn(Completable.complete());

        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        subject.sendAppUsages().subscribe(testSubscriber);

        verify(mockAppRepositoryHelper).deleteAppUsages(anyInt());
        verify(mockAppRepositoryHelper).updateAppUsages(any(List.class));
        verify(mockAppStatService).sendAppUsages(any(List.class));
        verify(mockSharedPreferencesHelper).setLastUpdateAppUsageTimestamp(eq(10L));
        testSubscriber.assertCompleted();
    }

    @Test
    public void sendAppUsages호출시_앱사용정보통계전송중_에러발생시_Callback의_onFail을_호출한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        when(mockSharedPreferencesHelper.getLastUpdateAppUsageTimestamp()).thenReturn(0L);
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(0L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mockAppStatService.sendAppUsages(any(List.class))).thenReturn(Completable.error(new Throwable()));

        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        subject.sendAppUsages().subscribe(testSubscriber);

        testSubscriber.assertError(Throwable.class);
    }

    @Test
    @Ignore
    public void getDailyStatSummary호출시_전달된_단기통계정보를_날짜별_앱별로_사용시간을_합산하여_리턴한다() throws Exception {
        List<ShortTermStat> mockShortTermStatList = new ArrayList<>();
        mockShortTermStatList.add(new ShortTermStat("package1", 1510974000000L, 1510974001000L, 1000L));     //2017-11-18 03:00:00
        mockShortTermStatList.add(new ShortTermStat("package1", 1511006400000L, 1511006402000L, 2000L));     //2017-11-18 12:00:00
        mockShortTermStatList.add(new ShortTermStat("package1", 1511049600000L, 1511049601500L, 1500L));     //2017-11-19 00:00:00
        mockShortTermStatList.add(new ShortTermStat("package2", 1511006400000L, 1511006403000L, 4000L));     //2017-11-18 12:00:00

        List<DailyStatSummary> dailyStatSummaryList = subject.getDailyStatSummary(mockShortTermStatList);

        sortDailyStatSummaryList(dailyStatSummaryList);

        assertDailyStatSummary(dailyStatSummaryList.get(0), "package1", 20171118, 3000L);
        assertDailyStatSummary(dailyStatSummaryList.get(1), "package1", 20171119, 1500L);
        assertDailyStatSummary(dailyStatSummaryList.get(2), "package2", 20171118, 4000L);
    }

    @Test
    @Ignore
    public void getDailyStatSummary호출시_시작일자와_종료일자가_하루차이가날경우_종료일자의_0시_기준으로_사용시간을_나누어_저장한다() throws Exception {
        List<ShortTermStat> mockShortTermStatList = new ArrayList<>();
        mockShortTermStatList.add(new ShortTermStat("package", 1511182800000L, 1511193600000L, 10800000L));     //2017-11-20 22:00:00 ~ 2017-11-21 01:00:00

        List<DailyStatSummary> dailyStatSummaryList = subject.getDailyStatSummary(mockShortTermStatList);

        sortDailyStatSummaryList(dailyStatSummaryList);

        assertDailyStatSummary(dailyStatSummaryList.get(0), "package", 20171120, 7200000L);
        assertDailyStatSummary(dailyStatSummaryList.get(1), "package", 20171121, 3600000L);

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

        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong()))
                .thenReturn(mockEventStatList);

        List<AppUsage> result = subject.getAppUsagesFor(7);

        // 7일 동안의 데이터를 가져왔니?
        verify(mockAppBeeAndroidNativeHelper).getUsageStatEvents(startTimeCaptor.capture(), endTimeCaptor.capture());
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
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> weeklyStatSummaryList = subject.getWeeklyStatSummaryList();

        assertThat(weeklyStatSummaryList.size()).isEqualTo(3);
        assertThat(weeklyStatSummaryList.get(0).getPackageName()).isEqualTo("packageB");
        assertThat(weeklyStatSummaryList.get(0).getTotalUsedTime()).isEqualTo(300L);
        assertThat(weeklyStatSummaryList.get(1).getPackageName()).isEqualTo("packageA");
        assertThat(weeklyStatSummaryList.get(1).getTotalUsedTime()).isEqualTo(250L);
        assertThat(weeklyStatSummaryList.get(2).getPackageName()).isEqualTo("packageC");
        assertThat(weeklyStatSummaryList.get(2).getTotalUsedTime()).isEqualTo(175L);

    }

    private void assertDailyStatSummary(DailyStatSummary dailyStatSummary, String packageName, int yyyymmdd, long totalUsedTime) {
        assertThat(dailyStatSummary.getPackageName()).isEqualTo(packageName);
        assertThat(dailyStatSummary.getYyyymmdd()).isEqualTo(yyyymmdd);
        assertThat(dailyStatSummary.getTotalUsedTime()).isEqualTo(totalUsedTime);
    }

    private void sortDailyStatSummaryList(List<DailyStatSummary> dailyStatSummaryList) {
        Collections.sort(dailyStatSummaryList, (o1, o2) -> {
            int i = o1.getPackageName().compareTo(o2.getPackageName());

            if (i == 0) {
                return o1.getYyyymmdd() - o2.getYyyymmdd();
            } else {
                return i;
            }
        });
    }
}