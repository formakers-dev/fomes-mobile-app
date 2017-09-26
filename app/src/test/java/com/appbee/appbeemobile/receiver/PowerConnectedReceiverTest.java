package com.appbee.appbeemobile.receiver;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.network.AppStatService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PowerConnectedReceiverTest {

    private PowerConnectedReceiver subject;

    @Mock
    AppStatService mockAppStatService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new PowerConnectedReceiver(mockAppStatService);
    }

    @Test
    public void onReceive에서_PowerConnect되었을때_단기통계데이터를_서버로_전송한다() throws Exception {
        subject.onReceive(RuntimeEnvironment.application.getApplicationContext(), new Intent(Intent.ACTION_POWER_CONNECTED));

        verify(mockAppStatService).sendShortTermStats();
    }
}