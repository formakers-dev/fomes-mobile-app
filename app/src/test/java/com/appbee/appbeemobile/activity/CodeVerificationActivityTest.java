package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import rx.Completable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CodeVerificationActivityTest {

    @Inject
    UserService mockUserService;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    private CodeVerificationActivity subject;


    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        ((TestAppBeeApplication) (RuntimeEnvironment.application)).getComponent().inject(this);

        subject = Robolectric.setupActivity(CodeVerificationActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onCreate호출시_초대장입력화면이_나타난다() throws Exception {
        assertThat(subject.codeVerificationEdittext.isShown()).isTrue();
        assertThat(subject.codeVerificationButton.isShown()).isTrue();
    }

    @Test
    public void 코드를입력하고_codeVerificationButton을_클릭하면_입력한코드로_코드확인API를_호출한다() throws Exception {
        when(mockUserService.verifyInvitationCode(anyString())).thenReturn(Completable.complete());
        subject.codeVerificationEdittext.setText("앱비코드123");

        subject.codeVerificationButton.performClick();

        verify(mockUserService).verifyInvitationCode(eq("앱비코드123"));
    }

    @Test
    public void 코드인증에_성공시_초대장코드를저장하고_로그인화면으로_이동한다() throws Exception {
        when(mockUserService.verifyInvitationCode(anyString())).thenReturn(Completable.complete());
        subject.codeVerificationEdittext.setText("VALID_CODE");

        subject.codeVerificationButton.performClick();

        verify(mockLocalStorageHelper).setInvitationCode(eq("VALID_CODE"));
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(LoginActivity.class.getName());
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    @Config(minSdk = 22)
    public void 코드인증에_실패시_안내팝업이_나타난다() throws Exception {
        when(mockUserService.verifyInvitationCode(anyString())).thenReturn(Completable.error(new HttpException(Response.error(401, ResponseBody.create(null, "")))));

        subject.codeVerificationButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("코드를 확인해주세요.");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("유효하지 않은 초대장 코드 입니다.");

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertThat(shadowAlertDialog.hasBeenDismissed()).isTrue();
    }
}