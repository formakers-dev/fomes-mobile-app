package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.AppStatServiceCallback;
import com.appbee.appbeemobile.util.AppBeeConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest extends ActivityTest {

    private ActivityController<MainActivity> activityController;

    @Inject
    AppStatService appStatService;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(MainActivity.class);

        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_앱목록데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatService).sendAppList(any(AppStatServiceCallback.class));
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_연간일별통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatService).sendLongTermStats(any(AppStatServiceCallback.class));
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_가공된_단기통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatService).sendShortTermStats(any(AppStatServiceCallback.class));
    }

    @Test
    public void 앱테이터를_전송하였지만토큰만료로실패한경우_앱을재시작한다() throws Exception {
        doAnswer(setApiResultToUnauthorized()).when(appStatService).sendAppList(any(AppStatServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendAppList(any(AppStatServiceCallback.class));

        assertRestartApp(subject);
    }

    @Test
    public void 단기통계테이터를_전송하였지만토큰만료로실패한경우_앱을재시작한다() throws Exception {
        doAnswer(setApiResultToUnauthorized()).when(appStatService).sendShortTermStats(any(AppStatServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendShortTermStats(any(AppStatServiceCallback.class));

        assertRestartApp(subject);
    }

    @Test
    public void 장기통계테이터를_전송하였지만토큰만료로실패한경우_앱을재시작한다() throws Exception {
        doAnswer(setApiResultToUnauthorized()).when(appStatService).sendLongTermStats(any(AppStatServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendLongTermStats(any(AppStatServiceCallback.class));

        assertRestartApp(subject);
    }

    @NonNull
    private Answer setApiResultToUnauthorized() {
        return (invocation) -> {
            ((AppStatServiceCallback) invocation.getArguments()[0]).onFail(AppBeeConstants.API_RESPONSE_CODE.UNAUTHORIZED);
            return null;
        };
    }

    private void assertRestartApp(MainActivity subject) {
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}