package com.formakers.fomes.provisioning.presenter;

import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ProvisioningPresenterTest {

    @Mock ProvisioningContract.View mockView;
    @Mock User mockUser;

    ProvisioningPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new ProvisioningPresenter(mockView, mockUser);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onNextPageEvent__다음_화면으로_넘어가는_이벤트_발생시__뷰에_다음페이지를_요청한다() {
        subject.onNextPageEvent();
        verify(mockView).nextPage();
    }

    @Test
    public void setUserInfo__호출시__유저정보를_셋한다() {
        subject.setUserInfo(1989, "developer", "male");

        verify(mockUser).setBirthday(eq(1989));
        verify(mockUser).setJob(eq("developer"));
        verify(mockUser).setGender(eq("male"));
    }
}