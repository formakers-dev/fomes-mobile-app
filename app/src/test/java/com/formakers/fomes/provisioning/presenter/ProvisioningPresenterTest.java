package com.formakers.fomes.provisioning.presenter;

import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.provisioning.view.CurrentAnalysisReportActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import rx.Completable;

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
        MockitoAnnotations.initMocks(this);

        subject = new ProvisioningPresenter(mockView, mockUser, mockUserService, mockAppBeeAndroidNativeHelper);
    }

    @After
    public void tearDown() throws Exception {
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
    public void emitGrantedEvent__권한_허용_이벤트_발생시__뷰에_화면을_이동도록_요청한다() {
        subject.emitGrantedEvent(true);

        verify(mockView).startActivityAndFinish(eq(CurrentAnalysisReportActivity.class));
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