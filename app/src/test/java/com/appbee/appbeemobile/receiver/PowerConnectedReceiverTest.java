package com.appbee.appbeemobile.receiver;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.network.AppStatService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PowerConnectedReceiverTest {

    private PowerConnectedReceiver subject;

    @Mock
    AppStatService mockAppStatService;

    @Mock
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new PowerConnectedReceiver(mockAppStatService, appBeeAndroidNativeHelper);

        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        when(mockAppStatService.getLastUpdateStatTimestamp()).thenReturn(Observable.just(0L));
        when(mockAppStatService.sendShortTermStats(anyLong())).thenReturn(Observable.just(true));

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onReceive에서_PowerConnect되었을때_단기통계데이터를_서버로_전송한다() throws Exception {
        subject.onReceive(RuntimeEnvironment.application.getApplicationContext(), new Intent(Intent.ACTION_POWER_CONNECTED));

        verify(mockAppStatService).sendShortTermStats(anyLong());
    }

    @Test
    public void onReceive에서_PowerConnect되었을때_권한이없으면_아무것도하지않는다() throws Exception {
        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        subject.onReceive(RuntimeEnvironment.application.getApplicationContext(), new Intent(Intent.ACTION_POWER_CONNECTED));
        verify(mockAppStatService, never()).sendShortTermStats(anyLong());
    }
}