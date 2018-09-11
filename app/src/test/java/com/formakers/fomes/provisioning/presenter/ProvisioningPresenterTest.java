package com.formakers.fomes.provisioning.presenter;

import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.provisioning.view.CurrentAnalysisReportActivity;
import com.formakers.fomes.provisioning.view.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProvisioningPresenterTest {

    @Mock ProvisioningContract.View mockView;
    @Mock User mockUser;
    @Mock UserService mockUserService;
    @Mock AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    ProvisioningPresenter subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.trampoline());
        RxJavaHooks.onComputationScheduler(Schedulers.trampoline());
        RxJavaHooks.onNewThreadScheduler(Schedulers.trampoline());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        MockitoAnnotations.initMocks(this);

        subject = new ProvisioningPresenter(mockView, mockUser, mockUserService, mockAppBeeAndroidNativeHelper);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void updateDemographicsToUser__호출시__유저정보를_업데이트한다() {
        subject.updateDemographicsToUser(1989, "developer", "male");

        verify(mockUser).setBirthday(eq(1989));
        verify(mockUser).setJob(eq("developer"));
        verify(mockUser).setGender(eq("male"));
    }

    @Test
    public void updateLifeGameToUser__호출시__유저의_인생게임정보를_업데이트한다() {
        subject.updateLifeGameToUser("미러스엣지");

        ArrayList<String> lifeGames = new ArrayList<>();
        lifeGames.add("미러스엣지");

        verify(mockUser).setLifeApps(eq(lifeGames));
    }

    @Test
    public void emitNextPageEvent__다음_화면으로_넘어가는_이벤트_발생시__뷰에_다음페이지를_요청한다() {
        subject.emitNextPageEvent();
        verify(mockView).nextPage();
    }

    @Test
    public void emitFilledUpEvent__항목들을_다_채웠다는_이벤트_발생시__뷰에_다음버튼을_보여주도록_요청한다() {
        subject.emitFilledUpEvent(true);
        verify(mockView).setNextButtonVisibility(eq(true));
    }

    @Test
    public void emitGrantedEvent__권한_허용_이벤트_발생시__토큰검증을_하고_성공시__뷰에_화면을_이동하도록_요청한다() {
        when(mockUserService.verifyToken()).thenReturn(Completable.complete());

        subject.emitGrantedEvent(true);

        verify(mockUserService).verifyToken();
        verify(mockView).startActivityAndFinish(eq(CurrentAnalysisReportActivity.class));
    }

    @Test
    public void emitGrantedEvent__권한_허용_이벤트_발생시__토큰검증을_하고_실패시__뷰에_토스트를_요청한다() {
        when(mockUserService.verifyToken()).thenReturn(Completable.error(new Throwable()));

        subject.emitGrantedEvent(true);

        verify(mockUserService).verifyToken();
        verify(mockView).showToast(eq("예상치 못한 에러가 발생하였습니다."));
    }

    @Test
    public void emitGrantedEvent__권한_허용_이벤트_발생시__토큰검증을_하고_인증오류시__뷰에_로그인화면_이동을_요청한다() {
        when(mockUserService.verifyToken()).thenReturn(Completable.error(new HttpException(Response.error(401, ResponseBody.create(null, "")))));

        subject.emitGrantedEvent(true);

        verify(mockUserService).verifyToken();
        verify(mockView).startActivityAndFinish(eq(LoginActivity.class));
        verify(mockView).showToast(eq("인증 오류가 발생하였습니다. 재로그인이 필요합니다."));
    }

    @Test
    public void emitGrantedEvent__권한_미허용_이벤트_발생시__뷰에_다음버튼을_보여주도록_요청한다() {
        subject.emitGrantedEvent(false);

        verify(mockView).setNextButtonVisibility(eq(true));
    }

    @Test
    public void requestUpdateUser__호출시__유저정보_업데이트_API를_호출한다() {
        when(mockUserService.updateUser(any(User.class))).thenReturn(Completable.complete());

        subject.requestUpdateUser();

        verify(mockUserService).updateUser(eq(mockUser));
    }

    @Test
    public void hasUsageStatsPermission__호출시__사용정보_접근_권한을_체크한다() {
        subject.hasUsageStatsPermission();

        verify(mockAppBeeAndroidNativeHelper).hasUsageStatsPermission();
    }
}