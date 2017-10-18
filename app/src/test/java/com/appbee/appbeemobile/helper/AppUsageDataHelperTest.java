package com.appbee.appbeemobile.helper;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeLongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppUsageDataHelperTest {
    private AppUsageDataHelper subject;

    @Captor
    ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);

    private AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;
    private TimeHelper mockTimeHelper;

    private static final long MILLISECONDS_OF_THREE_MONTHS = 7884000000L; // 365 * 24 * 60 * 60 * 1000 / 4
    private static final long MILLISECONDS_OF_TWO_YEARS = 63072000000L;


    @Before
    public void setUp() throws Exception {
        this.mockAppBeeAndroidNativeHelper = mock(AppBeeAndroidNativeHelper.class);
        this.mockTimeHelper = mock(TimeHelper.class);
        subject = new AppUsageDataHelper(mockAppBeeAndroidNativeHelper, mockTimeHelper);
    }

    @Test
    public void getLongTermStats호출시_최근3개월간_앱사용정보를_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockAppBeeAndroidNativeHelper.getUsageStats(eq(UsageStatsManager.INTERVAL_MONTHLY), anyLong(), anyLong())).thenReturn(preStoredUsageStats);

        List<LongTermStat> actualResult = subject.getLongTermStats();

        assertEquals(actualResult.size(), 2);
    }

    @Test
    public void getLongTermStats호출시_동일앱의_일간사용정보가_두개이상인경우_사용시간을_합산하여_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 200L, 1499934615000L));    //2017-07-13 17:30:15
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 400L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockAppBeeAndroidNativeHelper.getUsageStats(eq(UsageStatsManager.INTERVAL_MONTHLY), anyLong(), anyLong())).thenReturn(preStoredUsageStats);

        List<LongTermStat> actualResult = subject.getLongTermStats();

        assertEquals(actualResult.size(), 2);
        assertEquals(actualResult.get(0).getTotalUsedTime(), 400L);
        assertEquals(actualResult.get(1).getTotalUsedTime(), 300L);
    }

    @Test
    public void getLongTermStats호출시_연간_일별통계정보_사용시간이_많은순서대로_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00

        when(mockAppBeeAndroidNativeHelper.getUsageStats(eq(UsageStatsManager.INTERVAL_MONTHLY), anyLong(), anyLong())).thenReturn(preStoredUsageStats);

        List<LongTermStat> actualResult = subject.getLongTermStats();

        assertEquals(actualResult.size(), 2);
        assertEquals(actualResult.get(0).getPackageName(), "bbbbb");
    }

    @Test
    public void getLongTermStats호출시_3개월간의_앱사용정보를_요청한다() throws Exception {
        subject.getLongTermStats();

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> endTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockAppBeeAndroidNativeHelper).getUsageStats(eq(UsageStatsManager.INTERVAL_MONTHLY), startTimeCaptor.capture(), endTimeCaptor.capture());

        assertEquals(startTimeCaptor.getValue().longValue(), endTimeCaptor.getValue() - MILLISECONDS_OF_THREE_MONTHS);
    }

    @Test
    public void getNativeLongTermStatsFor2Years호출시_2년간의_앱사용정보를_요청한다() throws Exception {
        subject.getNativeLongTermStatsFor2Years();

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> endTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockAppBeeAndroidNativeHelper).getUsageStats(eq(UsageStatsManager.INTERVAL_YEARLY), startTimeCaptor.capture(), endTimeCaptor.capture());

        assertEquals(startTimeCaptor.getValue().longValue(), endTimeCaptor.getValue() - MILLISECONDS_OF_TWO_YEARS);
    }

    @Test
    public void getNativeLongTermStatsFor2Years호출시_앱사용정보를_리턴한다() throws Exception {
        List<UsageStats> mockUsageStatList = new ArrayList<>();
        mockUsageStatList.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));
        mockUsageStatList.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));

        when(mockAppBeeAndroidNativeHelper.getUsageStats(eq(UsageStatsManager.INTERVAL_YEARLY), anyLong(), anyLong())).thenReturn(mockUsageStatList);

        List<NativeLongTermStat> nativeLongTermStatList = subject.getNativeLongTermStatsFor2Years();

        assertThat(nativeLongTermStatList).isNotNull();
        assertThat(nativeLongTermStatList.size()).isEqualTo(2);
        assertEqualLongTermStat(nativeLongTermStatList.get(0), "aaaaa", 0L, 0L, 1499914800000L, 100L);
        assertEqualLongTermStat(nativeLongTermStatList.get(1), "bbbbb", 0L, 0L, 1500001200000L, 200L);
    }

    private void assertEqualLongTermStat(NativeLongTermStat longTermStat, String packageName, long beginTimeStamp, long lastTimeStamp, long lastTimeUsed, long totalUsedTime) {
        assertThat(longTermStat.getPackageName()).isEqualTo(packageName);
        assertThat(longTermStat.getBeginTimeStamp()).isEqualTo(beginTimeStamp);
        assertThat(longTermStat.getEndTimeStamp()).isEqualTo(lastTimeStamp);
        assertThat(longTermStat.getLastTimeUsed()).isEqualTo(lastTimeUsed);
        assertThat(longTermStat.getTotalTimeInForeground()).isEqualTo(totalUsedTime);
    }

    @Test
    public void getLongTermStats호출시_조회시작일자는3개월전_조회종료일자는현재시간을_전달한다() throws Exception {
        long currentTime = 1499914800000L;
        long threeMonthsAgo = 1492030800000L; // currentTime - (365 * 24 * 60 * 60 * 1000 / 4)

        when(mockTimeHelper.getCurrentTime()).thenReturn(currentTime);

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> endTimeCaptor = ArgumentCaptor.forClass(Long.class);

        subject.getLongTermStats();

        verify(mockAppBeeAndroidNativeHelper).getUsageStats(eq(UsageStatsManager.INTERVAL_MONTHLY), startTimeCaptor.capture(), endTimeCaptor.capture());

        assertThat(endTimeCaptor.getValue()).isEqualTo(currentTime);
        assertThat(startTimeCaptor.getValue()).isEqualTo(threeMonthsAgo);
    }

    @Test
    public void getShortTermStats호출시_앱사용정보를_시간대별로_조회하여_리턴한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1100L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

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

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

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

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

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

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

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

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

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

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

        assertThat(shortTermStats.size()).isEqualTo(0);
    }

    @Test
    public void getAppList호출시_설치된_앱리스트조회를_요청한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.getInstalledLaunchableApps()).thenReturn(mock(Observable.class));

        subject.getAppList();

        verify(mockAppBeeAndroidNativeHelper).getInstalledLaunchableApps();
    }

    @Test
    public void getShortTermStats호출시_파라미터로_넘어온_조회시작시간을_기준으로_통계데이터를_조회한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        subject.getShortTermStats(200L);

        verify(mockAppBeeAndroidNativeHelper).getUsageStatEvents(startTimeCaptor.capture(), anyLong());
        assertThat(startTimeCaptor.getValue()).isEqualTo(200L);
    }

    @Test
    public void getShortTermStatsTimeSummary호출시_package별_totalUsedTime의_합을_리턴한다() throws Exception {
        List<EventStat> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockEventStats("aaaaa", 1, 1499914800000L));    //2017-07-12 12:00:00
        preStoredUsageStats.add(createMockEventStats("aaaaa", 2, 1500001200000L));    //2017-07-14 12:00:00
        preStoredUsageStats.add(createMockEventStats("bbbbb", 1, 1500001200000L));    //2017-07-14 12:00:00

        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(preStoredUsageStats);

        Map<String, Long> map = subject.getShortTermStatsTimeSummary();

        assertThat(map.get("aaaaa")).isEqualTo(86400000L);
        assertThat(map.get("bbbbb")).isNull();
    }

    @Test
    public void getLongTermStatsFor3Months호출시_3개월간의_데이터를_리턴한다() throws Exception {

        subject.getLongTermStatsFor3Months();

        ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> endTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockAppBeeAndroidNativeHelper).getUsageStats(eq(UsageStatsManager.INTERVAL_MONTHLY), startTimeCaptor.capture(), endTimeCaptor.capture());

        assertThat(startTimeCaptor.getValue().longValue()).isEqualTo(endTimeCaptor.getValue() - MILLISECONDS_OF_THREE_MONTHS);
    }

    @Test
    public void getLongTermStatsFor3Months호출시_장기사용데이터를_리턴한다() throws Exception {
        List<UsageStats> usageStatsList = new ArrayList<>();
        usageStatsList.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        usageStatsList.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00

        when(mockAppBeeAndroidNativeHelper.getUsageStats(eq(UsageStatsManager.INTERVAL_MONTHLY), anyLong(), anyLong())).thenReturn(usageStatsList);

        List<NativeLongTermStat> nativeLongTermStatList = subject.getLongTermStatsFor3Months();

        assertThat(nativeLongTermStatList.size()).isEqualTo(2);
        assertEqualLongTermStat(nativeLongTermStatList.get(0), "aaaaa", 0L, 0L, 1499914800000L, 100L);
        assertEqualLongTermStat(nativeLongTermStatList.get(1), "bbbbb", 0L, 0L, 1500001200000L, 200L);
    }

    private void assertConfirmDetailUsageStat(ShortTermStat shortTermStat, String packageName, long startTimeStamp, long endTimeStamp) {
        assertThat(shortTermStat.getPackageName()).isEqualTo(packageName);
        assertThat(shortTermStat.getStartTimeStamp()).isEqualTo(startTimeStamp);
        assertThat(shortTermStat.getEndTimeStamp()).isEqualTo(endTimeStamp);
        assertThat(shortTermStat.getTotalUsedTime()).isEqualTo(endTimeStamp - startTimeStamp);
    }

    @NonNull
    private UsageStats createMockUsageStats(String packageName, long totalTimeInForeground, long lastTimeUsed) {
        UsageStats mockUsageStats = mock(UsageStats.class);
        when(mockUsageStats.getPackageName()).thenReturn(packageName);
        when(mockUsageStats.getTotalTimeInForeground()).thenReturn(totalTimeInForeground);
        when(mockUsageStats.getLastTimeUsed()).thenReturn(lastTimeUsed);
        return mockUsageStats;
    }

    @NonNull
    private EventStat createMockEventStats(String packageName, int eventType, long eventTime) {
        EventStat mockEventStats = mock(EventStat.class);
        when(mockEventStats.getPackageName()).thenReturn(packageName);
        when(mockEventStats.getEventType()).thenReturn(eventType);
        when(mockEventStats.getEventTime()).thenReturn(eventTime);
        return mockEventStats;
    }
}