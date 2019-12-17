package com.formakers.fomes.common.helper;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.api.AppAPI;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.network.helper.APIHelper;
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
public class APIHelperTest {
    @Mock
    private GoogleSignInAPIHelper mockGoogleSignInAPIHelper;

    @Mock
    private UserAPI mockUserAPI;

    @Mock
    private AppAPI mockAppAPI;

    @Mock
    private SharedPreferencesHelper mockSharedPreferencesHelper;

    private APIHelper subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.trampoline());
        RxJavaHooks.onComputationScheduler(Schedulers.trampoline());
        RxJavaHooks.onNewThreadScheduler(Schedulers.trampoline());

        MockitoAnnotations.initMocks(this);
        subject = new APIHelper(mockSharedPreferencesHelper, mockGoogleSignInAPIHelper, mockUserAPI);

        GoogleSignInResult mockGoogleSignInResult = mock(GoogleSignInResult.class);
        when(mockGoogleSignInAPIHelper.requestSilentSignInResult()).thenReturn(Observable.just(mockGoogleSignInResult));
        when(mockGoogleSignInResult.isSuccess()).thenReturn(true);

        GoogleSignInAccount mockGoogleSignInAccount = mock(GoogleSignInAccount.class);
        when(mockGoogleSignInResult.getSignInAccount()).thenReturn(mockGoogleSignInAccount);
        when(mockGoogleSignInAccount.getIdToken()).thenReturn("idToken");
        when(mockUserAPI.signIn(eq("idToken"))).thenReturn(Observable.just(new User().setAccessToken("appbeeToken")));
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void API조회시_401에러코드를_받은_경우_새로운_토큰을_발급받아_갱신하고_재요청한다() throws Exception {
        setupTokenException(401);

        Observable.defer(() -> mockAppAPI.getAppInfo(any(), any()))
                .compose(subject.refreshExpiredToken())
                .toList().toBlocking().single();

        verify(mockAppAPI, times(2)).getAppInfo(any(), any());
        verifyRefreshToken();
    }

//    @Test
//    public void API조회시_403에러코드를_받은_경우__프로비저닝_상태를_초기화한다() throws Exception {
//        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
//        Observable.defer(() -> Observable.error(new HttpException(Response.error(403, ResponseBody.create(null, "")))))
//                .compose(subject.refreshExpiredToken())
//                .subscribe(testSubscriber);
//
//        verify(mockSharedPreferencesHelper).resetProvisioningProgressStatus();
//    }

    private void setupTokenException(int errorCode) {
        when(mockAppAPI.getAppInfo(any(), any()))
                .thenReturn(Observable.error(new HttpException(Response.error(errorCode, ResponseBody.create(null, "")))))
                .thenReturn(Completable.complete().toObservable());
    }

    private void verifyRefreshToken() {
        verify(mockUserAPI).signIn(eq("idToken"));
        verify(mockSharedPreferencesHelper).setAccessToken(eq("appbeeToken"));
    }
}
