package com.appbee.appbeemobile.receiver;

import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.ShortTermStat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@Ignore
public class ScreenOffReceiverTest {
    private ScreenOffReceiver subject;

    private StatManager statManager = mock(StatManager.class);

    @Before
    public void setUp() throws Exception {
        subject = new ScreenOffReceiver();
    }

    @Test
    public void onReceive수행시_detailUsageStat을_조회한다() throws Exception {
        List<ShortTermStat> mockShortTermStatList = new ArrayList<>();
        mockShortTermStatList.add(new ShortTermStat("package_name", 1000L, 1100L, 100L));
        when(statManager.getShortTermStats()).thenReturn(mockShortTermStatList);

        Intent intent = new Intent(Intent.ACTION_SCREEN_OFF);
        subject.onReceive(RuntimeEnvironment.application, intent);

        verify(statManager).getShortTermStats();
        assertThat(isLogContains("package_name, 1000, 1100, 100")).isTrue();
    }

    private boolean isLogContains(String containedMessage) {
        return ShadowLog.getLogs().stream().anyMatch(t -> t.type== Log.DEBUG && t.msg.contains(containedMessage));
    }
}