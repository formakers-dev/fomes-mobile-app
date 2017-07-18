package com.appbee.appbeemobile.manager;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.model.AppInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPackageManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UsingPackageManagerTest {

    UsingPackageManager subject;
    private ShadowPackageManager shadowPackageManager;

    @Before
    public void setUp() throws Exception {
        shadowPackageManager = shadowOf(RuntimeEnvironment.application.getPackageManager());
        subject = new UsingPackageManager(RuntimeEnvironment.application);
    }

    @Test
    public void getAppList_설치된_앱리스트를_리턴한다() throws Exception {
        List<ResolveInfo> mockReturnList = new ArrayList<>();
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.isDefault = true;
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName =  "package";
        shadowOf(resolveInfo).setLabel("app_name");
        mockReturnList.add(resolveInfo);

        Intent intent = new Intent(Intent.ACTION_MAIN, null);

        shadowPackageManager.addResolveInfoForIntent(intent, mockReturnList);

        List<AppInfo> appList = subject.getAppList();
        assertThat(appList.size()).isEqualTo(1);
        assertThat(appList.get(0).getAppName()).isEqualTo("app_name");
        assertThat(appList.get(0).getPakageName()).isEqualTo("package");
    }
}