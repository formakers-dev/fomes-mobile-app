package com.formakers.fomes.common.network;

import com.formakers.fomes.helper.AppBeeAPIHelper;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

abstract class AbstractServiceTest {

    @Mock
    private AppBeeAPIHelper mockAppBeeAPIHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.trampoline());

        when(mockAppBeeAPIHelper.refreshExpiredToken()).thenReturn(observable -> observable);
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    protected <T> void verifyToCheckExpiredToken(Observable<T> observable) {
        observable.subscribe(new TestSubscriber<>());
        verify(mockAppBeeAPIHelper).refreshExpiredToken();
    }

    public AppBeeAPIHelper getMockAppBeeAPIHelper() {
        return mockAppBeeAPIHelper;
    }
}
