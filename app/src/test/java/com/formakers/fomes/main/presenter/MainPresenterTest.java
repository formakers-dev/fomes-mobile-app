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

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    TestScheduler testScheduler;

    @Before
    public void setUp() throws Exception {
        testScheduler = new TestScheduler();

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> testScheduler);

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

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

    @Test
    public void startEventBannerAutoSlide_호출시__3초마다_이벤트배너를_갱신한다() {
        subject.startEventBannerAutoSlide();

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        verify(mockView, never()).showNextEventBanner();

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        verify(mockView).showNextEventBanner();

        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        verify(mockView, times(2)).showNextEventBanner();
    }
}