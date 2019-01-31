package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.main.adapter.BetaTestListAdapter;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.model.User;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Completable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BetaTestPresenterTest {

    @Mock private BetaTestContract.View mockView;
    @Mock private BetaTestListAdapter mockAdapterModel;
    @Mock private BetaTestService mockBetaTestService;
    @Mock private UserDAO mockUserDAO;
    @Mock private EventLogService mockEventLogService;

    private User dummyUser;
    private List<BetaTest> betaTests = new ArrayList<>();
    private BetaTestPresenter subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        MockitoAnnotations.initMocks(this);

        dummyUser = new User().setEmail("user@gmail.com").setNickName("dummyNickName");
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(dummyUser));

        betaTests.add(new BetaTest().setTitle("베타테스트1").setCloseDate(new Date()));
        betaTests.add(new BetaTest().setTitle("베타테스트2").setCloseDate(new Date()));
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(betaTests));

        when(mockAdapterModel.getItem(0)).thenReturn(betaTests.get(0));
        when(mockAdapterModel.getItem(1)).thenReturn(betaTests.get(1));

        subject = new BetaTestPresenter(mockView, mockBetaTestService, mockEventLogService, mockUserDAO);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void initialize__호출시__유저정보를_가져온다() {
        subject.initialize();

        verify(mockUserDAO).getUserInfo();
        verify(mockView).setUserNickName(eq("dummyNickName"));
    }

    @Test
    public void loadToBetaTestList__호출시__테스트존_리스트를_요청한다() {
        subject.loadToBetaTestList().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
    }

    @Test
    public void loadToBetaTestList__호출시__결과로_받은_테스트존_리스트를_정렬된_순서로_화면에_보여준다() {
        List<BetaTest> unsortedBetaTestList = new ArrayList();
        unsortedBetaTestList.add(new BetaTest().setId(3).setOpened(true).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-12-28T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId(2).setOpened(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-31T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId(5).setOpened(false).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-03-01T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId(7).setOpened(false).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-11-01T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId(4).setOpened(true).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-12-29T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId(1).setOpened(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-30T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId(8).setOpened(false).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-11-30T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId(6).setOpened(false).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-03-30T00:00:00.000Z"))));
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(unsortedBetaTestList));

        subject.loadToBetaTestList().subscribe(new TestSubscriber<>());

        ArgumentCaptor<List<BetaTest>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel).clear();
        verify(mockAdapterModel).addAll(captor.capture());

        List<BetaTest> sortedBetaTestList = captor.getValue();
        assertThat(sortedBetaTestList.get(0).getId()).isEqualTo(1);
        assertThat(sortedBetaTestList.get(1).getId()).isEqualTo(2);
        assertThat(sortedBetaTestList.get(2).getId()).isEqualTo(3);
        assertThat(sortedBetaTestList.get(3).getId()).isEqualTo(4);
        assertThat(sortedBetaTestList.get(4).getId()).isEqualTo(5);
        assertThat(sortedBetaTestList.get(5).getId()).isEqualTo(6);
        assertThat(sortedBetaTestList.get(6).getId()).isEqualTo(7);
        assertThat(sortedBetaTestList.get(7).getId()).isEqualTo(8);

        verify(mockView).refreshBetaTestList();
        verify(mockView).showBetaTestListView();
    }

    @Test
    public void loadToBetaTestList__호출시__빈리스트가_올_경우에는__비었다는_내용을_알리는_화면을_보여준다() {
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(Lists.emptyList()));

        subject.loadToBetaTestList().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
        verify(mockView).showEmptyView();
    }

    @Test
    public void loadToBetaTestList__호출시__에러일_경우__비었다는_내용을_알리는_화면을_보여준다() {
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.error(new Throwable()));

        subject.loadToBetaTestList().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
        verify(mockView).showEmptyView();
    }


    @Test
    public void getBetaTestItem__호출시__해당_위치의_베타테스트를_리턴한다() {
        subject.loadToBetaTestList().subscribe(new TestSubscriber<>());
        BetaTest betaTest = subject.getBetaTestItem(0);

        assertThat(betaTest.getTitle()).isEqualTo("베타테스트1");
    }

    @Test
    public void getUserEmail__호출시__유저의_이메일을_리턴한다() {
        subject.initialize();
        String userEmail = subject.getUserEmail();

        assertThat(userEmail).isEqualTo("user@gmail.com");
    }

    @Test
    public void sendEventLog_호출시__전달받은_내용에_대한_이벤트로그_저장을_요청한다() {
        when(mockEventLogService.sendEventLog(any())).thenReturn(Completable.complete());

        subject.sendEventLog("ANY_CODE", "ANY_REF");

        ArgumentCaptor<EventLog> eventLogCaptor = ArgumentCaptor.forClass(EventLog.class);

        verify(mockEventLogService).sendEventLog(eventLogCaptor.capture());
        assertEquals(eventLogCaptor.getValue().getCode(), "ANY_CODE");
        assertEquals(eventLogCaptor.getValue().getRef(), "ANY_REF");
    }
}