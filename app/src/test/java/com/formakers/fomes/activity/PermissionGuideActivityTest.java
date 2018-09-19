package com.formakers.fomes.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.util.FomesConstants.EXTRA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;
import rx.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class PermissionGuideActivityTest extends BaseActivityTest<PermissionGuideActivity> {

    @Inject
    AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    @Inject
    ConfigService mockConfigService;

    @Inject
    UserService mockUserService;

    @Inject
    SharedPreferencesHelper mockSharedPreferencesHelper;

    public PermissionGuideActivityTest() {
        super(PermissionGuideActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockSharedPreferencesHelper.getInvitationCode()).thenReturn("CODE");
        when(mockSharedPreferencesHelper.isLoggedIn()).thenReturn(true);
        when(mockSharedPreferencesHelper.getLastUpdateAppUsageTimestamp()).thenReturn(99999L);
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        when(mockConfigService.getAppVersion()).thenReturn(Single.just(1L));
        when(mockUserService.verifyToken()).thenReturn(Completable.complete());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void onCreate호출시_최소앱버전코드확인API를_호출한다() throws Exception {
        getActivity(LIFECYCLE_TYPE_CREATE);

        verify(mockConfigService).getAppVersion();
    }

    @Test
    public void 최소앱버전코드확인API호출결과_최소앱버전보다_현재버전코드가_작은경우_업데이트_안내_팝업이_나타난다() throws Exception {
        when(mockConfigService.getAppVersion()).thenReturn(Single.just(Long.MAX_VALUE));

        getActivity(LIFECYCLE_TYPE_CREATE);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("신규 버전 업데이트");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("AppBee의 신규 버전이 업데이트 되었습니다.\n마켓에서 업데이트 진행 후 이용 부탁드립니다.");
    }

    @Test
    public void 업데이트_안내_팝업의_확인버튼_선택시_마켓으로_이동후_앱을_종료한다() throws Exception {
        when(mockConfigService.getAppVersion()).thenReturn(Single.just(Long.MAX_VALUE));

        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_CREATE);

        ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        Intent nextIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextIntent.getAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(nextIntent.getDataString()).isEqualTo("market://details?id=com.formakers.fomes");
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 업데이트_안내_팝업을_취소하면_앱을_종료한다() throws Exception {
        when(mockConfigService.getAppVersion()).thenReturn(Single.just(Long.MAX_VALUE));

        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_CREATE);;

        ShadowAlertDialog.getLatestAlertDialog().cancel();

        assertThat(shadowOf(subject).getNextStartedActivity()).isNull();
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void onCreate호출시_초대장인증미완료시_초대장코드인증화면으로_이동한다() throws Exception {
        when(mockSharedPreferencesHelper.getInvitationCode()).thenReturn("");
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verifyMoveToActivity(subject, CodeVerificationActivity.class);
    }

    @Test
    public void onCreate호출시_초대장인증완료_및_SignIn미완료_시_로그인화면으로_이동한다() throws Exception {
        when(mockSharedPreferencesHelper.isLoggedIn()).thenReturn(false);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verifyMoveToActivity(subject, LoginActivity.class);
    }

    @Test
    public void onCreate호출시_초대장인증완료_및_SignIn완료되었지만_권한이_없으면_토큰검증을_수행하지_않는다() throws Exception {
        getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verify(mockUserService, never()).verifyToken();
    }

    @Test
    public void onCreate호출시_토큰이_만료된_경우_LoginActivity로_이동한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(createHttpErrorForCompletable(401));
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verifyMoveToActivity(subject, LoginActivity.class);
    }

    @Test
    public void onCreate호출시_토큰이_유효하지_않은_경우_LoginActivity로_이동한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(createHttpErrorForCompletable(403));
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verifyMoveToActivity(subject, LoginActivity.class);
    }

    @Test
    public void onCreate호출시_토큰유효성검증요청의_서버응답오류가_발생한_경우_에러메시지표시후_종료한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(createHttpErrorForCompletable(500));
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("예상치 못한 에러가 발생하였습니다.");
        assertThat(subject.isFinishing()).isTrue();
    }

    @NonNull
    private Completable createHttpErrorForCompletable(int httpErrorCode) {
        return Completable.error(new HttpException(Response.error(httpErrorCode, ResponseBody.create(null, ""))));
    }

    @Test
    public void onCreate호출시_토큰검증시_예기치못한_에러가_발생할_경우_에러메시지표시후_종료한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(Completable.error(new Exception()));
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("예상치 못한 에러가 발생하였습니다.");
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void onCreate호출시_권한이_있고_토큰유효성이_검증된_경우_MainActivity로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verifyMoveToActivity(subject, MainActivity.class);
    }

    @Test
    public void onCreate호출시_권한이_있고_토큰유효성이_검증됐으나_앱사용정보전송이력이_없는경우_LoadingActivity로_이동한다() throws Exception {
        when(mockSharedPreferencesHelper.getLastUpdateAppUsageTimestamp()).thenReturn(0L);
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verifyMoveToActivity(subject, LoadingActivity.class);
    }

    @Test
    public void FCM_모집Noti를_통해_Activity가_실행되고_권한이있는경우_onCreate호출시_메인화면으로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = setUpActivityWithNotification("모집");

        verifyMoveToActivity(subject, MainActivity.class);
    }

    @Test
    public void FCM_확정Noti를_통해_Activity가_실행되고_권한이있는경우_onCreate호출시_다가오는_인터뷰화면으로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = setUpActivityWithNotification("확정");

        verifyMoveToActivity(subject, MyInterviewActivity.class);
    }

    private PermissionGuideActivity setUpActivityWithNotification(String notificationType) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA.PROJECT_ID, "testProjectId");
        intent.putExtra(EXTRA.INTERVIEW_SEQ, "1");
        intent.putExtra(EXTRA.NOTIFICATION_TYPE, notificationType);

        return getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);
    }

    @Test
    public void permissionButton클릭시_권한설정페이지를_표시한다() throws Exception {

        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        subject.permissionButton.performClick();

        assertThat(shadowOf(subject).getNextStartedActivity().getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        assertThat(shadowOf(subject).isFinishing()).isFalse();
    }

    @Test
    public void 권한설정_수행후_권한이_없을때_현재화면에_머무른다() throws Exception {
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        subject.onActivityResult(1001, 0, null);

        verifyStayingCurrentActivity(subject);
    }

    @Test
    public void 권한설정_수행후_앱사용데이터_전송기록이_없는경우_LoadingActivity로_이동한다() throws Exception {
        when(mockSharedPreferencesHelper.getLastUpdateAppUsageTimestamp()).thenReturn(0L);
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);

        verifyMoveToActivity(subject, LoadingActivity.class);
    }

    @Test
    public void 권한설정_수행후_확정노티에서_진입한경우_MyInterviewActivity로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = setUpActivityWithNotification("확정");

        subject.onActivityResult(1001, 0, null);

        verifyMoveToActivity(subject, MyInterviewActivity.class);
    }

    @Test
    public void 권한설정_수행후_일반노티에서_진입한경우_MainActivity로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        PermissionGuideActivity subject = setUpActivityWithNotification("모집");

        subject.onActivityResult(1001, 0, null);

        verifyMoveToActivity(subject, MainActivity.class);
    }

    @Test
    public void 권한설정_수행후_토큰이_만료된_경우_LoginActivity로_이동한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(createHttpErrorForCompletable(401));
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        subject.onActivityResult(1001, 0, null);

        verifyMoveToActivity(subject, LoginActivity.class);
    }

    @Test
    public void 권한설정_수행후_토큰이_유효하지_않은_경우_LoginActivity로_이동한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(createHttpErrorForCompletable(403));
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        subject.onActivityResult(1001, 0, null);

        verifyMoveToActivity(subject, LoginActivity.class);
    }

    @Test
    public void 권한설정_수행후_토큰유효성검증요청의_서버응답오류가_발생한_경우_에러메시지표시후_종료한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(createHttpErrorForCompletable(500));
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        subject.onActivityResult(1001, 0, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("예상치 못한 에러가 발생하였습니다.");
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 권한설정_수행후_토큰검증시_예기치못한_에러가_발생할_경우_에러메시지표시후_종료한다() throws Exception {
        when(mockUserService.verifyToken()).thenReturn(Completable.error(new Exception()));
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        subject.onActivityResult(1001, 0, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("예상치 못한 에러가 발생하였습니다.");
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void onCreate에서_권한설정이_완료되지않은경우_현재화면에_머무른다() throws Exception {
        PermissionGuideActivity subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verifyStayingCurrentActivity(subject);
    }

    private void verifyStayingCurrentActivity(PermissionGuideActivity subject) {
        ShadowActivity shadowSubject = shadowOf(subject);
        assertThat(shadowSubject.getNextStartedActivity()).isNull();
        assertThat(shadowSubject.isFinishing()).isFalse();
    }
}