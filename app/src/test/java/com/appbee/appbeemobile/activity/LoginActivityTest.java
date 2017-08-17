package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.network.SignInResultCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;

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
    private ActivityController<LoginActivity> activityController;

    @Inject
    GoogleSignInAPIHelper googleSignInAPIHelper;

    @Inject
    UserService userService;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(LoginActivity.class);
    }

    private LoginActivity getSubjectAfterSetupGoogleSignIn() {
        Intent intent = new Intent(RuntimeEnvironment.application, SignInHubActivity.class);
        intent.setAction("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        when(googleSignInAPIHelper.requestSignInIntent(any())).thenReturn(intent);

        when(localStorageHelper.getAccessToken()).thenReturn("");

        return activityController.create().get();
    }

    @Test
    public void onCreate앱시작시_GoogleSignInActivity가_시작된다() throws Exception {
        LoginActivity subject = getSubjectAfterSetupGoogleSignIn();

        ShadowActivity shadowActivity = shadowOf(subject);
        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowActivity.getNextStartedActivityForResult();

        assertThat(nextStartedActivityForResult.intent.getAction()).contains("GOOGLE_SIGN_IN");
        assertThat(nextStartedActivityForResult.intent.getComponent().getClassName()).contains("SignInHubActivity");
    }

    @Test
    public void onActivityResult_GoogleSign성공시_user정보를_저장하는API를_호출한다() throws Exception {
        LoginActivity subject = getSubjectAfterSetupGoogleSignIn();

        GoogleSignInAccount account = mock(GoogleSignInAccount.class);
        when(account.getIdToken()).thenReturn("testToken");
        when(account.getId()).thenReturn("testId");
        when(account.getDisplayName()).thenReturn("testName");

        GoogleSignInResult googleSignInResult = new GoogleSignInResult(account, Status.zzayh);
        GoogleSignInResult spy = spy(googleSignInResult);
        when(googleSignInAPIHelper.requestSignInResult(any())).thenReturn(spy);
        doReturn(true).when(spy).isSuccess();

        subject.onActivityResult(9001, 0, null);

        verify(userService).signIn(eq("testToken"), any(SignInResultCallback.class));
    }

    @Test
    public void user정보저장이_성공하면_userID를_sharedPreferences에_저장하고_MainActivity를_시작한다() throws Exception {
        when(googleSignInAPIHelper.requestSignInIntent(any(GoogleApiClient.class))).thenReturn(mock(Intent.class));

        doAnswer((invocation) -> {
            ((SignInResultCallback) invocation.getArguments()[1]).onSuccess("testAccessToken");
            return null;
        }).when(userService).signIn(anyString(), any(SignInResultCallback.class));

        LoginActivity subject = activityController.create().get();
        shadowOf(subject).getNextStartedActivity();

        subject.signInUser("testIdToken");

        verify(localStorageHelper).setAccessToken("testAccessToken");

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(MainActivity.class.getSimpleName());
    }
}
