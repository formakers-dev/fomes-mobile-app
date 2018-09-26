package com.formakers.fomes.provisioning.presenter;


import android.content.Intent;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.LoginContract;
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

import edu.emory.mathcs.backport.java.util.Collections;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoginPresenterTest {

    @Inject UserService mockUserService;
    @Inject GoogleSignInAPIHelper mockGoogleSignInAPIHelper;
    @Inject SharedPreferencesHelper mockSharedPreferencesHelper;
    @Inject UserDAO mockUserDAO;
    @Inject JobManager mockJobManager;

    @Mock LoginContract.View mockView;

    LoginPresenter subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();

            }
        });

        MockitoAnnotations.initMocks(this);

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        subject = new LoginPresenter(mockView, mockGoogleSignInAPIHelper, mockUserService, mockSharedPreferencesHelper, mockUserDAO, mockJobManager);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void requestSignUpBy_호출시__받아온_구글정보로_가입요청을_시도한다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just("anything"));

        subject.requestSignUpBy(mockResult);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).signUp(eq("testIdToken"), userCaptor.capture());
        assertThat(userCaptor.getValue().getUserId()).isEqualTo("testId");
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("testEmail");
        assertThat(userCaptor.getValue().getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void requestSignUpBy_호출시__가입요청_성공시__FomesToken_및_유저정보을_저장하고_데이터잡을_등록한다() {
        GoogleSignInResult mockResult = mock(GoogleSignInResult.class);
        when(mockResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockAccount.getDisplayName()).thenReturn("testName");
        when(mockResult.getSignInAccount()).thenReturn(mockAccount);

        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just("testFomesToken"));

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        subject.requestSignUpBy(mockResult).subscribe(testSubscriber);

        verify(mockSharedPreferencesHelper).setAccessToken(eq("testFomesToken"));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDAO).updateUserInfo(userArgumentCaptor.capture());

        verify(mockJobManager).registerSendDataJob(eq(JobManager.JOB_ID_SEND_DATA));

        User user = userArgumentCaptor.getValue();
        assertThat(user.getUserId()).isEqualTo("testId");
        assertThat(user.getName()).isEqualTo("testName");
        assertThat(user.getEmail()).isEqualTo("testEmail");
        assertThat(user.getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void getGoogleSignInIntent__호출시__GoogleSignInIntent를_리턴한다() {
        subject.getGoogleSignInIntent();
        verify(mockGoogleSignInAPIHelper).getSignInIntent();
    }

    @Test
    public void convertGoogleSignInResult_호출시__구글정보로_변환하여_리턴한다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(true);
        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockGoogleSignInResult);

        GoogleSignInResult result = subject.convertGoogleSignInResult(mock(Intent.class));

        assertThat(result).isEqualTo(mockGoogleSignInResult);
    }

    @Test
    public void convertGoogleSignInResult_호출시_구글정보로_변환이_실패하면__null을_리턴한다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(false);
        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockGoogleSignInResult);

        GoogleSignInResult result = subject.convertGoogleSignInResult(mock(Intent.class));

        assertThat(result).isNull();
    }

    @Test
    public void googleSilentSignIn_호출시__구글사일런트로그인을_요청하고_성공하면__구글정보를_리턴한다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(true);
        when(mockGoogleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mockGoogleSignInResult));

        TestSubscriber<GoogleSignInResult> testSubscriber = new TestSubscriber<>();
        subject.googleSilentSignIn().subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(mockGoogleSignInResult));
        testSubscriber.assertCompleted();
    }

    @Test
    public void googleSilentSignIn_호출시__구글사일런트로그인을_요청하고_실패하면__에러를_리턴한다() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(false);
        when(mockGoogleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mockGoogleSignInResult));

        TestSubscriber<GoogleSignInResult> testSubscriber = new TestSubscriber<>();
        subject.googleSilentSignIn().subscribe(testSubscriber);

        testSubscriber.assertError(Exception.class);
    }

    @Test
    public void isProvisioningProgress_호출시__프로비저닝_상태인지를_반환한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        assertThat(subject.isProvisioningProgress()).isTrue();

        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        assertThat(subject.isProvisioningProgress()).isFalse();
    }
}