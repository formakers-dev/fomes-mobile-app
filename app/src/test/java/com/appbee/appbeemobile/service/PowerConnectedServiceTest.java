package com.appbee.appbeemobile.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PowerConnectedServiceTest {

    @Inject
    Context context;

    @Inject
    UserService userService;

    @Inject
    TimeHelper timeHelper;

    @Test
    public void onStartCommand호출시_PowerConnectedReceiver를_등록한다() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        PowerConnectedService subject = Robolectric.setupService(PowerConnectedService.class);

        int onStartCommandResult = subject.onStartCommand(new Intent(RuntimeEnvironment.application.getBaseContext(), PowerConnectedService.class), Service.START_FLAG_RETRY, 1);

        List<ShadowApplication.Wrapper> registeredReceivers = shadowOf(RuntimeEnvironment.application).getRegisteredReceivers();

        boolean isPowerConnectedReceiverRegistered = false;
        for (ShadowApplication.Wrapper receiver : registeredReceivers) {
            BroadcastReceiver broadcastReceiver = receiver.getBroadcastReceiver();
            if (broadcastReceiver.getClass().getSimpleName().equals(PowerConnectedReceiver.class.getSimpleName())) {
                isPowerConnectedReceiverRegistered = true;
                break;
            }
        }
        assertThat(isPowerConnectedReceiverRegistered).isTrue();
        assertThat(onStartCommandResult).isEqualTo(Service.START_STICKY);
    }
}