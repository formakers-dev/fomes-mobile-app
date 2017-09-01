package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.helper.TimeHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.http.RealResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_TOKEN");

        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void tearDown() {
        RxJavaPlugins.getInstance().reset();
    }

    @Test
    public void sendEventStats호출시_단기통계데이터를_조회하여_서버로_전송한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("package_name", 1, 1000L));
        when(mockAppUsageDataHelper.getEventStats(anyLong())).thenReturn(mockEventStatList);
        when(mockStatAPI.sendEventStats(anyString(), any(List.class))).thenReturn(mock(Observable.class));

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
        when(mockAppUsageDataHelper.getLongTermStats()).thenReturn(mockLongTermStats);
        when(mockStatAPI.sendLongTermStats(anyString(), any(List.class))).thenReturn(mock(Observable.class));

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
    public void getUsedPackageNameList호출시_장기통계에서_사용이력이있는_appList를_리턴한다() throws Exception {
        List<LongTermStat> mockLongTermStats = new ArrayList<>();
        mockLongTermStats.add(new LongTermStat("com.package.name1", "20170717", 1000L));

        when(mockAppUsageDataHelper.getLongTermStats()).thenReturn(mockLongTermStats);

        List<String> usedPackageNameList = subject.getUsedPackageNameList();

        assertThat(usedPackageNameList.size()).isEqualTo(1);
        assertThat(usedPackageNameList.get(0)).isEqualTo("com.package.name1");
    }
}