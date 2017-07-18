package com.appbee.appbeemobile.activity;

import android.util.Log;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.DailyUsageStat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

     private ActivityController<MainActivity> activityController;

    @Inject
    StatManager statManager;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
         activityController = Robolectric.buildActivity(MainActivity.class);
    }

    @Test
    public void onCreate앱시작시_앱목록데이터를_조회하여_출력한다() throws Exception {
        List<AppInfo> mockAppInfoList = new ArrayList<>();
        AppInfo appInfo = new AppInfo("package_name", "app_name");
        mockAppInfoList.add(appInfo);
        when(statManager.getAppList()).thenReturn(mockAppInfoList);

        activityController.create();

        List<ShadowLog.LogItem> logs = ShadowLog.getLogs();
        boolean isLogContainingAppInfo = logs.stream()
                .anyMatch(t -> t.type == Log.DEBUG && t.msg.contains("package_name, app_name"));
        assertThat(isLogContainingAppInfo).isTrue();
    }

    @Test
    public void onCreate앱시작시연간일별통계데이터를_조회하여_출력한다() throws Exception {
        Map<String, DailyUsageStat> mockDailUsageStats = new HashMap<>();
        mockDailUsageStats.put("anyStat", new DailyUsageStat("anyPackage", "20170717", 1000L));
        when(statManager.getUserAppDailyUsageStatsForYear()).thenReturn(mockDailUsageStats);

        activityController.create();

        List<ShadowLog.LogItem> logs = ShadowLog.getLogs();
        boolean isLogContainingStat = logs.stream()
                .anyMatch(t -> t.type == Log.DEBUG && t.msg.contains("anyPackage,20170717,1000"));
        assertThat(isLogContainingStat).isTrue();
    }

    @Test
    public void onCreate앱시작시_단기통계데이터를_조회하여_출력한다() throws Exception {
        //TODO : 단기통계 출력
    }
}