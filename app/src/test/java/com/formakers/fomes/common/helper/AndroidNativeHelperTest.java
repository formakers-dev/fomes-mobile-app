package com.formakers.fomes.common.helper;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.formakers.fomes.common.model.NativeAppInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

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

@RunWith(RobolectricTestRunner.class)
public class AndroidNativeHelperTest {
    private AndroidNativeHelper subject;

    private UsageStatsManager mockUsageStatsManager = mock(UsageStatsManager.class);
    private AppOpsManager mockAppOpsManager = mock(AppOpsManager.class);
    private PackageManager mockPackageManager = mock(PackageManager.class);

    private Context mockContext = mock(Context.class);

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

        when(mockContext.getPackageName()).thenReturn("com.formakers.fomes");
        when(mockContext.getSystemService(Context.USAGE_STATS_SERVICE)).thenReturn(mockUsageStatsManager);
        when(mockContext.getSystemService(Context.APP_OPS_SERVICE)).thenReturn(mockAppOpsManager);
        when(mockContext.getPackageManager()).thenReturn(mockPackageManager);

        subject = new AndroidNativeHelper(mockContext);
    }

    @Test
    public void getUsageStatEvents?????????_???????????????_?????????_??????????????????_????????????() throws Exception {
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
    public void getInstalledLaunchableApps?????????_?????????_???????????????_????????????() throws Exception {
        List<ResolveInfo> mockReturnList = new ArrayList<>();
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.isDefault = true;
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName = "package";
        resolveInfo.nonLocalizedLabel = "app_name";
        mockReturnList.add(resolveInfo);

        when(mockPackageManager.queryIntentActivities(any(Intent.class), eq(PackageManager.GET_META_DATA)))
                .thenReturn(mockReturnList);

        List<NativeAppInfo> appList = subject.getInstalledLaunchableApps().toList().toBlocking().single();
        System.out.println(appList);
        assertThat(appList.size()).isEqualTo(1);
        assertThat(appList.get(0).getAppName()).isEqualTo("app_name");
        assertThat(appList.get(0).getPackageName()).isEqualTo("package");
    }

    @Test
    public void getUsageStats?????????_??????????????????_????????????() throws Exception {
        subject.getUsageStats(UsageStatsManager.INTERVAL_MONTHLY, 1000L, 1100L);

        verify(mockUsageStatsManager).queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, 1000L, 1100L);
    }

    @Test
    public void hasUsageStatsPermission?????????_??????????????????_?????????_??????_??????_false???_????????????() throws Exception {
        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_IGNORED);
        assertThat(subject.hasUsageStatsPermission()).isFalse();

        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_DEFAULT);
        assertThat(subject.hasUsageStatsPermission()).isFalse();

        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_ERRORED);
        assertThat(subject.hasUsageStatsPermission()).isFalse();
    }

    @Test
    public void hasUsageStatsPermission?????????_??????????????????_?????????_??????_??????_true???_????????????() throws Exception {
        when(mockAppOpsManager.checkOpNoThrow(eq(AppOpsManager.OPSTR_GET_USAGE_STATS), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_ALLOWED);

        assertThat(subject.hasUsageStatsPermission()).isTrue();
    }

    @Test
    public void getNativeAppInfo?????????_?????????????????????_PackageName?????????_????????????_????????????() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "com.package.name1";
        packageInfo.versionName = "1.0";
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = "com.package.name1";
        packageInfo.applicationInfo.name = "?????????1";
        Drawable mockDrawable = mock(Drawable.class);

        when(mockPackageManager.getApplicationInfo("com.package.name1", 0))
                .thenReturn(packageInfo.applicationInfo);
        when(mockPackageManager.getApplicationLabel(packageInfo.applicationInfo)).thenReturn(packageInfo.applicationInfo.name);
        when(mockPackageManager.getApplicationIcon(packageInfo.applicationInfo.packageName)).thenReturn(mockDrawable);

        NativeAppInfo nativeAppInfo = subject.getNativeAppInfo("com.package.name1");

        assertThat(nativeAppInfo.getPackageName()).isEqualTo("com.package.name1");
        assertThat(nativeAppInfo.getAppName()).isEqualTo("?????????1");
        assertThat(nativeAppInfo.getIcon()).isEqualTo(mockDrawable);
    }

    @Test
    public void getLaunchableIntent_?????????__???????????????_???????????????_null???_????????????() {
        assertThat(subject.getLaunchableIntent(null)).isNull();
        assertThat(subject.getLaunchableIntent("")).isNull();
    }

    @Test
    public void getLaunchableIntent_?????????__??????_????????????_?????????????????????_?????????_????????????() {
        Intent expectedIntent = new Intent();
        when(mockPackageManager.getLaunchIntentForPackage("com.package.name")).thenReturn(expectedIntent);

        Intent acutalIntent = subject.getLaunchableIntent("com.package.name");

        verify(mockPackageManager).getLaunchIntentForPackage("com.package.name");
        assertThat(acutalIntent).isEqualTo(expectedIntent);
    }
}