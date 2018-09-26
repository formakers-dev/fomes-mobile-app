package com.formakers.fomes.provisioning.view;

import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.common.view.BaseActivityTest;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginActivityTest extends BaseActivityTest<LoginActivity> {

    @Mock LoginPresenter mockPresenter;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void launchActivity() {
        subject = getActivity();
        subject.setPresenter(mockPresenter);
    }

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        MockitoAnnotations.initMocks(this);
        super.setUp();
    }

    @Test
    public void LoginActivity_시작시_로그인화면이_나타난다() {
        launchActivity();

        assertThat(subject.findViewById(R.id.login_logo).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_title).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_subtitle).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_google_button).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_tnc).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 약관링크_클릭시_해당_링크를_브라우저로_띄운다() {
        launchActivity();

        assertThat(((TextView) subject.findViewById(R.id.login_tnc)).getMovementMethod().getClass().getSimpleName())
                .isEqualTo(LinkMovementMethod.class.getSimpleName());
    }

    @Test
    public void 로그인버튼_클릭시_Google_인증_로직을_실행한다() {
        launchActivity();

        Intent intent = new Intent(RuntimeEnvironment.application, SignInHubActivity.class);
        intent.setAction("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        when(mockPresenter.getGoogleSignInIntent()).thenReturn(intent);

        subject.findViewById(R.id.login_google_button).performClick();
        
        verify(mockPresenter).getGoogleSignInIntent();
        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(subject).getNextStartedActivityForResult();
        System.out.println(nextStartedActivityForResult);
        assertThat(nextStartedActivityForResult.intent.getAction()).contains("GOOGLE_SIGN_IN");
        assertThat(nextStartedActivityForResult.intent.getComponent().getClassName()).isEqualTo(SignInHubActivity.class.getName());
    }

    @Test
    public void Google_인증_성공시_Fomes에_가입을_요청한다() {
        launchActivity();

        when(mockPresenter.requestSignUpBy(any())).thenReturn(true);

        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        verify(mockPresenter).requestSignUpBy(any(Intent.class));
    }

    @Test
    public void Google_인증_실패시_실패문구를_띄운다() {
        launchActivity();

        subject.onActivityResult(9001, Activity.RESULT_CANCELED, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("구글 로그인이 취소되었습니다.");
    }

    @Test
    public void Google_인증_성공후_Fomes에_가입_요청이_실패한_경우_실패문구를_띄운다() {
        launchActivity();

        when(mockPresenter.requestSignUpBy(any())).thenReturn(false);

        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("구글 로그인에 실패하였습니다.");
    }
}