package com.appbee.appbeemobile.manager;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import com.appbee.appbeemobile.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SystemServiceBridgeTest {
    private SystemServiceBridge subject;

    private UsageStatsManager mockUsageStatsManager;

    @Before
    public void setUp() throws Exception {
        mockUsageStatsManager = mock(UsageStatsManager.class);
        shadowOf(RuntimeEnvironment.application).setSystemService(Context.USAGE_STATS_SERVICE, mockUsageStatsManager);

        subject = new SystemServiceBridge(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void getUsageStatEvents호출시_디바이스에_기록된_앱사용정보를_리턴한다() throws Exception {
        UsageEvents usageEvents = mock(UsageEvents.class);

        when(usageEvents.hasNextEvent()).thenAnswer(new Answer<Boolean>() {
            private int callCount = 0;
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                if(callCount < 2) {
                    callCount++;
                    return true;
                } else {
                    return false;
                }
            }
        });
        when(mockUsageStatsManager.queryEvents(anyLong(), anyLong())).thenReturn(usageEvents);

        subject.getUsageStatEvents(0L, 1L);

        verify(usageEvents, times(2)).getNextEvent(any());
    }

}