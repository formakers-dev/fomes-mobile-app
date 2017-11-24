package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.AppUsage;

import org.junit.After;
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

import retrofit2.Response;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppServiceTest {
    private static final String TEST_ACCESS_TOKEN = "TEST_ACCESS_TOKEN";
    private AppService subject;

    @Mock
    private AppAPI mockAppAPI;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Captor
    ArgumentCaptor<List<String>> packageNamesCaptor = ArgumentCaptor.forClass(List.class);

    @Captor
    ArgumentCaptor<List<AppInfo>> appInfosCaptor = ArgumentCaptor.forClass(List.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new AppService(mockAppAPI, mockLocalStorageHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn(TEST_ACCESS_TOKEN);

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }


    @Test
    public void postUncrawledApps호출시_크롤링되지_않은_앱목록을_서버로_전송된다() throws Exception {
        List<String> mockUncrawledAppList = new ArrayList<>();
        mockUncrawledAppList.add("packageNameA");
        mockUncrawledAppList.add("packageNameB");
        mockUncrawledAppList.add("packageNameC");
        when(mockAppAPI.postUncrawledApps(anyString(), any(List.class))).thenReturn(Observable.just(true));

        subject.postUncrawledApps(mockUncrawledAppList);

        verify(mockAppAPI).postUncrawledApps(anyString(), eq(mockUncrawledAppList));
    }

    @Test
    public void postAppUsages호출시_앱별_사용정보통계를_서버로_전송한다() throws Exception {
        List<AppUsage> mockAppUsageList = new ArrayList<>();
        mockAppUsageList.add(new AppUsage("packageA", 1000L));
        mockAppUsageList.add(new AppUsage("packageB", 2000L));
        when(mockAppAPI.postUsages(anyString(), any(List.class))).thenReturn(Observable.just(Response.success(true)));
        subject.sendAppUsages(mockAppUsageList);

        verify(mockAppAPI).postUsages(anyString(), eq(mockAppUsageList));
    }
}