package com.appbee.appbeemobile.service;

import com.appbee.appbeemobile.manager.AppStatServiceManager;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.UsageStatEvent;
import com.appbee.appbeemobile.model.UserApps;
import com.appbee.appbeemobile.network.HTTPService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppStatServiceManagerTest {
    private AppStatServiceManager subject;

    @Mock
    private StatManager mockStatManager;

    @Mock
    private HTTPService mockHttpService;

    @Captor
    ArgumentCaptor<UserApps> userAppsCaptor = ArgumentCaptor.forClass(UserApps.class);

    @Captor
    ArgumentCaptor<List<UsageStatEvent>> usageStateEventsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<DailyUsageStat>> dailyUsageStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new AppStatServiceManager(mockStatManager, mockHttpService);
    }

    @Test
    public void sendAppList호출시_설치앱리스트를_조회하여_서버로_전송한다() throws Exception {
        List<AppInfo> mockAppInfoList = new ArrayList<>();
        mockAppInfoList.add(new AppInfo("package_name", "app_name"));
        when(mockStatManager.getAppList()).thenReturn(mockAppInfoList);

        when(mockHttpService.sendAppInfoList(anyString(), any(UserApps.class))).thenReturn(mock(Call.class));

        subject.sendAppList();

        verify(mockHttpService).sendAppInfoList(any(String.class), userAppsCaptor.capture());
        UserApps actualUserApps = userAppsCaptor.getValue();
        assertEquals(actualUserApps.getUserId(), "testUser");
        assertEquals(actualUserApps.getApps().get(0).getPackageName(), "package_name");
        assertEquals(actualUserApps.getApps().get(0).getAppName(), "app_name");
    }

    @Test
    public void sendDetailUsageStatsByEvent호출시_단기통계데이터를_조회하여_서버로_전송한다() throws Exception {
        List<UsageStatEvent> mockUsageStatEventList = new ArrayList<>();
        mockUsageStatEventList.add(new UsageStatEvent("package_name", 1, 1000L));
        when(mockStatManager.getDetailUsageEvents()).thenReturn(mockUsageStatEventList);
        when(mockHttpService.sendDetailUsageStatsByEvent(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendDetailUsageStatsByEvent();

        verify(mockHttpService).sendDetailUsageStatsByEvent(anyString(), usageStateEventsCaptor.capture());
        UsageStatEvent actualUsageStatEvent = usageStateEventsCaptor.getValue().get(0);
        assertEquals(actualUsageStatEvent.getPackageName(), "package_name");
        assertEquals(actualUsageStatEvent.getEventType(), 1);
        assertEquals(actualUsageStatEvent.getTimeStamp(), 1000L);
    }

    @Test
    public void sendDailyUsageStats호출시_연간일별통계를_조회하여_서버로_전송한다() throws Exception {
        List<DailyUsageStat> mockDailyUsageStats = new ArrayList<>();
        mockDailyUsageStats.add(new DailyUsageStat("anyPackage", "20170717", 1000L));
        when(mockStatManager.getUserAppDailyUsageStatsForYear()).thenReturn(mockDailyUsageStats);
        when(mockHttpService.sendDailyUsageStats(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendDailyUsageStats();

        verify(mockHttpService).sendDailyUsageStats(anyString(), dailyUsageStatsCaptor.capture());
        DailyUsageStat actualDailyUsageStat = dailyUsageStatsCaptor.getValue().get(0);
        assertEquals(actualDailyUsageStat.getPackageName(), "anyPackage");
        assertEquals(actualDailyUsageStat.getLastUsedDate(), "20170717");
        assertEquals(actualDailyUsageStat.getTotalUsedTime(), 1000L);
    }
}