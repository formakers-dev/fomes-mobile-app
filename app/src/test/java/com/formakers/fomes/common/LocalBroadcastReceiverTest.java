package com.formakers.fomes.common;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.main.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class LocalBroadcastReceiverTest {

    @Inject ChannelManager mockChannelManager;

    @Before
    public void setUp() throws Exception {
        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);

        when(mockChannelManager.onNotificationClick(any())).thenReturn(new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class));
    }

    @Test
    public void 알림이_클릭되었을때__알림_클릭_콜백을_호출하고_리턴된_목적지로_이동한다() {
        Intent intent = new Intent("com.formakers.fomes.NOTI_CLICKED");
        intent.putExtra(FomesConstants.Notification.TITLE, "타이틀");

        when(mockChannelManager.onNotificationClick(intent.getExtras())).thenReturn(new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class));

        ApplicationProvider.getApplicationContext().sendBroadcast(intent);

        ArgumentCaptor<Bundle> argumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(mockChannelManager).onNotificationClick(argumentCaptor.capture());

        Bundle bundle = argumentCaptor.getValue();
        assertThat(bundle.getString(FomesConstants.Notification.TITLE)).isEqualTo("타이틀");

        Intent nextStartedIntent = shadowOf(((Application) ApplicationProvider.getApplicationContext())).getNextStartedActivity();
        assertThat(nextStartedIntent.getComponent().getClassName()).contains(MainActivity.class.getSimpleName());
    }

    @Test
    public void 알림이_취소되었을때__알림_취소_콜백을_호출한다() {
        Intent intent = new Intent("com.formakers.fomes.NOTI_CANCELLED");
        intent.putExtra(FomesConstants.Notification.TITLE, "타이틀");

        ApplicationProvider.getApplicationContext().sendBroadcast(intent);

        ArgumentCaptor<Bundle> argumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(mockChannelManager).onNotificationCancel(argumentCaptor.capture());

        Bundle bundle = argumentCaptor.getValue();
        assertThat(bundle.getString(FomesConstants.Notification.TITLE)).isEqualTo("타이틀");
    }
}