package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        subject = Robolectric.buildActivity(LoginActivity.class).create().get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    private LoginActivity getSubjectAfterSetupGoogleSignIn() {
        Intent intent = new Intent(RuntimeEnvironment.application, SignInHubActivity.class);
        intent.setAction("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        when(googleSignInAPIHelper.requestSignInIntent(any())).thenReturn(intent);

        return Robolectric.setupActivity(LoginActivity.class);
    }

    @Test
    public void onPostCreate시_사용약관에링크가_나타난다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        assertThat(subject.tncAgreeTextView.getMovementMethod().getClass().getSimpleName()).isEqualTo(LinkMovementMethod.class.getSimpleName());
    }

    @Test
    public void login버튼_클릭시_GoogleSignInActivity가_시작된다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();
        subject.findViewById(R.id.login_button).performClick();

        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(subject).getNextStartedActivityForResult();
        assertThat(nextStartedActivityForResult.intent.getAction()).contains("GOOGLE_SIGN_IN");
        assertThat(nextStartedActivityForResult.intent.getComponent().getClassName()).isEqualTo(SignInHubActivity.class.getName());
    }

    @Test
    public void onActivityResult_GoogleSign성공시_Person_Profile정보조회후_signIn_API를_호출한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        when(googleSignInAPIHelper.getPerson(any())).thenReturn(Observable.just(new Person()));

        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        mockGoogleSignInResult(mockGoogleSignInAccount, true);

        when(userService.signIn(anyString(), any())).thenReturn(Observable.just("accessToken"));

        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        verify(googleSignInAPIHelper).getPerson(eq(mockGoogleSignInAccount));
        verify(userService).signIn(eq("testToken"), any(User.class));
    }

    @Test
    public void onActivityResult_GoogleSign결과실패시_오류메시지를_표시하고_액티비티를_종료한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        mockGoogleSignInResult(mock(GoogleSignInAccount.class), false);

        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        assertFinishActivityForFail("Fail to connect Google Play Service");
    }

    @Test
    public void onActivityResult_GoogleSign성공했으나_구글계정정보가_없는경우_오류메시지를_표시하고_액티비티를_종료한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        mockGoogleSignInResultWithoutAccount(true);

        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        assertFinishActivityForFail("Fail to connect Google Play Service");
    }

    @Test
    public void onActivityResult_요청취소시_아무것도하지않고_리턴한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        subject.onActivityResult(9001, Activity.RESULT_CANCELED, null);

        verify(googleSignInAPIHelper, times(0)).requestSignInIntent(any());
    }

    @Test
    public void signInAPI_성공응답을_받으면_user정보를_sharedPreferences에_저장하고_OnboardingActivity를_시작한다() throws Exception {
        when(userService.signIn(anyString(), any())).thenReturn(Observable.just("testAccessToken"));
        when(googleSignInAPIHelper.getProvider()).thenReturn("google");

        Gender gender = new Gender().setValue("male");
        Birthday birthday = new Birthday().setDate(new Date().setYear(1999).setMonth(11).setDay(31));
        Person person = getPerson(gender, birthday);

        subject.signInUser("testIdToken", "testGoogleId", "testEmail", person);

        verify(localStorageHelper).setAccessToken("testAccessToken");
        verify(localStorageHelper).setUserId("googletestGoogleId");

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).contains(OnboardingActivity.class.getSimpleName());
    }

    @Test
    public void signInAPI_실패응답을_받으면_오류메세지를_표시한다() throws Exception {
        when(userService.signIn(anyString(), any())).thenReturn(Observable.error(new Throwable()));

        subject.signInUser("testIdToken", "testGoogleId", "testEmail", null);

        assertFinishActivityForFail("Fail to sign in");
    }

    private void assertFinishActivityForFail(String message) {
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(message);
        assertThat(shadowOf(subject).getResultCode()).isEqualTo(Activity.RESULT_CANCELED);
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    private void mockGoogleSignInResult(GoogleSignInAccount mockAccount, boolean isSuccess) {
        when(mockAccount.getIdToken()).thenReturn("testToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getDisplayName()).thenReturn("testName");

        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(isSuccess);
        when(googleSignInAPIHelper.requestSignInResult(any())).thenReturn(mockGoogleSignInResult);
        when(mockGoogleSignInResult.getSignInAccount()).thenReturn(mockAccount);
    }

    private void mockGoogleSignInResultWithoutAccount(boolean isSuccess) {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(isSuccess);
        when(googleSignInAPIHelper.requestSignInResult(any())).thenReturn(mockGoogleSignInResult);
    }


    private Person getPerson(Gender gender, Birthday birthday) {
        List<Birthday> birthdayList = new ArrayList<>();
        birthdayList.add(birthday);

        List<Gender> genderList = new ArrayList<>();
        genderList.add(gender);

        Person person = new Person();
        person.setBirthdays(birthday != null ? birthdayList : null);
        person.setBirthdays(birthday != null ? birthdayList : null);
        person.setGenders(gender != null ? genderList : null);

        return person;
    }
}