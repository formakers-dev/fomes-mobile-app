package com.appbee.appbeemobile.helper;

import com.appbee.appbeemobile.model.AppUsage;
import com.appbee.appbeemobile.model.DailyStatSummary;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
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
    private AppService mockAppService;
    private AppRepositoryHelper mockAppRepositoryHelper;
    private TimeHelper mockTimeHelper;
    private LocalStorageHelper mockLocalStorageHelper;

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
        this.mockAppService = mock(AppService.class);
        this.mockAppRepositoryHelper = mock(AppRepositoryHelper.class);
        this.mockTimeHelper = mock(TimeHelper.class);
        this.mockLocalStorageHelper = mock(LocalStorageHelper.class);
        subject = new AppUsageDataHelper(mockAppBeeAndroidNativeHelper, mockAppStatService, mockAppService, mockAppRepositoryHelper, mockLocalStorageHelper, mockTimeHelper);
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
    public void sendShortTermStatAndAppUsages호출시_앱통계정보가_DB에_삭제_저장하고_통계저장API를_호출한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        when(mockLocalStorageHelper.getLastUpdateStatTimestamp()).thenReturn(0L);
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(10L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        AppUsageDataHelper.SendDataCallback mockSendDataCallback = mock(AppUsageDataHelper.SendDataCallback.class);
        subject.sendShortTermStatAndAppUsages(mockSendDataCallback);

        verify(mockAppRepositoryHelper).deleteAppUsages(anyInt());
        verify(mockAppRepositoryHelper).updateTotalUsedTime(any(List.class));
        verify(mockAppStatService).sendShortTermStats(any(List.class));
        verify(mockAppService).sendAppUsages(any(List.class));
        verify(mockLocalStorageHelper).setLastUpdateStatTimestamp(eq(10L));
        verify(mockSendDataCallback).onSuccess();
    }

    @Test
    public void sendShortTermStatAndAppUsages호출시_앱단기정보데이터전송중_에러발생시_Callback의_onFail을_호출한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        when(mockLocalStorageHelper.getLastUpdateStatTimestamp()).thenReturn(0L);
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(0L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mockAppStatService.sendShortTermStats(any(List.class))).thenReturn(Observable.error(new Throwable()));

        AppUsageDataHelper.SendDataCallback mockSendDataCallback = mock(AppUsageDataHelper.SendDataCallback.class);
        subject.sendShortTermStatAndAppUsages(mockSendDataCallback);

        verify(mockSendDataCallback).onFail();
    }

    @Test
    public void sendShortTermStatAndAppUsages호출시_앱사용정보통계전송중_에러발생시_Callback의_onFail을_호출한다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        when(mockLocalStorageHelper.getLastUpdateStatTimestamp()).thenReturn(0L);
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(0L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mockAppService.sendAppUsages(any(List.class))).thenReturn(Observable.error(new Throwable()));

        AppUsageDataHelper.SendDataCallback mockSendDataCallback = mock(AppUsageDataHelper.SendDataCallback.class);
        subject.sendShortTermStatAndAppUsages(mockSendDataCallback);

        verify(mockSendDataCallback).onFail();
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
    public void getSortedUsedPackageNames호출시_totalUsedTime으로_내림차순으로_정렬하여_리턴한다() throws Exception {
        List<AppUsage> mockAppUsageList = new ArrayList<>();
        mockAppUsageList.add(new AppUsage("package1", 1000L));
        mockAppUsageList.add(new AppUsage("package2", 2000L));
        mockAppUsageList.add(new AppUsage("package3", 3000L));
        mockAppUsageList.add(new AppUsage("package4", 4000L));
        when(mockAppRepositoryHelper.getAppUsages()).thenReturn(mockAppUsageList);
        List<String> result = subject.getSortedUsedPackageNames().toBlocking().single();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualTo("package4");
        assertThat(result.get(1)).isEqualTo("package3");
        assertThat(result.get(2)).isEqualTo("package2");
        assertThat(result.get(3)).isEqualTo("package1");
    }

    @Test
    public void getSortedUsedPackageNames호출시_totalUsedTime이_같을경우_packgeName으로_오름차순하여_리턴한다() throws Exception {
        List<AppUsage> mockAppUsageList = new ArrayList<>();
        mockAppUsageList.add(new AppUsage("package3", 1000L));
        mockAppUsageList.add(new AppUsage("package2", 1000L));
        mockAppUsageList.add(new AppUsage("package1", 1000L));
        mockAppUsageList.add(new AppUsage("package4", 1000L));
        when(mockAppRepositoryHelper.getAppUsages()).thenReturn(mockAppUsageList);
        List<String> result = subject.getSortedUsedPackageNames().toBlocking().single();

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualTo("package1");
        assertThat(result.get(1)).isEqualTo("package2");
        assertThat(result.get(2)).isEqualTo("package3");
        assertThat(result.get(3)).isEqualTo("package4");
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