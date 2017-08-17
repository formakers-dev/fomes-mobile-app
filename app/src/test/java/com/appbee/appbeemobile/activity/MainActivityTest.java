package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.ServiceCallback;
import com.appbee.appbeemobile.network.UserService;
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
    UserService userService;

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

        assertThat(shadowOf(subject).getNextStartedActivityForResult()).isNull();
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_앱목록_및_통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        assertSendAppListAndStatData();
    }

    @Test
    public void 앱테이터를_전송하였지만토큰만료로실패한경우_앱을재시작한다() throws Exception {
        doAnswer(setApiResultToUnauthorized()).when(userService).sendAppList(any(ServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(userService).sendAppList(any(ServiceCallback.class));
        assertRestartApp(subject);
    }

    @Test
    public void 단기통계테이터를_전송하였지만토큰만료로실패한경우_앱을재시작한다() throws Exception {
        doAnswer(setApiResultToUnauthorized()).when(appStatService).sendShortTermStats(any(ServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendShortTermStats(any(ServiceCallback.class));
        assertRestartApp(subject);
    }

    @Test
    public void 장기통계테이터를_전송하였지만토큰만료로실패한경우_앱을재시작한다() throws Exception {
        doAnswer(setApiResultToUnauthorized()).when(appStatService).sendLongTermStats(any(ServiceCallback.class));

        MainActivity subject = activityController.create().get();

        verify(appStatService).sendLongTermStats(any(ServiceCallback.class));
        assertRestartApp(subject);
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여된_경우_데이터를_전송한다() throws Exception {
        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = activityController.create().get();

        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);

        assertSendAppListAndStatData();
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여되지_않은_경우_앱을_종료한다() throws Exception {
        when(appBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = activityController.create().get();

        subject.onActivityResult(1001, 0, null);

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @NonNull
    private Answer setApiResultToUnauthorized() {
        return (invocation) -> {
            ((ServiceCallback) invocation.getArguments()[0]).onFail(AppBeeConstants.API_RESPONSE_CODE.UNAUTHORIZED);
            return null;
        };
    }

    private void assertSendAppListAndStatData() {
        verify(userService).sendAppList(any(ServiceCallback.class));
        verify(appStatService).sendLongTermStats(any(ServiceCallback.class));
        verify(appStatService).sendShortTermStats(any(ServiceCallback.class));
    }

    private void assertRestartApp(MainActivity subject) {
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}