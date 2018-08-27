package com.formakers.fomes.network;

import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.ShortTermStat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AppStatServiceTest extends AbstractServiceTest {

    private AppStatService subject;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Mock
    private StatAPI mockStatAPI;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        subject = new AppStatService(mockStatAPI, mockLocalStorageHelper, getMockAppBeeAPIHelper());
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void sendShortTermStats호출시_전달받은_단기통계데이터를_서버로_전송한다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        mockShortTermStats.add(new ShortTermStat("anyPackage", 1000L, 3000L, 2000L));
        when(mockStatAPI.sendShortTermStats(anyString(), any(List.class))).thenReturn(mock(Observable.class));

        subject.sendShortTermStats(mockShortTermStats).subscribe(new TestSubscriber<>());

        ArgumentCaptor<List<ShortTermStat>> shortTermStatsCaptor = ArgumentCaptor.forClass(List.class);

        verify(mockStatAPI).sendShortTermStats(anyString(), shortTermStatsCaptor.capture());
        ShortTermStat actualShortTermStat = shortTermStatsCaptor.getValue().get(0);
        assertEquals(actualShortTermStat.getPackageName(), "anyPackage");
        assertEquals(actualShortTermStat.getStartTimeStamp(), 1000L);
        assertEquals(actualShortTermStat.getEndTimeStamp(), 3000L);
        assertEquals(actualShortTermStat.getTotalUsedTime(), 2000L);
    }

    @Test
    public void sendShortTermStats호출시_단기통계데이터가_없는경우_서버로_전송하지_않는다() throws Exception {
        subject.sendShortTermStats(new ArrayList<>());

        verify(mockStatAPI, never()).sendShortTermStats(anyString(), any(List.class));
    }

    @Test
    public void sendShortTermStats호출시_토큰_만료_여부를_확인한다() throws Exception {
        List<ShortTermStat> shortTermStatList = Collections.singletonList(new ShortTermStat("packageName", 0L, 999L, 999L));
        verifyToCheckExpiredToken(subject.sendShortTermStats(shortTermStatList).toObservable());
    }

    @Test
    public void postAppUsages호출시_앱별_사용정보통계를_서버로_전송한다() throws Exception {
        List<AppUsage> mockAppUsageList = new ArrayList<>();
        mockAppUsageList.add(new AppUsage("packageA", 1000L));
        mockAppUsageList.add(new AppUsage("packageB", 2000L));
        when(mockStatAPI.postUsages(anyString(), any(List.class))).thenReturn(mock(Observable.class));
        subject.sendAppUsages(mockAppUsageList).subscribe(new TestSubscriber<>());

        verify(mockStatAPI).postUsages(anyString(), eq(mockAppUsageList));
    }

    @Test
    public void sendAppUsages호출시_토큰_만료_여부를_확인한다() throws Exception {
        verifyToCheckExpiredToken(subject.sendAppUsages(new ArrayList<>()).toObservable());
    }
}