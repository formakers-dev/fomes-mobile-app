package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import rx.Single;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigServiceTest {

    private ConfigService subject;

    @Mock
    private ConfigAPI mockConfigAPI;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        MockitoAnnotations.initMocks(this);
        subject = new ConfigService(mockConfigAPI, mockLocalStorageHelper);

        when(mockLocalStorageHelper.getAccessToken()).thenReturn("myToken");
        when(mockConfigAPI.getAppVersion()).thenReturn(Single.just(123L));
        when(mockConfigAPI.getExcludePackageNames(eq("myToken"))).thenReturn(Single.just(Arrays.asList("com.kakao.talk", "com.naver.search")));
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void getAppVersion호출시_앱버전을_리턴한다() throws Exception {
        assertThat(subject.getAppVersion()).isEqualTo(123L);
    }

    @Test
    public void getExcludePackageNames호출시_앱분석시_제외할_앱목록을_리턴한다() throws Exception {
        assertThat(subject.getExcludePackageNames()).isEqualTo(Arrays.asList("com.kakao.talk", "com.naver.search"));
    }
}