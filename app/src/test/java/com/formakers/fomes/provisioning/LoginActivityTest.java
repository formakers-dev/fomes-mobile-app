package com.formakers.fomes.provisioning;

import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.activity.BaseActivityTest;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import javax.inject.Inject;

import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


public class LoginActivityTest extends BaseActivityTest<LoginActivity> {

    private LoginActivity subject;

    @Inject UserService mockUserService;
    @Inject GoogleSignInAPIHelper mockGoogleSignInAPIHelper;
    @Inject LocalStorageHelper mockLocalStorageHelper;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void LoginActivity_시작시_로그인화면이_나타난다() {
        subject = getActivity();

        assertThat(subject.findViewById(R.id.login_logo).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_title).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_subtitle).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_google_button).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.login_tnc).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 약관링크_클릭시_해당_링크를_브라우저로_띄운다() {
        subject = getActivity();

        assertThat(((TextView) subject.findViewById(R.id.login_tnc)).getMovementMethod().getClass().getSimpleName())
                .isEqualTo(LinkMovementMethod.class.getSimpleName());
    }

    @Test
    public void 로그인버튼_클릭시_Google_인증_로직을_실행한다() {
        Intent intent = new Intent(RuntimeEnvironment.application, SignInHubActivity.class);
        intent.setAction("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        when(mockGoogleSignInAPIHelper.getSignInIntent()).thenReturn(intent);

        subject = getActivity();
        subject.findViewById(R.id.login_google_button).performClick();

        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(subject).getNextStartedActivityForResult();

        assertThat(nextStartedActivityForResult.intent.getAction()).contains("GOOGLE_SIGN_IN");
        assertThat(nextStartedActivityForResult.intent.getComponent().getClassName()).isEqualTo(SignInHubActivity.class.getName());
    }

    @Test
    public void Google_인증_성공시_Fomes에_가입을_요청한다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);
        when(mockLocalStorageHelper.getRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Observable.just(""));

        subject = getActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).signUp(eq("testIdToken"), userCaptor.capture());
        assertThat(userCaptor.getValue().getUserId()).isEqualTo("testId");
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("testEmail");
        assertThat(userCaptor.getValue().getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void Fomes_가입요청_성공시_FomesToken_및_유저정보를_저장한다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);
        when(mockLocalStorageHelper.getRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Observable.just("testFomesToken"));

        subject = getActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        verify(mockLocalStorageHelper).setAccessToken(eq("testFomesToken"));
        verify(mockLocalStorageHelper).setUserId(eq("testId"));
        verify(mockLocalStorageHelper).setEmail(eq("testEmail"));
    }

    @Test
    public void Fomes_가입요청_실패시_실패메세지를_띄운다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);
        when(mockLocalStorageHelper.getRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Observable.error(new Throwable()));

        subject = getActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("가입에 실패하였습니다. 재시도 고고");
    }

    @Test
    public void Google_인증_실패시_실패문구를_띄운다() {
        subject = getActivity();
        subject.onActivityResult(9001, Activity.RESULT_CANCELED, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("구글 로그인에 실패하였습니다.");
    }

    @Test
    public void Google_인증_성공후_result가_없거나_실패인_경우_실패문구를_띄운다() {
        // 없는경우
        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(null);

        subject = getActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("구글 로그인에 실패하였습니다.");

        // 실패인 경우
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(false);
        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);

        subject.onActivityResult(9001, Activity.RESULT_OK, new Intent());
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("구글 로그인에 실패하였습니다.");
    }
}