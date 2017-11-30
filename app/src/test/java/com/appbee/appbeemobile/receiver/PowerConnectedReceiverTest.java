package com.appbee.appbeemobile.receiver;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PowerConnectedReceiverTest {

    private PowerConnectedReceiver subject;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        MockitoAnnotations.initMocks(this);
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
        subject = new PowerConnectedReceiver();

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onReceive에서_PowerConnect되었을때_단기통계데이터를_서버로_전송한다() throws Exception {
        subject.onReceive(RuntimeEnvironment.application.getApplicationContext(), new Intent(Intent.ACTION_POWER_CONNECTED));

        verify(mockAppUsageDataHelper).sendShortTermStatAndAppUsages(any());
    }

    @Test
    public void onReceive에서_PowerConnect되었을때_권한이없으면_아무것도하지않는다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        subject.onReceive(RuntimeEnvironment.application.getApplicationContext(), new Intent(Intent.ACTION_POWER_CONNECTED));
        verify(mockAppUsageDataHelper, never()).sendShortTermStatAndAppUsages(any());
    }
}