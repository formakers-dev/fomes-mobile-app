package com.formakers.fomes.provisioning.login;


import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.main.MainActivity;
import com.formakers.fomes.provisioning.ProvisioningActivity;
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
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
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
    public void signUpOrSignIn_?????????__???????????????_?????????__?????????_???????????????_???????????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just(new User().setAccessToken("anything")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).signUp(eq("testIdToken"), userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("testEmail");
        assertThat(userCaptor.getValue().getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void signUp_?????????__???????????????_??????_?????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just(new User()
                .setName("testName").setEmail("testEmail").setRegistrationToken("testRegistrationToken")
                .setAccessToken("testFomesToken")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        // ???????????? ???????????? ??????
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDAO).updateUserInfo(userArgumentCaptor.capture());

        User user = userArgumentCaptor.getValue();
        assertThat(user.getName()).isEqualTo("testName");
        assertThat(user.getEmail()).isEqualTo("testEmail");
        assertThat(user.getRegistrationToken()).isEqualTo("testRegistrationToken");
    }

    @Test
    public void singUp?????????__FomesToken??????_??????????????????_?????????????????????????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        User user = new User().setAccessToken("testFomesToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just(user));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verifyOnSuccessSignUpOrSignIn(user);
    }

    @Test
    public void singUp?????????__???????????????_????????????_????????????_?????????_?????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.just(new User().setAccessToken("testFomesToken")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockView).startActivityAndFinish(eq(ProvisioningActivity.class));
    }

    private void verifyOnSuccessSignUpOrSignIn(User responseUserInfo) {
        // ???????????? ??????
        verify(mockUserDAO).updateUserInfo(eq(responseUserInfo));

        // ????????? ?????? ??????
        verify(mockSharedPreferencesHelper).setAccessToken(eq("testFomesToken"));

        // ?????? ?????? ??????
        verify(mockChannelManager).subscribeTopic(FomesConstants.Notification.TOPIC_NOTICE_ALL);

        // ?????? ?????? ????????? ?????? ??????
        verify(mockJobManager).registerSendDataJob(eq(JobManager.JOB_ID_SEND_DATA));
    }

    @Test
    public void singUp?????????__?????????_?????????_????????????__singIn????????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.error(new HttpException(Response.error(UserAPI.StatusCode.ALREADY_SIGN_UP, ResponseBody.create(null, "")))));
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just(new User().setAccessToken("testFomesToken")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn("testIdToken");
    }

    @Test
    public void signUpOrSignIn_?????????__???????????????_?????????_?????????__?????????_???????????????_??????????????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just(new User().setAccessToken("testFomesToken")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));
    }

    @Test
    public void singIn?????????__???????????????_?????????_???????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just(new User().setAccessToken("testFomesToken")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        // ????????? ?????? ??????
        verify(mockSharedPreferencesHelper).setProvisioningProgressStatus(eq(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED));
    }

    @Test
    public void singIn?????????__signUp??????????????????__???????????????_????????????_?????????_???????????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signUp(anyString(), any(User.class))).thenReturn(Single.error(new HttpException(Response.error(UserAPI.StatusCode.ALREADY_SIGN_UP, ResponseBody.create(null, "")))));
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just(new User().setAccessToken("testFomesToken")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        // ????????? ?????? ??????
        verify(mockSharedPreferencesHelper).setProvisioningProgressStatus(eq(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION));
    }

    @Test
    public void singIn?????????__??????????????????_FomesToken??????_??????????????????_?????????????????????????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        User user = new User().setAccessToken("testFomesToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just(user));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        verifyOnSuccessSignUpOrSignIn(user);
    }

    @Test
    public void singIn?????????__??????????????????_????????????_?????????_?????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.just(new User().setAccessToken("testFomesToken")));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        verify(mockView).startActivityAndFinish(eq(MainActivity.class));
    }

    @Test
    public void singIn?????????__??????????????????_??????_?????????_????????????__???????????????_?????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.error(new HttpException(Response.error(403, ResponseBody.create(null, "")))));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));
        verify(mockSharedPreferencesHelper).resetProvisioningProgressStatus();
        verify(mockView).showToast(contains("??????"));
        verify(mockView).hideFomesLogoAndShowLoginView();
    }

    @Test
    public void signUpOrSignIn_?????????__??????_?????????_???????????????_????????????_??????_????????????__???????????????_????????????_?????????() {
        when(mockGoogleSignInResult.getSignInAccount()).thenReturn(null);

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockView).showToast("?????? ????????? ??????????????? ???????????? ?????? ?????????????????????. ????????? ??????");
    }

    @Test
    public void signUpOrSignIn?????????__???????????????_????????????_????????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("testRegistrationToken");
        when(mockUserService.signIn(anyString())).thenReturn(Observable.error(new Throwable()));

        subject.signUpOrSignIn(mockGoogleSignInResult);

        verify(mockUserService).signIn(eq("testIdToken"));

        verify(mockView).showToast(contains("??????"));
    }

    @Test
    public void getGoogleSignInIntent__?????????__GoogleSignInIntent???_????????????() {
        subject.getGoogleSignInIntent();
        verify(mockGoogleSignInAPIHelper).getSignInIntent();
    }

    @Test
    public void convertGoogleSignInResult_?????????__???????????????_????????????_????????????() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(true);
        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockGoogleSignInResult);

        GoogleSignInResult result = subject.convertGoogleSignInResult(mock(Intent.class));

        assertThat(result).isEqualTo(mockGoogleSignInResult);
    }

    @Test
    public void convertGoogleSignInResult_?????????_???????????????_?????????_????????????__null???_????????????() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(false);
        when(mockGoogleSignInAPIHelper.getSignInResult(any(Intent.class))).thenReturn(mockGoogleSignInResult);

        GoogleSignInResult result = subject.convertGoogleSignInResult(mock(Intent.class));

        assertThat(result).isNull();
    }

    @Test
    public void googleSilentSignIn_?????????__??????????????????????????????_????????????_????????????__???????????????_????????????() {
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
    public void googleSilentSignIn_?????????__??????????????????????????????_????????????_????????????__?????????_????????????() {
        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInResult.isSuccess()).thenReturn(false);
        when(mockGoogleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mockGoogleSignInResult));

        TestSubscriber<GoogleSignInResult> testSubscriber = new TestSubscriber<>();
        subject.googleSilentSignIn().subscribe(testSubscriber);

        testSubscriber.assertError(Exception.class);
    }

    @Test
    public void isProvisioningProgress_?????????__???????????????_???????????????_????????????() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        assertThat(subject.isProvisioningProgress()).isTrue();

        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        assertThat(subject.isProvisioningProgress()).isFalse();
    }
}