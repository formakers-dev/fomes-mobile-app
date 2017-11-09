package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.ShortTermStat;

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
import static org.mockito.Mockito.never;
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
    private TimeHelper mockTimeHelper;

    @Captor
    ArgumentCaptor<List<NativeAppInfo>> appInfos = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<EventStat>> eventStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<ShortTermStat>> shortTermStatsCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new AppStatService(mockAppUsageDataHelper, mockStatAPI, mockLocalStorageHelper, mockTimeHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void sendShortTermStats호출시_가공된_단기통계데이터를_조회하여_서버로_전송한다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        mockShortTermStats.add(new ShortTermStat("anyPackage", 1000L, 3000L, 2000L));
        when(mockAppUsageDataHelper.getShortTermStats(anyLong(), anyLong())).thenReturn(mockShortTermStats);
        when(mockStatAPI.getLastUpdateStatTimestamp(anyString())).thenReturn(Observable.just(1234567890L));
        when(mockStatAPI.sendShortTermStats(anyString(), anyLong(), any(List.class))).thenReturn(mock(Observable.class));

        subject.sendShortTermStats(0L);

        verify(mockStatAPI).sendShortTermStats(anyString(), anyLong(), shortTermStatsCaptor.capture());
        ShortTermStat actualShortTermStat = shortTermStatsCaptor.getValue().get(0);
        assertEquals(actualShortTermStat.getPackageName(), "anyPackage");
        assertEquals(actualShortTermStat.getStartTimeStamp(), 1000L);
        assertEquals(actualShortTermStat.getEndTimeStamp(), 3000L);
        assertEquals(actualShortTermStat.getTotalUsedTime(), 2000L);
    }

    @Test
    public void sendShortTermStats호출시_단기통계데이터가_없는경우_서버로_전송하지_않는다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        when(mockAppUsageDataHelper.getShortTermStats(anyLong(), anyLong())).thenReturn(mockShortTermStats);
        when(mockStatAPI.getLastUpdateStatTimestamp(anyString())).thenReturn(Observable.just(1234567890L));

        subject.sendShortTermStats(0L);

        verify(mockStatAPI, never()).sendShortTermStats(anyString(), anyLong(), any(List.class));
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
    public void getShortTermStats호출시_특정시간으로부터의_단기통계데이터를_서버에서_가져온다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        mockShortTermStats.add(new ShortTermStat("anyPackage1", 1001L, 3000L, 2000L));
        mockShortTermStats.add(new ShortTermStat("anyPackage2", 1002L, 3000L, 2000L));
        when(mockTimeHelper.getCurrentTime()).thenReturn(0L);
        when(mockStatAPI.getShortTermStats(anyString(), anyLong())).thenReturn(Observable.just(mockShortTermStats));

        subject.getShortTermStats().subscribe(result -> {
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0).getPackageName()).isEqualTo("anyPackage1");
            assertThat(result.get(0).getStartTimeStamp()).isEqualTo(1001L);
            assertThat(result.get(1).getPackageName()).isEqualTo("anyPackage2");
            assertThat(result.get(1).getStartTimeStamp()).isEqualTo(1002L);
        });
    }
}