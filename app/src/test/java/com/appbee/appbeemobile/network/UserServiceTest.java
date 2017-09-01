package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook(){
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
    public void sendAppList호출시_설치앱리스트를_조회하여_서버로_전송한다() throws Exception {
        List<NativeAppInfo> mockNativeAppInfoList = new ArrayList<>();
        mockNativeAppInfoList.add(new NativeAppInfo("package_name", "app_name"));
        when(mockAppUsageDataHelper.getAppList()).thenReturn(mockNativeAppInfoList);
        when(mockLocalStorageHelper.getUUID()).thenReturn("uuid");
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