package com.formakers.fomes.main.presenter;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.contract.EventPagerAdapterContract;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.model.User;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterTest {

    @Inject UserDAO mockUserDAO;
    @Inject UserService mockUserService;
    @Inject JobManager mockJobManager;
    @Inject EventLogService mockEventLogService;
    @Inject SharedPreferencesHelper mockSharedPreferenceHelper;
    @Inject PostService mockPostService;
    @Mock EventPagerAdapterContract.Model mockEventPagerAdapterModel;

    @Mock
    MainContract.View mockView;

    private MainPresenter subject;

    private TestScheduler testScheduler;

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
        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);

        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(new User("email", "notiToken")));
        when(mockPostService.getPromotions()).thenReturn(Single.just(Lists.newArrayList(new Post())));
        when(mockEventPagerAdapterModel.getCount()).thenReturn(3);

        subject = new MainPresenter(mockView, mockUserDAO, mockUserService, mockPostService, mockEventLogService, mockJobManager, mockSharedPreferenceHelper);

        subject.setEventPagerAdapterModel(mockEventPagerAdapterModel);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getUserInfo_호출시__이미저장된_유저정보가_없을때에만__DB에_유저정보를_요청한다() {
        subject.getUserInfo();
        subject.getUserInfo();

        verify(mockUserDAO, times(1)).getUserInfo();
    }

    @Test
    public void requestVerifyAccessToken_호출시__토큰_검증을_요청한다() {
        subject.requestVerifyAccessToken();

        verify(mockUserService).verifyToken();
    }

    @Test
    public void registerSendDataJob_호출시__단기통계데이터전송_작업을_등록한다() {
        subject.registerSendDataJob();

        verify(mockJobManager).registerSendDataJob(eq(JobManager.JOB_ID_SEND_DATA));
    }

    @Test
    public void checkRegisteredSendDataJob_호출시__단기통계데이터전송_작업의_등록여부를_반환한다() {
        when(mockJobManager.isRegisteredJob(JobManager.JOB_ID_SEND_DATA)).thenReturn(true);

        boolean isRegistered = subject.checkRegisteredSendDataJob();

        verify(mockJobManager).isRegisteredJob(eq(JobManager.JOB_ID_SEND_DATA));
        assertThat(isRegistered).isTrue();
    }

    @Test
    public void requestPromotions_호출시__이벤트배너를_요청한다() {
        subject.requestPromotions();

        ArgumentCaptor<List<Post>> postArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockEventPagerAdapterModel).addAll(postArgumentCaptor.capture());
        verify(mockView).refreshEventPager();

        List<Post> actualPosts = postArgumentCaptor.getValue();

    }

    @Test
    public void getPromotionCount_호출시__이벤트배너의_개수를_반환한다() {
        assertThat(subject.getPromotionCount()).isEqualTo(3);
    }

    @Test
    public void sendEventLog_호출시__전달받은_내용에_대한_이벤트로그_저장을_요청한다() {
        when(mockEventLogService.sendEventLog(any())).thenReturn(Completable.complete());

        subject.sendEventLog("ANY_CODE");

        ArgumentCaptor<EventLog> eventLogCaptor = ArgumentCaptor.forClass(EventLog.class);

        verify(mockEventLogService).sendEventLog(eventLogCaptor.capture());
        assertEquals(eventLogCaptor.getValue().getCode(), "ANY_CODE");
    }
}