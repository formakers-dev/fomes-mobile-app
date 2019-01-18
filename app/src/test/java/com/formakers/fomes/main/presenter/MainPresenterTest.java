package com.formakers.fomes.main.presenter;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.common.repository.dao.UserDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainPresenterTest {

    @Inject UserDAO mockUserDAO;
    @Inject UserService mockUserService;
    @Inject JobManager mockJobManager;

    @Mock
    MainContract.View mockView;

    MainPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ((TestFomesApplication) RuntimeEnvironment.application).getComponent().inject(this);
        subject = new MainPresenter(mockView, mockUserDAO, mockUserService, mockJobManager);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void requestUserInfo_호출시__DB에_유저정보를_요청한다() {
        subject.requestUserInfo();

        verify(mockUserDAO).getUserInfo();
    }

    @Test
    public void requestVerifyAccessToken_호출시__토큰_검증을_요청한다() {
        subject.requestVerifyAccessToken();

        verify(mockUserService).verifyToken();
    }

    @Test
    public void checkRegisteredSendDataJob_호출시__단기통계데이터전송_작업의_등록여부를_반환한다() {
        when(mockJobManager.isRegisteredJob(JobManager.JOB_ID_SEND_DATA)).thenReturn(true);

        boolean isRegistered = subject.checkRegisteredSendDataJob();

        verify(mockJobManager).isRegisteredJob(eq(JobManager.JOB_ID_SEND_DATA));
        assertThat(isRegistered).isTrue();
    }
}