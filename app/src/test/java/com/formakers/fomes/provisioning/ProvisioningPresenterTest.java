package com.formakers.fomes.provisioning;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.MainActivity;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.provisioning.ProvisioningPresenter;
import com.formakers.fomes.provisioning.ProvisioningContract;

import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProvisioningPresenterTest {

    @Mock ProvisioningContract.View mockView;
    @Mock UserService mockUserService;
    @Mock AndroidNativeHelper mockAndroidNativeHelper;
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;
    @Mock UserDAO mockUserDAO;
    @Mock AnalyticsModule.Analytics mockAnalytics;
    @Mock User mockUser;

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

        when(mockUser.setAppVersion(any())).thenReturn(mockUser);

        subject = new ProvisioningPresenter(mockView, mockUser, mockUserService, mockAndroidNativeHelper, mockSharedPreferencesHelper, mockUserDAO, mockAnalytics);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void updateUserInfo__호출시__유저정보를_업데이트한다() {
        subject.updateUserInfo("미러스엣지", 1989, 1, "male");

        verify(mockUser).setLifeApps(eq(Lists.newArrayList("미러스엣지")));
        verify(mockUser).setBirthday(eq(1989));
        verify(mockUser).setJob(eq(1));
        verify(mockUser).setGender(eq("male"));
    }

    @Test
    public void getUserNickName__호출시__유저의_닉네임을_반환한다() {
        subject.getUserNickName();

        verify(mockUser).getNickName();
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
//        verify(mockUserService).updateUser(eq(mockUser));
    }

    @Test
    public void requestVerifyUserNickName__호출시__닉네임_검사_API를_호출한다() {
        when(mockUserService.verifyNickName(anyString())).thenReturn(Completable.complete());

        subject.requestVerifyUserNickName("닉네임");

        verify(mockUserService).verifyNickName(eq("닉네임"));
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

    @Test
    public void isSelected_호출시__해당_프래그먼트가_선택되어있는지_확인후_여부를_리턴한다() {
        BaseFragment baseFragment = new BaseFragment();
        when(mockView.isSelectedFragement(baseFragment)).thenReturn(true);

        boolean isSelected = subject.isSelected(baseFragment);

        verify(mockView).isSelectedFragement(baseFragment);
        assertThat(isSelected).isTrue();
    }

    @Test
    public void isProvisiongProgress_호출시__프로비저닝_진행_상태_여부를_리턴한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION);

        boolean isProvisioing = subject.isProvisiongProgress();

        verify(mockSharedPreferencesHelper).getProvisioningProgressStatus();
        assertThat(isProvisioing).isTrue();

        // 완료인 경우
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus()).thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        assertThat(subject.isProvisiongProgress()).isFalse();
    }
}