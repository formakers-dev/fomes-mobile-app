package com.formakers.fomes.provisioning.presenter;


import android.content.Intent;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.util.FomesConstants;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginPresenterTest {

    @Inject UserService mockUserService;
    @Inject GoogleSignInAPIHelper mockGoogleSignInAPIHelper;
    @Inject SharedPreferencesHelper mockSharedPreferencesHelper;

    @Mock LoginContract.View mockView;

    LoginPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        subject = new LoginPresenter(mockView, mockGoogleSignInAPIHelper, mockUserService, mockSharedPreferencesHelper);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void requestSignUpBy__GoogleSignInResult가_정상_리턴_되었을_때__포메스에_가입을_요청한다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);
        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Observable.empty());

        subject.requestSignUpBy(mock(Intent.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).signUp(eq("testIdToken"), userCaptor.capture());
        assertThat(userCaptor.getValue().getUserId()).isEqualTo("testId");
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("testEmail");
        assertThat(userCaptor.getValue().getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void requestSignUpBy__GoogleSignInResult가_null로_리턴_되었을_때__false를_리턴한다() {
        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(null);

        assertThat(subject.requestSignUpBy(mock(Intent.class))).isEqualTo(false);
    }

    @Test
    public void requestSignUpBy__GoogleSignInResult의_상태가_Fail일_때__false를_리턴한다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(false);

        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);

        assertThat(subject.requestSignUpBy(mock(Intent.class))).isEqualTo(false);
    }

    @Test
    public void requestSignUpBy__Fomes_가입요청_성공시_FomesToken_및_유저정보와_프로비저닝_플로우_상태를_저장하고_화면을_이동한다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);
        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("testRegistrationToken");

        // Fomes 가입 요청 성공시
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Observable.just("testFomesToken"));

        subject.requestSignUpBy(mock(Intent.class));

        verify(mockSharedPreferencesHelper).setAccessToken(eq("testFomesToken"));
        verify(mockSharedPreferencesHelper).setUserId(eq("testId"));
        verify(mockSharedPreferencesHelper).setEmail(eq("testEmail"));
        verify(mockSharedPreferencesHelper).setProvisioningProgressStatus(eq(FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO));

        verify(mockView).startActivityAndFinish(eq(ProvisioningActivity.class));
    }

    @Test
    public void requestSignUpBy__Fomes_가입요청_실패시_실패메세지를_띄운다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockResult);
        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("testRegistrationToken");

        // Fomes 가입 요청 실패시
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Observable.error(new Throwable()));

        doNothing().when(mockView).showToast(anyString());

        subject.requestSignUpBy(mock(Intent.class));

        verify(mockView).showToast(contains("실패"));
    }

    @Test
    public void getGoogleSignInIntent__호출시__GoogleSignInIntent를_리턴한다() {
        subject.getGoogleSignInIntent();
        verify(mockGoogleSignInAPIHelper).getSignInIntent();
    }
}