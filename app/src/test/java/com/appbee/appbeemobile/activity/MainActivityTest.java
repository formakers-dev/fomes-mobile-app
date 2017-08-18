package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.provider.Settings;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest extends ActivityTest {

    private ActivityController<MainActivity> activityController;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(MainActivity.class);

        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
    }

    @Test
    public void onCreate호출시_Stat접근권한이_없는_경우_권한요청_Activity를_호출한다() throws Exception {
        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        MainActivity subject = activityController.create().get();

        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(subject).getNextStartedActivityForResult();
        assertThat(nextStartedActivityForResult.intent.getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        assertThat(nextStartedActivityForResult.requestCode).isEqualTo(1001);
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_권한요청_Activity를_호출하지_않는다() throws Exception {
        MainActivity subject = activityController.create().get();
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent()).isNotNull();
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_분석결과화면으로_이동한다() throws Exception {
        MainActivity subject = activityController.create().get();

        assertLaunchAnalysisResultActivity(subject);
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여된_경우_분석결과화면으로_이동한다() throws Exception {
        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = activityController.create().get();
        shadowOf(subject).getNextStartedActivity();

        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);

        assertLaunchAnalysisResultActivity(subject);
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여되지_않은_경우_앱을_종료한다() throws Exception {
        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = activityController.create().get();

        subject.onActivityResult(1001, 0, null);

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    private void assertLaunchAnalysisResultActivity(MainActivity subject) {
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(AnalysisResultActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}