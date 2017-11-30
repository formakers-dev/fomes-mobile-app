package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.ConfigService;
import com.appbee.appbeemobile.service.PowerConnectedService;
import com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PermissionGuideActivityTest extends ActivityTest {

    @Inject
    AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    @Inject
    ConfigService mockConfigService;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;
    private ActivityController<PermissionGuideActivity> activityController;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockLocalStorageHelper.getInvitationCode()).thenReturn("CODE");
        when(mockLocalStorageHelper.getEmail()).thenReturn("test@test.com");
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        when(mockConfigService.getAppVersion()).thenReturn(1L);
        activityController = Robolectric.buildActivity(PermissionGuideActivity.class);
    }

    @Test
    public void onCreate호출시_최소앱버전코드확인API를_호출한다() throws Exception {
        activityController.create().get();

        verify(mockConfigService).getAppVersion();
    }

    @Test
    public void 최소앱버전코드확인API호출결과_최소앱버전보다_현재버전코드가_작은경우_업데이트_안내_팝업이_나타난다() throws Exception {
        when(mockConfigService.getAppVersion()).thenReturn(2L);

        PermissionGuideActivity subject = activityController.create().get();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("신규 버전 업데이트");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("AppBee의 신규 버전이 업데이트 되었습니다.\n마켓에서 업데이트 진행 후 이용 부탁드립니다.");
    }

    @Test
    public void 업데이트_안내_팝업의_확인버튼_선택시_마켓으로_이동후_앱을_종료한다() throws Exception {
        when(mockConfigService.getAppVersion()).thenReturn(2L);

        PermissionGuideActivity subject = activityController.create().get();

        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        Intent nextIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextIntent.getAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(nextIntent.getDataString()).isEqualTo("market://details?id=com.appbee.appbeemobile");
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 업데이트_안내_팝업을_취소하면_앱을_종료한다() throws Exception {
        when(mockConfigService.getAppVersion()).thenReturn(2L);

        PermissionGuideActivity subject = activityController.create().get();

        ShadowAlertDialog.getLatestAlertDialog().cancel();

        assertThat(shadowOf(subject).getNextStartedActivity()).isNull();
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void onCreate호출시_초대장인증미완료_및_SignIn미완료_시_초대장코드인증화면으로_이동한다() throws Exception {
        when(mockLocalStorageHelper.getInvitationCode()).thenReturn("");
        when(mockLocalStorageHelper.getEmail()).thenReturn("");
        PermissionGuideActivity subject = activityController.create().postCreate(null).get();

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(CodeVerificationActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void onCreate호출시_초대장인증완료_및_SignIn미완료_시_로그인화면으로_이동한다() throws Exception {
        when(mockLocalStorageHelper.getEmail()).thenReturn("");
        PermissionGuideActivity subject = activityController.create().postCreate(null).get();

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(LoginActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void onCreate호출시_권한이있는경우_PowerConnectedService를_시작하고_MainActivity로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = activityController.create().postCreate(null).get();

        assertThat(shadowOf(subject).getNextStartedService().getComponent().getClassName()).isEqualTo(PowerConnectedService.class.getName());
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(MainActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void FCM_Noti를_통해_Activity가_실행되고_권한이있는경우_onCreate호출시_인터뷰상세화면으로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        Intent intent = new Intent();
        intent.putExtra(EXTRA.PROJECT_ID, "testProjectId");
        intent.putExtra(EXTRA.INTERVIEW_SEQ, "1");

        PermissionGuideActivity subject = Robolectric.buildActivity(PermissionGuideActivity.class, intent).create().postCreate(null).get();

        Intent nextStartedActivityIntent = shadowOf(subject).getNextStartedActivity();

        assertThat(nextStartedActivityIntent.getComponent().getClassName()).isEqualTo(InterviewDetailActivity.class.getName());
        assertThat(subject.isFinishing()).isTrue();
        assertThat(nextStartedActivityIntent.getStringExtra(EXTRA.PROJECT_ID)).isEqualTo("testProjectId");
        assertThat(nextStartedActivityIntent.getLongExtra(EXTRA.INTERVIEW_SEQ, 0L)).isEqualTo(1L);
    }

    @Test
    public void permissionButton클릭시_권한설정페이지를_표시한다() throws Exception {

        PermissionGuideActivity subject = activityController.create().postCreate(null).get();

        subject.permissionButton.performClick();

        assertThat(shadowOf(subject).getNextStartedActivity().getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        assertThat(shadowOf(subject).isFinishing()).isFalse();
    }

    @Test
    public void 권한설정이_완료되고_돌아와서_권한이있으면_PowerConnectedService를_시작하고_LoadingActivity로_이동한다() throws Exception {
        PermissionGuideActivity subject = activityController.create().postCreate(null).get();

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);

        assertThat(shadowOf(subject).getNextStartedService().getComponent().getClassName()).isEqualTo(PowerConnectedService.class.getName());
        assertThat(shadowOf(subject).getNextStartedActivityForResult().intent.getComponent().getClassName()).isEqualTo(LoadingActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void onCreate에서_권한설정이_완료되지않은경우_PowerConnectedService를_시작하지않고_현재_Activity에_머무른다() throws Exception {
        PermissionGuideActivity subject = activityController.create().postCreate(null).get();

        ShadowActivity shadowSubject = shadowOf(subject);
        assertThat(shadowSubject.getNextStartedService()).isNull();
        assertThat(shadowSubject.getNextStartedActivity()).isNull();
        assertThat(shadowSubject.isFinishing()).isFalse();
    }
}