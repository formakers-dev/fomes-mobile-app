package com.appbee.appbeemobile.manager;

import android.app.usage.UsageStats;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.DetailUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    @Inject
    SystemServiceBridge mockSystemServiceBridge;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);

        subject = new StatManager(application.getApplicationContext());
    }

    @Test
    public void getUserAppDailyUsageStatsForYear호출시_1년간_일별통계정보를_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockSystemServiceBridge.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        Map<String, DailyUsageStat> actualResult = subject.getUserAppDailyUsageStatsForYear();

        assertEquals(actualResult.size(), 2);
    }

    @Test
    public void getUserAppDailyUsageStatsForYear호출시_동일앱의_일간사용정보가_두개이상인경우_사용시간을_합산하여_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 200L, 1499934615000L));    //2017-07-13 17:30:15
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 400L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockSystemServiceBridge.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        Map<String, DailyUsageStat> actualResult = subject.getUserAppDailyUsageStatsForYear();

        assertEquals(actualResult.size(), 2);
        assertEquals(actualResult.get("aaaaa20170713").getTotalUsedTime(), 300L);
        assertEquals(actualResult.get("aaaaa20170714").getTotalUsedTime(), 400L);
    }

    @Test
    public void getDetailUsageStat호출시_앱사용정보를_시간대별로_조회하여_리턴한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("package_name", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("package_name", MOVE_TO_BACKGROUND, 1100L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(detailUsageStats.get(0), "package_name", 1000L, 1100L);
    }

    @Test
    public void getDetailUsageStat호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행된경우_A앱의_종료시간은_B앱의_시작시간으로_리턴한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_BACKGROUND, 1250L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_BACKGROUND, 1300L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(2);
        assertConfirmDetailUsageStat(detailUsageStats.get(0), "packageA", 1000L, 1100L);
        assertConfirmDetailUsageStat(detailUsageStats.get(1), "packageB", 1100L, 1250L);
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
    public void getDetailUsageStat호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행되고_B앱이_떠있는상태에서_C앱이_실행될경우_A앱의_종료시간은_B앱의_시작시간으로_B앱의종료시간은_C앱의_시작시간으로리턴한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockUsageStatEventList.add(new UsageStatEvent("packageC", MOVE_TO_FOREGROUND, 1200L));
        mockUsageStatEventList.add(new UsageStatEvent("packageA", MOVE_TO_BACKGROUND, 1250L));
        mockUsageStatEventList.add(new UsageStatEvent("packageC", MOVE_TO_BACKGROUND, 1375L));
        mockUsageStatEventList.add(new UsageStatEvent("packageB", MOVE_TO_BACKGROUND, 1400L));
        when(mockSystemServiceBridge.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockUsageStatEventList);

        List<DetailUsageStat> detailUsageStats = subject.getDetailUsageStats();

        assertThat(detailUsageStats.size()).isEqualTo(3);
        assertConfirmDetailUsageStat(detailUsageStats.get(0), "packageA", 1000L, 1100L);
        assertConfirmDetailUsageStat(detailUsageStats.get(1), "packageB", 1100L, 1200L);
        assertConfirmDetailUsageStat(detailUsageStats.get(2), "packageC", 1200L, 1375L);
    }

    private void assertConfirmDetailUsageStat(DetailUsageStat detailUsageStat, String packageName, long startTimeStamp, long endTimeStamp) {
        assertThat(detailUsageStat.getPackageName()).isEqualTo(packageName);
        assertThat(detailUsageStat.getStartTimeStamp()).isEqualTo(startTimeStamp);
        assertThat(detailUsageStat.getEndTimeStamp()).isEqualTo(endTimeStamp);
        assertThat(detailUsageStat.getTotalUsedTime()).isEqualTo(endTimeStamp - startTimeStamp);
    }

    @Test
    public void getAppList호출시_설치된_앱리스트조회를_요청한다() throws Exception {
        subject.getAppList();

        verify(mockSystemServiceBridge).getInstalledLaunchableApps();
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