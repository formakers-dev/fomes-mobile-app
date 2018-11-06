package com.formakers.fomes.provisioning.presenter;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.common.repository.dao.UserDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

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
    @Mock
    AndroidNativeHelper mockAndroidNativeHelper;
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;
    @Mock UserDAO mockUserDAO;

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

        subject = new ProvisioningPresenter(mockView, mockUser, mockUserService, mockAndroidNativeHelper, mockSharedPreferencesHelper, mockUserDAO);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void updateDemographicsToUser__호출시__유저정보를_업데이트한다() {
        subject.updateDemographicsToUser(1989, 1, "male");

        verify(mockUser).setBirthday(eq(1989));
        verify(mockUser).setJob(eq(1));
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
    public void emitUpdateHeaderViewEvent__헤더뷰업데이트_이벤트_발생시__뷰에_헤더뷰_셋팅을_요청한다() {
        subject.emitUpdateHeaderViewEvent(R.string.provision_user_info_title, R.string.provision_user_info_subtitle);
        verify(mockView).setHeaderView(eq(R.string.provision_user_info_title), eq(R.string.provision_user_info_subtitle));
    }

    @Test
    public void emitNextPageEvent__다음_화면으로_넘어가는_이벤트_발생시__뷰에_다음페이지를_요청한다() {
        subject.emitNextPageEvent();
        verify(mockView).nextPage();
    }

    @Test
    public void emitFilledUpEvent__항목들을_다_채웠다는_이벤트_발생시__뷰에_다음버튼을_보여주도록_요청한다() {
        when(mockView.isSelectedFragement(any(BaseFragment.class))).thenReturn(true);

        subject.emitFilledUpEvent(new BaseFragment(), true);

        verify(mockView).setNextButtonVisibility(eq(true));
    }

    @Test
    public void emitNeedToGrantEvent__권한이_필요하다는_이벤트_발생시__권한허용_버튼을_셋팅한다() {
        subject.emitNeedToGrantEvent();

        verify(mockView).setNextButtonText(eq(R.string.common_go_to_grant));
        verify(mockView).setNextButtonVisibility(eq(true));
    }

    @Test
    public void emitStartActivityAndFinishEvent__화면전환_이벤트_발생시__화면을_이동하고_종료하도록_뷰에_요청한다() {

        subject.emitStartActivityAndFinishEvent(MainActivity.class);

        verify(mockView).startActivityAndFinish(eq(MainActivity.class));
    }

    @Test
    public void requestVerifyUserToken_호출시__유저_토큰_검증을_요청한다() {
        when(mockUserService.verifyToken()).thenReturn(Completable.complete());

        subject.requestVerifyUserToken();

        verify(mockUserService).verifyToken();
    }

    @Test
    public void requestUpdateUser__호출시__유저정보_업데이트_API를_호출한다() {
        when(mockUserService.updateUser(any(User.class))).thenReturn(Completable.complete());

        subject.requestUpdateUser();

        verify(mockUserDAO).updateUserInfo(eq(mockUser));
        verify(mockUserService).updateUser(eq(mockUser));
    }

    @Test
    public void hasUsageStatsPermission__호출시__사용정보_접근_권한을_체크한다() {
        subject.hasUsageStatsPermission();

        verify(mockAndroidNativeHelper).hasUsageStatsPermission();
    }

    @Test
    public void setProvisioningProgressStatus__호출시__프로비저닝_플로우_상태를_저장한다() {
        subject.setProvisioningProgressStatus(1);

        verify(mockSharedPreferencesHelper).setProvisioningProgressStatus(eq(1));
    }
}