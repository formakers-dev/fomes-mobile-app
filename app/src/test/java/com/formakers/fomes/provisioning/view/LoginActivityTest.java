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
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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

import rx.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
        subject = getActivityController().get();
//        subject = getActivity(LIFECYCLE_TYPE_CREATE);
        subject.setPresenter(mockPresenter);
        getActivityController().create().start().postCreate(null).resume();
    }

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        MockitoAnnotations.initMocks(this);
        super.setUp();

        when(mockPresenter.googleSilentSignIn()).thenReturn(Single.error(new Throwable()));
    }

    @Test
    public void LoginActivity_시작시_로그인화면이_나타난다() {
        launchActivity();

        assertThat(subject.findViewById(R.id.login_beta_label).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_logo).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_game_logo).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_subtitle).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_google_button).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_tnc).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void LoginActivity_시작시__사일런트사인인을_시도한다() {
        launchActivity();

        verify(mockPresenter).googleSilentSignIn();
    }

    @Test
    public void LoginActivity_시작시__사일런트사인인을_성공하면__로그인버튼이_사라진다() {
        when(mockPresenter.googleSilentSignIn()).thenReturn(Single.just(mock(GoogleSignInResult.class)));
        when(mockPresenter.requestSignUpBy(any())).thenReturn(Single.just("fomesAccessToken"));

        launchActivity();

        assertThat(subject.findViewById(R.id.login_google_button).getVisibility()).isEqualTo(View.GONE);
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
    public void Google_인증_실패시_실패문구를_띄운다() {
        launchActivity();

        subject.onActivityResult(9001, Activity.RESULT_CANCELED, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("구글 로그인이 취소되었습니다.");
    }

    @Test
    public void Google_인증_성공후_구글정보변환이_실패한경우__실패문구를_띄운다() {
        when(mockPresenter.convertGoogleSignInResult(any())).thenReturn(null);

        launchActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("구글 로그인에 실패하였습니다.");
    }

    @Test
    public void Google_인증_성공후_구글정보변환이_성공한경우__Fomes에_가입을_요청한다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockPresenter.convertGoogleSignInResult(any())).thenReturn(mockGoogleSignInResult);
        when(mockPresenter.requestSignUpBy(any())).thenReturn(Single.just("fomesAccessToken"));

        launchActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        verify(mockPresenter).requestSignUpBy(eq(mockGoogleSignInResult));
    }

    @Test
    public void Google_인증_성공후_구글정보변환이_성공하고_Fomes_가입요청이_성공하고_프로비저닝_상태이면__프로비저닝_플로우로_이동한다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockPresenter.convertGoogleSignInResult(any(Intent.class))).thenReturn(mockGoogleSignInResult);
        when(mockPresenter.requestSignUpBy(any())).thenReturn(Single.just("fomesAccessToken"));
        when(mockPresenter.isProvisioningProgress()).thenReturn(true);

        launchActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        verify(mockPresenter).requestSignUpBy(eq(mockGoogleSignInResult));

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(ProvisioningActivity.class.getName());
    }

    @Test
    public void Google_인증_성공후_구글정보변환이_성공하고_Fomes_가입요청이_성공하고_프로비저닝_상태가_아니면__런치화면으로_이동한다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockPresenter.convertGoogleSignInResult(any())).thenReturn(mockGoogleSignInResult);
        when(mockPresenter.requestSignUpBy(any())).thenReturn(Single.just("fomesAccessToken"));
        when(mockPresenter.isProvisioningProgress()).thenReturn(false);

        launchActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        verify(mockPresenter).requestSignUpBy(eq(mockGoogleSignInResult));

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(MainActivity.class.getName());
    }

    @Test
    public void Google_인증_성공후_구글정보변환이_성공하고_Fomes_가입요청이_실패하면__실패문구를_띄운다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockPresenter.convertGoogleSignInResult(any())).thenReturn(mockGoogleSignInResult);
        when(mockPresenter.requestSignUpBy(any())).thenReturn(Single.error(new Throwable()));

        launchActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        assertThat(ShadowToast.getTextOfLatestToast()).contains("가입에 실패하였습니다.");
    }
}