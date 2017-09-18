package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.User;

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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class UserServiceTest {
    private UserService subject;

    @Mock
    private AppUsageDataHelper mockAppUsageDataHelper;

    @Mock
    private UserAPI mockUserAPI;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Captor
    ArgumentCaptor<List<NativeAppInfo>> appInfos = ArgumentCaptor.forClass(List.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new UserService(mockAppUsageDataHelper, mockUserAPI, mockLocalStorageHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void sendAppList호출시_설치앱리스트를_조회하여_서버로_전송한다() throws Exception {
        List<NativeAppInfo> mockNativeAppInfoList = new ArrayList<>();
        mockNativeAppInfoList.add(new NativeAppInfo("package_name", "app_name"));
        Observable<List<NativeAppInfo>> mockObservable = Observable.just(mockNativeAppInfoList);
        when(mockAppUsageDataHelper.getAppList()).thenReturn(mockObservable);
        when(mockUserAPI.sendAppInfoList(anyString(), any(List.class))).thenReturn(mock(Observable.class));

        subject.sendAppList();

        verify(mockUserAPI).sendAppInfoList(anyString(), appInfos.capture());
        List<NativeAppInfo> actualNativeAppInfos = appInfos.getValue();
        assertEquals(actualNativeAppInfos.get(0).getPackageName(), "package_name");
        assertEquals(actualNativeAppInfos.get(0).getAppName(), "app_name");
    }

    @Test
    public void sendUser호출시_유저정보를_서버로_전송한다() throws Exception {
        when(mockUserAPI.sendUser(any(User.class))).thenReturn(mock(Observable.class));

        User mockUser = mock(User.class);
        subject.sendUser(mockUser);

        verify(mockUserAPI).sendUser(mockUser);
    }
}