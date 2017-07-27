package com.appbee.appbeemobile.manager;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.DetailUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;
import com.appbee.appbeemobile.util.TimeUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StatManagerTest {
    private StatManager subject;

    @Captor
    ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);

    private SystemServiceBridge mockSystemServiceBridge;

    @Before
    public void setUp() throws Exception {
        mockSystemServiceBridge = mock(SystemServiceBridge.class);
        subject = new StatManager(RuntimeEnvironment.application.getApplicationContext(), mockSystemServiceBridge);
    }

    @Test
    public void getUserAppDailyUsageStatsForYear호출시_1년간_일별통계정보를_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockSystemServiceBridge.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        List<DailyUsageStat> actualResult = subject.getUserAppDailyUsageStatsForYear();

        assertEquals(actualResult.size(), 2);
    }

    @Test
    public void getUserAppDailyUsageStatsForYear호출시_동일앱의_일간사용정보가_두개이상인경우_사용시간을_합산하여_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 200L, 1499934615000L));    //2017-07-13 17:30:15
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 400L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockSystemServiceBridge.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        List<DailyUsageStat> actualResult = subject.getUserAppDailyUsageStatsForYear();

        assertEquals(actualResult.size(), 2);
        assertEquals(actualResult.get(0).getTotalUsedTime(), 300L);
        assertEquals(actualResult.get(1).getTotalUsedTime(), 400L);
    }

    @Test
    public void getDetailUsageStat호출시_앱사용정보를_시간대별로_조회하여_리턴한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_BACKGROUND, 1100L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(detailUsageStats.get(0), "packageA", 1000L, 1100L);
    }

    @Test
    public void getDetailUsageStat호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행된경우_A앱의_실행시간은_무시한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_BACKGROUND, 1250L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_BACKGROUND, 1300L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(detailUsageStats.get(0), "packageB", 1100L, 1250L);
    }

    @Test
    public void getDetailUsageStat호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_기록이_종료된_경우_A앱의_사용기록은_무시한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_BACKGROUND, 1100L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1300L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(1);
        assertThat(detailUsageStats.get(0).getPackageName()).isEqualTo("packageB");
    }

    @Test
    public void getDetailUsageStat호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행되고_B앱이_떠있는상태에서_C앱이_실행될경우_AB앱의_사용시간은_무시한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockUsageStatEventList.add(new UsageStatEvent("packageC", MOVE_TO_FOREGROUND, 1200L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_BACKGROUND, 1250L));
        mockUsageStatEventList.add(new UsageStatEvent("packageC", MOVE_TO_BACKGROUND, 1375L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_BACKGROUND, 1400L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(detailUsageStats.get(0), "packageC", 1200L, 1375L);
    }

    @Test
    public void getDetailUsageStat호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_A앱이_다시실행되는경우_기존시작시간은_무시한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1100L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_BACKGROUND, 1300L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(detailUsageStats.get(0), "packageA", 1100L, 1300L);
    }

    @Test
    public void getDetailUsageStat호출시_여러앱이_FOREGROUND기록만있고_종료기록이없는경우_전체데이터를_무시한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockUsageStatEventList.add(new UsageStatEvent("packageC", MOVE_TO_FOREGROUND, 1300L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(0);
    }

    @Test
    public void getAppList호출시_설치된_앱리스트조회를_요청한다() throws Exception {
        subject.getAppList();

        verify(mockSystemServiceBridge).getInstalledLaunchableApps();
    }

    @Test
    public void getDetailUsageStats호출시_SharedPreferences에_endTime을_기록한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(subject.context.getString(R.string.shared_prefereces), Context.MODE_PRIVATE);
        long endTime1 = sharedPreferences.getLong(subject.context.getString(R.string.shared_prefereces_key_last_usage_time), 0L);

        subject.getDetailUsageStats();

        long endTime2 = sharedPreferences.getLong(subject.context.getString(R.string.shared_prefereces_key_last_usage_time), 0L);
        assertThat(endTime2).isGreaterThan(endTime1);
    }

    @Test
    public void getDetailUsageStats호출시_SharedPreference에_LAST_USAGE_TIME이_있을경우_startTime은_LAST_USAGE_TIME로_대체된다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(subject.context.getString(R.string.shared_prefereces), Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(subject.context.getString(R.string.shared_prefereces_key_last_usage_time), 200L).commit();

        subject.getDetailUsageStats();

        verify(mockSystemServiceBridge).getUsageStatEvents(startTimeCaptor.capture(), anyLong());
        assertThat(startTimeCaptor.getValue()).isEqualTo(200L);
    }

    @Test
    public void getDetailUsageStats호출시_SharedPreference에_LAST_USAGE_TIME이_없을경우_startTime은_ms단위는_절삭한_일주일전시간으로_세팅된다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        subject.getDetailUsageStats();

        verify(mockSystemServiceBridge).getUsageStatEvents(startTimeCaptor.capture(), anyLong());
        long padding = Math.abs(startTimeCaptor.getValue() - (TimeUtil.getCurrentTime()-1000*60*60*24*7));

        assertThat(padding).isLessThan(1000L);
    }

    @Test
    public void getDetailUsageEvents호출시_가공되지_않은_앱사용정보를_리턴한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_BACKGROUND, 1100L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<UsageStatEvent> usageStatEventList = subject.getDetailUsageEvents();

        assertThat(usageStatEventList.size()).isEqualTo(2);
        assertThat(usageStatEventList.get(0).getPackageName()).isEqualTo("packageA");
        assertThat(usageStatEventList.get(0).getEventType()).isEqualTo(MOVE_TO_FOREGROUND);
        assertThat(usageStatEventList.get(0).getTimeStamp()).isEqualTo(1000L);
    }

    private void assertConfirmDetailUsageStat(DetailUsageStat detailUsageStat, String packageName, long startTimeStamp, long endTimeStamp) {
        assertThat(detailUsageStat.getPackageName()).isEqualTo(packageName);
        assertThat(detailUsageStat.getStartTimeStamp()).isEqualTo(startTimeStamp);
        assertThat(detailUsageStat.getEndTimeStamp()).isEqualTo(endTimeStamp);
        assertThat(detailUsageStat.getTotalUsedTime()).isEqualTo(endTimeStamp - startTimeStamp);
    }

    @NonNull
    private UsageStats createMockUsageStats(String packageName, long totalTimeInForeground, long lastTimeUsed) {
        UsageStats mockUsageStats = mock(UsageStats.class);
        when(mockUsageStats.getPackageName()).thenReturn(packageName);
        when(mockUsageStats.getTotalTimeInForeground()).thenReturn(totalTimeInForeground);
        when(mockUsageStats.getLastTimeUsed()).thenReturn(lastTimeUsed);
        return mockUsageStats;
    }

}