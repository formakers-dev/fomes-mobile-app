package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AnalysisResult;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.NativeLongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.helper.TimeHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AppStatServiceTest {

    private AppStatService subject;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Mock
    private AppUsageDataHelper mockAppUsageDataHelper;

    @Mock
    private StatAPI mockStatAPI;

    @Mock
    private TimeHelper timeHelper;

    @Captor
    ArgumentCaptor<List<NativeAppInfo>> appInfos = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<EventStat>> eventStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<ShortTermStat>> shortTermStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<LongTermStat>> longTermStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new AppStatService(mockAppUsageDataHelper, mockStatAPI, mockLocalStorageHelper, timeHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void sendLongTermStatsFor2Years호출시_연간일별통계를_조회하여_서버로_전송한다() throws Exception {
        List<NativeLongTermStat> mockNativeLongTermStatList = new ArrayList<>();
        mockNativeLongTermStatList.add(createMockNativeLongTermStat("anyPackage", 100L, 200L, 300L, 400L));

        when(mockAppUsageDataHelper.getNativeLongTermStatsFor2Years()).thenReturn(mockNativeLongTermStatList);
        when(mockStatAPI.sendLongTermStatsYearly(anyString(), any(List.class))).thenReturn(mock(Observable.class));

        subject.sendLongTermStatsFor2Years();

        ArgumentCaptor<List<NativeLongTermStat>> navtiveLongTermStatCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockStatAPI).sendLongTermStatsYearly(anyString(), navtiveLongTermStatCaptor.capture());
        assertThat(navtiveLongTermStatCaptor.getValue().size()).isEqualTo(1);
        NativeLongTermStat nativeLongTermStat = navtiveLongTermStatCaptor.getValue().get(0);
        assertEquals(nativeLongTermStat.getPackageName(), "anyPackage");
        assertEquals(nativeLongTermStat.getBeginTimeStamp(), 100L);
        assertEquals(nativeLongTermStat.getEndTimeStamp(), 200L);
        assertEquals(nativeLongTermStat.getLastTimeUsed(), 300L);
        assertEquals(nativeLongTermStat.getTotalTimeInForeground(), 400L);
    }

    @Test
    public void sendShortTermStats호출시_가공된_단기통계데이터를_조회하여_서버로_전송한다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        mockShortTermStats.add(new ShortTermStat("anyPackage", 1000L, 3000L, 2000L));
        when(mockAppUsageDataHelper.getShortTermStats(anyLong())).thenReturn(mockShortTermStats);
        when(mockStatAPI.sendShortTermStats(anyString(), any(List.class))).thenReturn(mock(Observable.class));

        subject.sendShortTermStats();

        verify(mockStatAPI).sendShortTermStats(anyString(), shortTermStatsCaptor.capture());
        ShortTermStat actualShortTermStat = shortTermStatsCaptor.getValue().get(0);
        assertEquals(actualShortTermStat.getPackageName(), "anyPackage");
        assertEquals(actualShortTermStat.getStartTimeStamp(), 1000L);
        assertEquals(actualShortTermStat.getEndTimeStamp(), 3000L);
        assertEquals(actualShortTermStat.getTotalUsedTime(), 2000L);
    }

    @Test
    public void sendAnalysisResult호출시_분석된_결과를_서버로_전송한다() throws Exception {
        AnalysisResult mockResult = mock(AnalysisResult.class);

        when(mockStatAPI.sendAnalysisResult(anyString(), any(AnalysisResult.class))).thenReturn(mock(Observable.class));
        subject.sendAnalysisResult(mockResult);

        ArgumentCaptor<AnalysisResult> analysisResultArgumentCaptor = ArgumentCaptor.forClass(AnalysisResult.class);
        verify(mockStatAPI).sendAnalysisResult(anyString(), analysisResultArgumentCaptor.capture());
        AnalysisResult result = analysisResultArgumentCaptor.getValue();
        assertThat(result).isEqualTo(mockResult);
    }

    @Test
    public void getStartDate호출시_LocalStorageHelper에_저장된_lastUsageTime이_없으면_현시점에서_일주일전_시간을_리턴한다() throws Exception {
        long currentTime = 1503871200000L;
        when(timeHelper.getCurrentTime()).thenReturn(currentTime);
        assertThat(subject.getStartTime()).isEqualTo(currentTime - 604800000L); // 604800000 = 7 * 24 * 60 * 60 * 1000
    }

    @Test
    public void getStartDate호출시_LocalStorageHelper에_저장된_lastUsageTime이_있으면_저장된_시간을_리턴한다() throws Exception {
        when(mockLocalStorageHelper.getLastUsageTime()).thenReturn(2000L);

        assertThat(subject.getStartTime()).isEqualTo(2000L);
    }

    @Test
    public void getUsedPackageNameList호출시_단기통계에서_사용이력이있는_appList를_리턴한다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        mockShortTermStats.add(new ShortTermStat("com.package.name1", 0L, 100L, 1000L));

        when(mockAppUsageDataHelper.getShortTermStats(0L)).thenReturn(mockShortTermStats);

        List<String> usedPackageNameList = subject.getUsedPackageNameList();

        assertThat(usedPackageNameList.size()).isEqualTo(1);
        assertThat(usedPackageNameList.get(0)).isEqualTo("com.package.name1");
    }

    @Test
    public void sendLongTermStatsFor3Months() throws Exception {
        List<NativeLongTermStat> nativeLongTermStatList = new ArrayList<>();
        nativeLongTermStatList.add(new NativeLongTermStat("package1", 100L, 200L, 300L, 400L));
        nativeLongTermStatList.add(new NativeLongTermStat("package2", 200L, 300L, 400L, 500L));
        when(mockAppUsageDataHelper.getLongTermStatsFor3Months()).thenReturn(nativeLongTermStatList);
        when(mockStatAPI.sendLongTermStatsMonthly(anyString(), any(List.class))).thenReturn(mock(Observable.class));

        subject.sendLongTermStatsFor3Months();

        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockStatAPI).sendLongTermStatsMonthly(anyString(), listCaptor.capture());
        List<NativeLongTermStat> list = listCaptor.getValue();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0).getPackageName()).isEqualTo("package1");
        assertThat(list.get(0).getBeginTimeStamp()).isEqualTo(100L);
        assertThat(list.get(0).getEndTimeStamp()).isEqualTo(200L);
        assertThat(list.get(0).getLastTimeUsed()).isEqualTo(300L);
        assertThat(list.get(0).getTotalTimeInForeground()).isEqualTo(400L);

    }

    private NativeLongTermStat createMockNativeLongTermStat(String packageName, long beginTimeStamp, long endTimeStamp, long lastTimeUsed, long totalUsedTime) {
        NativeLongTermStat mockNativeLongTermStat = mock(NativeLongTermStat.class);
        when(mockNativeLongTermStat.getPackageName()).thenReturn(packageName);
        when(mockNativeLongTermStat.getBeginTimeStamp()).thenReturn(beginTimeStamp);
        when(mockNativeLongTermStat.getEndTimeStamp()).thenReturn(endTimeStamp);
        when(mockNativeLongTermStat.getTotalTimeInForeground()).thenReturn(totalUsedTime);
        when(mockNativeLongTermStat.getLastTimeUsed()).thenReturn(lastTimeUsed);
        return mockNativeLongTermStat;
    }
}