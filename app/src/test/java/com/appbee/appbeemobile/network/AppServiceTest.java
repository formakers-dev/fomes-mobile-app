package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;

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

import retrofit2.Response;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
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
    public void getInfos호출시_요청한패키지명들에_대한_앱정보목록조회를_요청한다() throws Exception {
        List<String> packageNames = new ArrayList<>();
        packageNames.add("com.test.app1");
        packageNames.add("com.test.app2");

        when(mockAppAPI.getInfo(anyString(), any(List.class))).thenReturn(Observable.just(Response.success(null)));

        AppService.AppInfosServiceCallback mockAppInfosServiceCallback = mock(AppService.AppInfosServiceCallback.class);
        subject.getInfos(packageNames, mockAppInfosServiceCallback);

        verify(mockAppAPI).getInfo(anyString(), packageNamesCaptor.capture());
        List<String> actualPackageNames = packageNamesCaptor.getValue();
        assertEquals(actualPackageNames.get(0), "com.test.app1");
        assertEquals(actualPackageNames.get(1), "com.test.app2");
    }

    @Test
    public void getInfos호출시_요청한패키지명들에_대한_앱정보목록조회를_리턴한다() throws Exception {
        List<AppInfo> returnAppInfo = new ArrayList<>();
        returnAppInfo.add(new AppInfo("packageNameA", "appNameA", "categoryId1A", "categoryName1A", "categoryId2A", "categoryName2A"));
        returnAppInfo.add(new AppInfo("packageNameB", "appNameB", "categoryId1B", "categoryName1B", "categoryId2B", "categoryName2B"));

        when(mockAppAPI.getInfo(anyString(), any(List.class))).thenReturn(Observable.just(Response.success(returnAppInfo)));

        AppService.AppInfosServiceCallback mockAppInfosServiceCallback = mock(AppService.AppInfosServiceCallback.class);
        subject.getInfos(mock(List.class), mockAppInfosServiceCallback);

        verify(mockAppInfosServiceCallback).onSuccess(appInfosCaptor.capture());
        List<AppInfo> appInfos = appInfosCaptor.getValue();
        assertEquals(appInfos.size(), 2);
        assertAppInfo(appInfos.get(0), "packageNameA", "appNameA", "categoryId1A", "categoryName1A", "categoryId2A", "categoryName2A");
        assertAppInfo(appInfos.get(1), "packageNameB", "appNameB", "categoryId1B", "categoryName1B", "categoryId2B", "categoryName2B");
    }

    @Test
    public void postUncrawledApps호출시_크롤링되지_않은_앱목록을_서버로_전송된다() throws Exception {
        List<String> mockUncrawledAppList = new ArrayList<>();
        mockUncrawledAppList.add("packageNameA");
        mockUncrawledAppList.add("packageNameB");
        mockUncrawledAppList.add("packageNameC");
        when(mockAppAPI.postUncrawledApps(anyString(), any(List.class))).thenReturn(Observable.just(Response.success(true)));

        subject.postUncrawledApps(mockUncrawledAppList);

        verify(mockAppAPI).postUncrawledApps(anyString(), eq(mockUncrawledAppList));
    }

    private void assertAppInfo(AppInfo appInfo, String packageName, String appName, String categoryId1, String categoryName1, String categoryId2, String categoryName2) {
        assertEquals(appInfo.getPackageName(), packageName);
        assertEquals(appInfo.getAppName(), appName);
        assertEquals(appInfo.getCategoryId1(), categoryId1);
        assertEquals(appInfo.getCategoryName1(), categoryName1);
        assertEquals(appInfo.getCategoryId2(), categoryId2);
        assertEquals(appInfo.getCategoryName2(), categoryName2);
    }
}