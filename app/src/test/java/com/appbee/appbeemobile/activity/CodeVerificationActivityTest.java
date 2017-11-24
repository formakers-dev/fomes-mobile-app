package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
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

    private CodeVerificationActivity subject;


    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) (RuntimeEnvironment.application)).getComponent().inject(this);

        subject = Robolectric.setupActivity(CodeVerificationActivity.class);

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
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
        when(mockUserService.verifyRegistrationCode(anyString())).thenReturn(Completable.complete());
        subject.codeVerificationEdittext.setText("앱비코드123");

        subject.codeVerificationButton.performClick();

        verify(mockUserService).verifyRegistrationCode(eq("앱비코드123"));
    }

    @Test
    public void 코드인증에_성공시_로그인화면으로_이동한다() throws Exception {
        when(mockUserService.verifyRegistrationCode(anyString())).thenReturn(Completable.complete());

        subject.codeVerificationButton.performClick();

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(LoginActivity.class.getName());
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    @Config(minSdk = 22)
    public void 코드인증에_실패시_안내팝업이_나타난다() throws Exception {
        when(mockUserService.verifyRegistrationCode(anyString())).thenReturn(Completable.error(new HttpException(Response.error(401, ResponseBody.create(null, "")))));

        subject.codeVerificationButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        assertThat(shadowAlertDialog.getTitle()).isEqualTo("초대장코드오류");
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("초대 코드를 다시 확인해주세요.");

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertThat(shadowAlertDialog.hasBeenDismissed()).isTrue();
    }
}