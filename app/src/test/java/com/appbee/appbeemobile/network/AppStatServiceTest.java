package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.util.TimeUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.http.RealResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppStatServiceTest {

    private AppStatService subject;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Mock
    private AppUsageDataHelper mockAppUsageDataHelper;

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

    @Captor
    ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new AppStatService(mockAppUsageDataHelper, mockStatAPI, mockLocalStorageHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_TOKEN");
    }

    @Test
    public void sendAppList호출시_설치앱리스트를_조회하여_서버로_전송한다() throws Exception {
        List<AppInfo> mockAppInfoList = new ArrayList<>();
        mockAppInfoList.add(new AppInfo("package_name", "app_name"));
        when(mockAppUsageDataHelper.getAppList()).thenReturn(mockAppInfoList);

        when(mockStatAPI.sendAppInfoList(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendAppList(mock(AppStatServiceCallback.class));

        verify(mockStatAPI).sendAppInfoList(anyString(), appInfos.capture());
        List<AppInfo> actualAppInfos = appInfos.getValue();
        assertEquals(actualAppInfos.get(0).getPackageName(), "package_name");
        assertEquals(actualAppInfos.get(0).getAppName(), "app_name");
    }

    @Test
    public void sendEventStats호출시_단기통계데이터를_조회하여_서버로_전송한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("package_name", 1, 1000L));
        when(mockAppUsageDataHelper.getEventStats(anyLong())).thenReturn(mockEventStatList);
        when(mockStatAPI.sendEventStats(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendEventStats(mock(AppStatServiceCallback.class));

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
        when(mockStatAPI.sendLongTermStats(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendLongTermStats(mock(AppStatServiceCallback.class));

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
        when(mockStatAPI.sendShortTermStats(anyString(), any(List.class))).thenReturn(mock(Call.class));

        subject.sendShortTermStats(mock(AppStatServiceCallback.class));

        verify(mockStatAPI).sendShortTermStats(anyString(), shortTermStatsCaptor.capture());
        ShortTermStat actualShortTermStat = shortTermStatsCaptor.getValue().get(0);
        assertEquals(actualShortTermStat.getPackageName(), "anyPackage");
        assertEquals(actualShortTermStat.getStartTimeStamp(), 1000L);
        assertEquals(actualShortTermStat.getEndTimeStamp(), 3000L);
        assertEquals(actualShortTermStat.getTotalUsedTime(), 2000L);
    }

    @Test
    public void getStartDate호출시_LocalStorageHelper에_저장된_lastUsageTime이_없으면_현시점에서_일주일전_시간을_리턴한다() throws Exception {
        assertThat(subject.getStartTime()).isEqualTo(TimeUtil.getCurrentTime() - 7*24*60*60*1000);
    }

    @Test
    public void getStartDate호출시_LocalStorageHelper에_저장된_lastUsageTime이_있으면_저장된_시간을_리턴한다() throws Exception {
        when(mockLocalStorageHelper.getLastUsageTime()).thenReturn(2000L);

        assertThat(subject.getStartTime()).isEqualTo(2000L);
    }

    @Test
    public void sendShortTermStatsAPI호출후_성공시_현재시간을_LocalStorage에_저장한다() throws Exception {
        mockingCall(retrofit2.Response.success(null));

        subject.sendShortTermStats(mock(AppStatServiceCallback.class));

        verify(mockLocalStorageHelper).setLastUsageTime(TimeUtil.getCurrentTime());
    }

    @Test
    public void sendShortTermStatsAPI호출후_실패시_현재시간을_LocalStorage에_저장하지않는다() throws Exception {
        mockingCall(retrofit2.Response.error(400, new RealResponseBody(null, null)));

        subject.sendShortTermStats(mock(AppStatServiceCallback.class));

        verify(mockLocalStorageHelper, times(0)).setLastUsageTime(anyLong());
    }

    private void mockingCall(Response<Boolean> response) {
        Call<Boolean> mockCall = mock(Call.class);
        when(mockStatAPI.sendShortTermStats(anyString(), any(List.class))).thenReturn(mockCall);
        doAnswer(invocation -> {
            ((Callback<Boolean>) invocation.getArguments()[0]).onResponse(mockCall, response);
            return null;
        }).when(mockCall).enqueue(any(Callback.class));
    }
}