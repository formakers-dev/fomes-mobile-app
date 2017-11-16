package com.appbee.appbeemobile.helper;

import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Before
    public void setUp() throws Exception {
        this.mockAppBeeAndroidNativeHelper = mock(AppBeeAndroidNativeHelper.class);
        this.mockAppStatService = mock(AppStatService.class);
        this.mockAppService = mock(AppService.class);
        this.mockAppRepositoryHelper = mock(AppRepositoryHelper.class);
        this.mockTimeHelper = mock(TimeHelper.class);

        subject = new AppUsageDataHelper(mockAppBeeAndroidNativeHelper, mockAppStatService, mockAppService, mockAppRepositoryHelper, mockTimeHelper);

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
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

    @Test
    public void getShortTermStatsTimeSummary호출시_package별_totalUsedTime의_합을_리턴한다() throws Exception {
        List<ShortTermStat> mockShortTermStatList = new ArrayList<>();
        mockShortTermStatList.add(new ShortTermStat("packageName1", 1000L, 2000L, 1000L));
        mockShortTermStatList.add(new ShortTermStat("packageName2", 2000L, 4000L, 2000L));
        mockShortTermStatList.add(new ShortTermStat("packageName1", 4000L, 9000L, 5000L));

        Map<String, Long> map = subject.getShortTermStatsTimeSummary(mockShortTermStatList);

        assertThat(map.get("packageName1")).isEqualTo(6000L);
        assertThat(map.get("packageName2")).isEqualTo(2000L);
    }

    private void assertConfirmDetailUsageStat(ShortTermStat shortTermStat, String packageName, long startTimeStamp, long endTimeStamp) {
        assertThat(shortTermStat.getPackageName()).isEqualTo(packageName);
        assertThat(shortTermStat.getStartTimeStamp()).isEqualTo(startTimeStamp);
        assertThat(shortTermStat.getEndTimeStamp()).isEqualTo(endTimeStamp);
        assertThat(shortTermStat.getTotalUsedTime()).isEqualTo(endTimeStamp - startTimeStamp);
    }

    @Test
    public void sendShortTermStatAndAppUsages호출시_앱통계정보가_db_에_저장된다() throws Exception {
        when(mockAppStatService.getLastUpdateStatTimestamp()).thenReturn(Observable.just(0L));
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(10L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        AppUsageDataHelper.SendDataCallback mockSendDataCallback = mock(AppUsageDataHelper.SendDataCallback.class);
        subject.sendShortTermStatAndAppUsages(mockSendDataCallback);

        verify(mockAppRepositoryHelper).updateTotalUsedTime(any(Map.class));
        verify(mockAppStatService).sendShortTermStats(any(List.class), anyLong());
        verify(mockAppService).sendAppUsages(any(List.class));
        verify(mockSendDataCallback).onSuccess();
    }

    @Test
    public void sendShortTermStatAndAppUsages호출시_앱단기정보데이터전송중_에러발생시_Callback의_onFail을_호출한다() throws Exception {
        when(mockAppStatService.getLastUpdateStatTimestamp()).thenReturn(Observable.just(0L));
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(0L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mock(List.class));
        when(mockAppStatService.sendShortTermStats(any(List.class), anyLong())).thenReturn(Observable.error(new Throwable()));

        AppUsageDataHelper.SendDataCallback mockSendDataCallback = mock(AppUsageDataHelper.SendDataCallback.class);
        subject.sendShortTermStatAndAppUsages(mockSendDataCallback);

        verify(mockSendDataCallback).onFail();
    }

    @Test
    public void sendShortTermStatAndAppUsages호출시_앱사용정보통계전송중_에러발생시_Callback의_onFail을_호출한다() throws Exception {
        when(mockAppStatService.getLastUpdateStatTimestamp()).thenReturn(Observable.just(0L));
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(0L);
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mock(List.class));
        when(mockAppService.sendAppUsages(any(List.class))).thenReturn(Observable.error(new Throwable()));

        AppUsageDataHelper.SendDataCallback mockSendDataCallback = mock(AppUsageDataHelper.SendDataCallback.class);
        subject.sendShortTermStatAndAppUsages(mockSendDataCallback);

        verify(mockSendDataCallback).onFail();
    }

    @Test
    public void sendShortTermStatAndAppUsages호출시_최종전송시간API조회에러발생시_Callback의_onFail을_호출한다() throws Exception {
        when(mockAppStatService.getLastUpdateStatTimestamp()).thenReturn(Observable.error(new Throwable()));

        AppUsageDataHelper.SendDataCallback mockSendDataCallback = mock(AppUsageDataHelper.SendDataCallback.class);
        subject.sendShortTermStatAndAppUsages(mockSendDataCallback);

        verify(mockSendDataCallback).onFail();
    }
}