package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.AppAPI;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.AppInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppServiceTest extends AbstractServiceTest {

    @Mock AppAPI mockAppAPI;
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;

    AppService subject;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");

        this.subject = new AppService(mockAppAPI, mockSharedPreferencesHelper, getMockAPIHelper());
    }

    @Test
    public void getAppInfo_호출시__해당_패키지명의_앱_정보를_요청한다() {
        when(mockAppAPI.getAppInfo(anyString(), anyString())).thenReturn(Observable.just(mock(AppInfo.class)));

        subject.requestAppInfo("test.com.test").subscribe(new TestSubscriber<>());

        verify(mockAppAPI).getAppInfo(eq("TEST_ACCESS_TOKEN"), eq("test.com.test"));
    }

}