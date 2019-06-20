package com.formakers.fomes.provisioning.presenter;


import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.LoginContract;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
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
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class LoginPresenterTest {

    @Inject UserService mockUserService;
    @Inject GoogleSignInAPIHelper mockGoogleSignInAPIHelper;
    @Inject SharedPreferencesHelper mockSharedPreferencesHelper;
    @Inject UserDAO mockUserDAO;
    @Inject JobManager mockJobManager;
    @Inject ChannelManager mockChannelManager;

    @Mock GoogleSignInResult mockGoogleSignInResult;
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

        when(mockGoogleSignInResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockAccount = mock(GoogleSignInAccount.class);
        when(mockAccount.getIdToken()).thenReturn("testIdToken");
        when(mockAccount.getId()).thenReturn("testId");
        when(mockAccount.getEmail()).thenReturn("testEmail");
        when(mockAccount.getDisplayName()).thenReturn("testName");
        when(mockGoogleSignInResult.getSignInAccount()).thenReturn(mockAccount);

        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);
        subject = new LoginPresenter(mockView, mockGoogleSignInAPIHelper, mockUserService, mockSharedPreferencesHelper, mockUserDAO, mockJobManager, mockChannelManager);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void signUpOrSignIn_호출시__프로비저닝_단계면__받아온_구글정보로_가입요청을_시도한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just("anything"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).signUp(eq("testIdToken"), userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("testEmail");
        assertThat(userCaptor.getValue().getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void signUp_성공시__유저정보를_내부_디비에_저장한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just("testFomesToken"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        // 유저정보 내부디비 저장
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDAO).updateUserInfo(userArgumentCaptor.capture());

        User user = userArgumentCaptor.getValue();
        assertThat(user.getName()).isEqualTo("testName");
        assertThat(user.getEmail()).isEqualTo("testEmail");
        assertThat(user.getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void singUp성공시__FomesToken저장_공지채널등록_단기통계데이터작업등록() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just("testFomesToken"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        // 포메스 토큰 저장
        verify(mockSharedPreferencesHelper).setAccessToken(eq("testFomesToken"));

        // 공지 채널 등록
        verify(mockChannelManager).subscribePublicTopic();

        // 단기 통계 데이터 작업 등록
        verify(mockJobManager).registerSendDataJob(eq(JobManager.JOB_ID_SEND_DATA));
    }

    @Test
    public void singUp성공시__프로비저닝_플로우로_넘어가는_요청을_보낸다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just("testFomesToken"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockView).startActivityAndFinish(eq(ProvisioningActivity.class));
    }

    @Test
    public void signUpOrSignIn_호출시__프로비저닝_단계가_아니면__받아온_구글정보로_로그인요청을_시도한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just("testFomesToken"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));
    }

    @Test
    public void singIn성공시__프로비저닝_단계를_완료시킨다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just("testFomesToken"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        // 포메스 토큰 저장
        verify(mockSharedPreferencesHelper).setProvisioningProgressStatus(eq(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED));
    }

    @Test
    public void singIn성공시__FomesToken저장_공지채널등록_단기통계데이터작업등록() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just("testFomesToken"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        // 포메스 토큰 저장
        verify(mockSharedPreferencesHelper).setAccessToken(eq("testFomesToken"));

        // 공지 채널 등록
        verify(mockChannelManager).subscribePublicTopic();

        // 단기 통계 데이터 작업 등록
        verify(mockJobManager).registerSendDataJob(eq(JobManager.JOB_ID_SEND_DATA));
    }

    @Test
    public void singIn성공시__메인화면으로_넘어가는_요청을_보낸다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just("testFomesToken"));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        verify(mockView).startActivityAndFinish(eq(MainActivity.class));
    }

    @Test
    public void signUpOrSignIn실패시__실패문구를_토스트로_띄우도록_요청한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.error(new Throwable()));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        verify(mockView).showToast(contains("실패"));
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