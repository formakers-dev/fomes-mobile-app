package com.appbee.appbeemobile.manager;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.model.AppInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPackageManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
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

    @Test
    public void getInstalledLaunchableApps호출시_설치된_앱리스트를_리턴한다() throws Exception {
        List<ResolveInfo> mockReturnList = new ArrayList<>();
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.isDefault = true;
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName =  "package";
        shadowOf(resolveInfo).setLabel("app_name");
        mockReturnList.add(resolveInfo);

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ShadowPackageManager shadowPackageManager = shadowOf(RuntimeEnvironment.application.getPackageManager());
        shadowPackageManager.addResolveInfoForIntent(intent, mockReturnList);

        List<AppInfo> appList = subject.getInstalledLaunchableApps();
        assertThat(appList.size()).isEqualTo(1);
        assertThat(appList.get(0).getAppName()).isEqualTo("app_name");
        assertThat(appList.get(0).getPakageName()).isEqualTo("package");
    }

    @Test
    public void getUsageStats호출시_일별통계정보를_조회한다() throws Exception {
        subject.getUsageStats(1000L, 1100L);

        verify(mockUsageStatsManager).queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 1000L, 1100L);
    }
}