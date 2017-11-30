package com.appbee.appbeemobile.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Single;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConfigServiceTest {

    private ConfigService subject;

    @Mock
    private ConfigAPI mockConfigAPI;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        MockitoAnnotations.initMocks(this);
        subject = new ConfigService(mockConfigAPI);

        when(mockConfigAPI.getAppVersion()).thenReturn(Single.just(123L));
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void getAppVersion호출시_앱버전을_리턴한다() throws Exception {
        assertThat(subject.getAppVersion()).isEqualTo(123L);
    }
}