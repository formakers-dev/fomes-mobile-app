package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.UserService;
import com.bumptech.glide.load.HttpException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import javax.inject.Inject;

import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginActivityTest extends ActivityTest {
    private LoginActivity subject;

    @Inject
    GoogleSignInAPIHelper googleSignInAPIHelper;

    @Inject
    UserService userService;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
        subject = Robolectric.buildActivity(LoginActivity.class).create().get();
    }

    private LoginActivity getSubjectAfterSetupGoogleSignIn() {
        Intent intent = new Intent(RuntimeEnvironment.application, SignInHubActivity.class);
        intent.setAction("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        when(googleSignInAPIHelper.requestSignInIntent(any())).thenReturn(intent);
        when(localStorageHelper.getAccessToken()).thenReturn("");
        return Robolectric.buildActivity(LoginActivity.class).create().postCreate(null).get();
    }

    @Test
    public void onPostCreate호출시_GoogleSignInActivity가_시작된다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        ShadowActivity shadowActivity = shadowOf(subject);
        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowActivity.getNextStartedActivityForResult();

        assertThat(nextStartedActivityForResult.intent.getAction()).contains("GOOGLE_SIGN_IN");
        assertThat(nextStartedActivityForResult.intent.getComponent().getClassName()).contains("SignInHubActivity");
    }

    @Test
    public void onActivityResult_GoogleSign성공시_user정보를_저장하는API를_호출한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        mockGoogleSignInResult(true);
        when(userService.signIn(any())).thenReturn(mock(Observable.class));

        subject.onActivityResult(9001, 0, null);

        verify(userService).signIn(eq("testToken"));
    }

    @Test
    public void onActivityResult_GoogleSign실패시_오류메시지를_표시한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        mockGoogleSignInResult(false);

        subject.onActivityResult(9001, 0, null);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Fail to connect Google Play Service");
    }

    @Test
    public void onConnectionFailed호출시_GoogleSign실패메시지를_표시한다() throws Exception {
        subject.onConnectionFailed(new ConnectionResult(0));

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Fail to connect Google Play Service");
    }

    @Test
    public void user정보저장이_성공하면_userID를_sharedPreferences에_저장하고_StartActivity를_시작한다() throws Exception {
        doAnswer((invocation) -> Observable.just("testAccessToken")).when(userService).signIn(anyString());

        subject.signInUser("testIdToken", "testGoogleId");

        verify(localStorageHelper).setAccessToken("testAccessToken");
        verify(localStorageHelper).setUserId("testGoogleId");

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(StartActivity.class.getSimpleName());
    }

    @Test
    public void user정보저장이_실패하면_오류메세지를_표시한다() throws Exception {
        doAnswer((invocation) -> Observable.error(new HttpException(404))).when(userService).signIn(anyString());

        subject.signInUser("testIdToken", "testGoogleId");

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Fail to sign in");
    }

    private void mockGoogleSignInResult(boolean isSuccess) {
        GoogleSignInAccount account = mock(GoogleSignInAccount.class);
        when(account.getIdToken()).thenReturn("testToken");
        when(account.getId()).thenReturn("testId");
        when(account.getDisplayName()).thenReturn("testName");

        GoogleSignInResult googleSignInResult = new GoogleSignInResult(account, Status.zzaBm);
        GoogleSignInResult spy = spy(googleSignInResult);
        when(googleSignInAPIHelper.requestSignInResult(any())).thenReturn(spy);
        doReturn(isSuccess).when(spy).isSuccess();
    }
}