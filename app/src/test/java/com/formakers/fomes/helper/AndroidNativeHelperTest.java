package com.formakers.fomes.helper;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.model.NativeAppInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowPackageManager;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class AndroidNativeHelperTest {
    private AndroidNativeHelper subject;

    private UsageStatsManager mockUsageStatsManager;
    private AppOpsManager mockAppOpsManager;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());
//        RxJavaHooks.setOnComputationScheduler(scheduler -> testScheduler);

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        mockUsageStatsManager = mock(UsageStatsManager.class);
        mockAppOpsManager = mock(AppOpsManager.class);

        shadowOf((Application) ApplicationProvider.getApplicationContext()).setSystemService(Context.USAGE_STATS_SERVICE, mockUsageStatsManager);
        shadowOf((Application) ApplicationProvider.getApplicationContext()).setSystemService(Context.APP_OPS_SERVICE, mockAppOpsManager);

        subject = new AndroidNativeHelper(ApplicationProvider.getApplicationContext().getApplicationContext());
    }

    @Test
    public void getUsageStatEvents호출시_디바이스에_기록된_앱사용정보를_리턴한다() throws Exception {
        UsageEvents usageEvents = mock(UsageEvents.class);

        when(usageEvents.hasNextEvent()).thenAnswer(new Answer<Boolean>() {
            private int callCount = 0;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                if (callCount < 2) {
                    callCount++;
                    return true;
                } else {
                    return false;
                }
            }
        });
        when(mockUsageStatsManager.queryEvents(anyLong(), anyLong())).thenReturn(usageEvents);

        subject.getUsageStatEvents(0L, 1L).subscribe(new TestSubscriber<>());

        verify(usageEvents, times(2)).getNextEvent(any());
    }

    @Test
    public void getInstalledLaunchableApps호출시_설치된_앱리스트를_리턴한다() throws Exception {
        List<ResolveInfo> mockReturnList = new ArrayList<>();
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.isDefault = true;
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName = "package";
//        shadowOf(resolveInfo).setLabel("app_name");
        mockReturnList.add(resolveInfo);

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ShadowPackageManager shadowPackageManager = shadowOf(ApplicationProvider.getApplicationContext().getPackageManager());
        shadowPackageManager.addResolveInfoForIntent(intent, resolveInfo);

        List<NativeAppInfo> appList = subject.getInstalledLaunchableApps().toList().toBlocking().single();
        System.out.println(appList);
        assertThat(appList.size()).isEqualTo(2);
//        assertThat(appList.get(0).getAppName()).isEqualTo("app_name");
        assertThat(appList.get(0).getPackageName()).isEqualTo("package");
    }

    @Test
    public void getUsageStats호출시_앱사용정보를_조회한다() throws Exception {
        subject.getUsageStats(UsageStatsManager.INTERVAL_MONTHLY, 1000L, 1100L);

        verify(mockUsageStatsManager).queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, 1000L, 1100L);
    }

    @Test
    public void hasUsageStatsPermission호출시_사용통계조회_권한이_없는_경우_false를_리턴한다() throws Exception {
        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_IGNORED);
        assertThat(subject.hasUsageStatsPermission()).isFalse();

        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_DEFAULT);
        assertThat(subject.hasUsageStatsPermission()).isFalse();

        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_ERRORED);
        assertThat(subject.hasUsageStatsPermission()).isFalse();
    }

    @Test
    public void hasUsageStatsPermission호출시_사용통계조회_권한이_있는_경우_true를_리턴한다() throws Exception {
        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_ALLOWED);

        assertThat(subject.hasUsageStatsPermission()).isTrue();
    }

    @Test
    public void getNativeAppInfo호출시_파라미터로받은_PackageName에대한_앱정보를_리턴한다() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "com.package.name1";
        packageInfo.versionName = "1.0";
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = "com.package.name1";
        packageInfo.applicationInfo.name = "앱이름1";
        Drawable mockDrawable = mock(Drawable.class);
        shadowOf(ApplicationProvider.getApplicationContext().getPackageManager()).setApplicationIcon("com.package.name1", mockDrawable);

        shadowOf(ApplicationProvider.getApplicationContext().getPackageManager()).addPackage(packageInfo);

        NativeAppInfo nativeAppInfo = subject.getNativeAppInfo("com.package.name1");

        assertThat(nativeAppInfo.getPackageName()).isEqualTo("com.package.name1");
        assertThat(nativeAppInfo.getAppName()).isEqualTo("앱이름1");
        assertThat(nativeAppInfo.getIcon()).isEqualTo(mockDrawable);
    }
}