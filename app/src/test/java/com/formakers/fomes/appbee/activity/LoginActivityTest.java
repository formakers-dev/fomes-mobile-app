package com.formakers.fomes.appbee.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.common.view.BaseActivityTest;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.common.network.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

@Ignore
public class LoginActivityTest extends BaseActivityTest<LoginActivity> {
    private LoginActivity subject;

    @Inject
    GoogleSignInAPIHelper googleSignInAPIHelper;

    @Inject
    UserService userService;

    @Inject
    SharedPreferencesHelper SharedPreferencesHelper;

    @Mock
    GoogleSignInAccount mockGoogleSignInAccount;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        MockitoAnnotations.initMocks(this);

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        Intent intent = new Intent(RuntimeEnvironment.application, SignInHubActivity.class);
        intent.setAction("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        when(googleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mock(GoogleSignInResult.class)));
        when(googleSignInAPIHelper.getSignInIntent()).thenReturn(intent);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        super.tearDown();
    }

    private LoginActivity getSubjectAfterSetupGoogleSignIn() {
        mockGoogleSignInResult(true);
        mockPerson();
        when(userService.signIn(anyString(), any())).thenReturn(Observable.just("testAccessToken"));

        return getActivity();
    }

    private LoginActivity getSubjectAfterSetupGoogleSilentSignedIn() {
        GoogleSignInResult mockGoogleSignInResult = mockGoogleSignInResult(true);
        mockPerson();
        when(userService.signIn(anyString(), any())).thenReturn(Observable.just("testAccessToken"));

        when(googleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mockGoogleSignInResult));

        return getActivity();
    }

    private void mockPerson() {
        Gender gender = new Gender().setValue("male");
        Birthday birthday = new Birthday().setDate(new Date().setYear(1999).setMonth(11).setDay(31));
        when(googleSignInAPIHelper.getPerson(any())).thenReturn(Observable.just(getPerson(gender, birthday)));
    }

    private GoogleSignInResult mockGoogleSignInResult(boolean isSuccess) {
        when(mockGoogleSignInAccount.getIdToken()).thenReturn("testToken");
        when(mockGoogleSignInAccount.getId()).thenReturn("testId");
        when(mockGoogleSignInAccount.getDisplayName()).thenReturn("testName");
        when(mockGoogleSignInAccount.getEmail()).thenReturn("testEmail");
        when(googleSignInAPIHelper.getProvider()).thenReturn("google");

        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(isSuccess);
        when(googleSignInAPIHelper.getSignInResult(any())).thenReturn(mockGoogleSignInResult);
        when(mockGoogleSignInResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);

        return mockGoogleSignInResult;
    }

    private void mockGoogleSignInResultWithoutAccount(boolean isSuccess) {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(isSuccess);
        when(googleSignInAPIHelper.getSignInResult(any())).thenReturn(mockGoogleSignInResult);
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

        when(userService.signIn(anyString(), any())).thenReturn(Observable.just("accessToken"));

        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        verify(googleSignInAPIHelper).getPerson(eq(mockGoogleSignInAccount));
        verify(userService).signIn(eq("testToken"), any(User.class));
    }

    @Test
    public void onActivityResult_GoogleSign결과실패시_오류메시지를_표시하고_액티비티를_종료한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        mockGoogleSignInResult(false);

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

        verify(googleSignInAPIHelper, times(0)).getSignInIntent();
    }

    @Test
    public void onActivityResult에서_호출된_signInAPI_성공응답을_받으면_user정보를_sharedPreferences에_저장하고_OnboardingActivity로_이동한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSignIn();

        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        verifySharedPreferenceForPersonDataAndMoveTo(OnboardingActivity.class.getSimpleName());
    }

    @Test
    public void signInAPI_실패응답을_받으면_오류메세지를_표시한다() throws Exception {
        mockGoogleSignInResult(true);
        mockPerson();
        when(userService.signIn(anyString(), any())).thenReturn(Observable.error(new Throwable()));

        subject = getActivity();
        subject.onActivityResult(9001, Activity.RESULT_OK, null);

        assertFinishActivityForFail("Fail to sign in");
    }

    /* SilentSignIn 관련 테스트 */
    @Test
    public void onPostCreate시_기존에_구글로그인하지_않아서_GoogleSignInResult가_null인_경우_로그인버튼을_표시한다() throws Exception {
        when(googleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(null));
        subject = getSubjectAfterSetupGoogleSignIn();

        assertThat(subject.loginButtonLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void onPostCreate시_기존에_구글로그인했으나_GoogleSignInResult의_isSuccess가_false인_경우_로그인버튼을_표시한다() throws Exception {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(false);
        when(googleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mockGoogleSignInResult));

        subject = getSubjectAfterSetupGoogleSignIn();

        assertThat(subject.loginButtonLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void onPostCreate시_기존에_구글로그인한_경우_로그인버튼을_표시하지_않는다() throws Exception {
        subject = getSubjectAfterSetupGoogleSilentSignedIn();

        assertThat(subject.loginButtonLayout.getVisibility()).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void onPostCreate시_기존에_구글로그인한_경우_person데이터를_조회한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSilentSignedIn();

        verify(googleSignInAPIHelper).getPerson(eq(mockGoogleSignInAccount));
    }

    @Test
    public void SilentSignIn후_signInAPI_성공응답을_받으면_sharedPreferences에_저장하고_MainActivity로_이동한다() throws Exception {
        subject = getSubjectAfterSetupGoogleSilentSignedIn();

        verifySharedPreferenceForPersonDataAndMoveTo(MainActivity.class.getSimpleName());
    }

    private void verifySharedPreferenceForPersonDataAndMoveTo(String moveToActivitySimpleName) {
        verify(SharedPreferencesHelper).setAccessToken("testAccessToken");
        verify(SharedPreferencesHelper).setUserId("googletestId");
        verify(SharedPreferencesHelper).setEmail("testEmail");

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).contains(moveToActivitySimpleName);
        assertThat(subject.isFinishing()).isTrue();
    }

    private void assertFinishActivityForFail(String message) {
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(message);
        assertThat(shadowOf(subject).getResultCode()).isEqualTo(Activity.RESULT_CANCELED);
        assertThat(shadowOf(subject).isFinishing()).isTrue();
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