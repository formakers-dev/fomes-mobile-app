package com.formakers.fomes.helper;

import com.formakers.fomes.model.User;
import com.formakers.fomes.network.api.ProjectAPI;
import com.formakers.fomes.network.api.UserAPI;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AppBeeAPIHelperTest {
    @Mock
    private GoogleSignInAPIHelper mockGoogleSignInAPIHelper;

    @Mock
    private UserAPI mockUserAPI;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Mock
    private ProjectAPI mockProjectAPI;  // for test

    private AppBeeAPIHelper subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.trampoline());
        RxJavaHooks.onComputationScheduler(Schedulers.trampoline());
        RxJavaHooks.onNewThreadScheduler(Schedulers.trampoline());

        MockitoAnnotations.initMocks(this);
        subject = new AppBeeAPIHelper(mockLocalStorageHelper, mockGoogleSignInAPIHelper, mockUserAPI);

        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mockGoogleSignInResult));
        when(mockGoogleSignInResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockGoogleSignInResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        when(mockGoogleSignInAccount.getIdToken()).thenReturn("idToken");
        when(mockUserAPI.signIn(eq("idToken"), any(User.class))).thenReturn(Observable.just("appbeeToken"));
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void API조회시_401에러코드를_받은_경우_새로운_토큰을_발급받아_갱신하고_재요청한다() throws Exception {
        setupTokenException(401);

        Observable.defer(() -> mockProjectAPI.getProject(any(), any()))
                .compose(subject.refreshExpiredToken())
                .toList().toBlocking().single();

        verify(mockProjectAPI, times(2)).getProject(any(), any());
        verifyRefreshToken();
    }

    @Test
    public void API조회시_403에러코드를_받은_경우_새로운_토큰을_발급받아_갱신하고_재요청한다() throws Exception {
        setupTokenException(403);

        Observable.defer(() -> mockProjectAPI.getProject(any(), any()))
                .compose(subject.refreshExpiredToken())
                .toList().toBlocking().single();

        verify(mockProjectAPI, times(2)).getProject(any(), any());
        verifyRefreshToken();
    }

    private void setupTokenException(int errorCode) {
        when(mockProjectAPI.getProject(any(), any()))
                .thenReturn(Observable.error(new HttpException(Response.error(errorCode, ResponseBody.create(null, "")))))
                .thenReturn(Completable.complete().toObservable());
    }

    private void verifyRefreshToken() {
        verify(mockUserAPI).signIn(eq("idToken"), any(User.class));
        verify(mockLocalStorageHelper).setAccessToken(eq("appbeeToken"));
    }
}
