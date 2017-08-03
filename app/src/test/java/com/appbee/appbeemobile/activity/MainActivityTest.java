package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.AppStatServiceCallback;
import com.appbee.appbeemobile.util.AppBeeConstants;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private ActivityController<MainActivity> activityController;

    @Inject
    AppStatService appStatService;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
         activityController = Robolectric.buildActivity(MainActivity.class);
    }

    @Test
    public void onCreate앱시작시_앱목록데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatService).sendAppList(any(AppStatServiceCallback.class));
    }

    @Test
    public void onCreate앱시작시_연간일별통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatService).sendLongTermStats(any(AppStatServiceCallback.class));
    }

    @Test
    public void onCreate앱시작시_단기통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatService).sendEventStats(any(AppStatServiceCallback.class));
    }

    @Test
    public void onCreate앱시작시_가공된_단기통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatService).sendShortTermStats(any(AppStatServiceCallback.class));
    }

    @Test
    public void 앱테이터를_전송하였지만토큰만료로실패한경우_LoginActivity로_이동한다() throws Exception {
        doAnswer((invocation) -> {
            ((AppStatServiceCallback) invocation.getArguments()[0]).onFail(AppBeeConstants.API_RESPONSE_CODE.UNAUTHORIZED);
            return null;
        }).when(appStatService).sendAppList(any(AppStatServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendAppList(any(AppStatServiceCallback.class));

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
    }

    @Test
    public void 단기통계테이터를_전송하였지만토큰만료로실패한경우_LoginActivity로_이동한다() throws Exception {
        doAnswer((invocation) -> {
            ((AppStatServiceCallback) invocation.getArguments()[0]).onFail(AppBeeConstants.API_RESPONSE_CODE.UNAUTHORIZED);
            return null;
        }).when(appStatService).sendShortTermStats(any(AppStatServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendShortTermStats(any(AppStatServiceCallback.class));

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
    }

    @Test
    public void 장기통계테이터를_전송하였지만토큰만료로실패한경우_LoginActivity로_이동한다() throws Exception {
        doAnswer((invocation) -> {
            ((AppStatServiceCallback) invocation.getArguments()[0]).onFail(AppBeeConstants.API_RESPONSE_CODE.UNAUTHORIZED);
            return null;
        }).when(appStatService).sendLongTermStats(any(AppStatServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendLongTermStats(any(AppStatServiceCallback.class));

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
    }

    @Test
    public void 가공전단기통계테이터를_전송하였지만토큰만료로실패한경우_LoginActivity로_이동한다() throws Exception {
        doAnswer((invocation) -> {
            ((AppStatServiceCallback) invocation.getArguments()[0]).onFail(AppBeeConstants.API_RESPONSE_CODE.UNAUTHORIZED);
            return null;
        }).when(appStatService).sendEventStats(any(AppStatServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendEventStats(any(AppStatServiceCallback.class));

        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
    }
}