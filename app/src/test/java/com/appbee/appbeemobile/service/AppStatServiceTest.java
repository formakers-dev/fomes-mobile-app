package com.appbee.appbeemobile.service;

import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.network.StatAPI;

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

public class AppStatServiceTest {
    private AppStatService subject;

    @Mock
    private StatManager mockStatManager;

    @Mock
    private StatAPI mockStatAPI;

    @Captor
    ArgumentCaptor<List<AppInfo>> appInfos = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<EventStat>> eventStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<ShortTermStat>> shortTermStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<LongTermStat>> longTermStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new AppStatService(mockStatManager, mockStatAPI);
    }

    @Test
    public void sendAppList호출시_설치앱리스트를_조회하여_서버로_전송한다() throws Exception {
        List<AppInfo> mockAppInfoList = new ArrayList<>();
        mockAppInfoList.add(new AppInfo("package_name", "app_name"));
        when(mockStatManager.getAppList()).thenReturn(mockAppInfoList);

        when(mockStatAPI.sendAppInfoList(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendAppList();

        verify(mockStatAPI).sendAppInfoList(any(String.class), appInfos.capture());
        List<AppInfo> actualAppInfos = appInfos.getValue();
        assertEquals(actualAppInfos.get(0).getPackageName(), "package_name");
        assertEquals(actualAppInfos.get(0).getAppName(), "app_name");
    }

    @Test
    public void sendEventStats호출시_단기통계데이터를_조회하여_서버로_전송한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("package_name", 1, 1000L));
        when(mockStatManager.getEventStats()).thenReturn(mockEventStatList);
        when(mockStatAPI.sendEventStats(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendEventStats();

        verify(mockStatAPI).sendEventStats(anyString(), eventStatsCaptor.capture());
        EventStat actualEventStat = eventStatsCaptor.getValue().get(0);
        assertEquals(actualEventStat.getPackageName(), "package_name");
        assertEquals(actualEventStat.getEventType(), 1);
        assertEquals(actualEventStat.getTimeStamp(), 1000L);
    }

    @Test
    public void sendLongTermStats호출시_연간일별통계를_조회하여_서버로_전송한다() throws Exception {
        List<LongTermStat> mockLongTermStats = new ArrayList<>();
        mockLongTermStats.add(new LongTermStat("anyPackage", "20170717", 1000L));
        when(mockStatManager.getLongTermStatsForYear()).thenReturn(mockLongTermStats);
        when(mockStatAPI.sendLongTermStats(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendLongTermStats();

        verify(mockStatAPI).sendLongTermStats(anyString(), longTermStatsCaptor.capture());
        LongTermStat actualLongTermStat = longTermStatsCaptor.getValue().get(0);
        assertEquals(actualLongTermStat.getPackageName(), "anyPackage");
        assertEquals(actualLongTermStat.getLastUsedDate(), "20170717");
        assertEquals(actualLongTermStat.getTotalUsedTime(), 1000L);
    }

    @Test
    public void sendShortTermStats호출시_가공된_단기통계데이터를_조회하여_서버로_전송한다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        mockShortTermStats.add(new ShortTermStat("anyPackage", 1000L, 3000L, 2000L));
        when(mockStatManager.getShortTermStats()).thenReturn(mockShortTermStats);
        when(mockStatAPI.sendShortTermStats(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendShortTermStats();

        verify(mockStatAPI).sendShortTermStats(anyString(), shortTermStatsCaptor.capture());
        ShortTermStat actualShortTermStat = shortTermStatsCaptor.getValue().get(0);
        assertEquals(actualShortTermStat.getPackageName(), "anyPackage");
        assertEquals(actualShortTermStat.getStartTimeStamp(), 1000L);
        assertEquals(actualShortTermStat.getEndTimeStamp(), 3000L);
        assertEquals(actualShortTermStat.getTotalUsedTime(), 2000L);
    }
}