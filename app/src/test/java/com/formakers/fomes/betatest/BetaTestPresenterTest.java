package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BetaTestPresenterTest {

    @Mock private BetaTestContract.View mockView;
    @Mock private BetaTestListAdapter mockAdapterModel;
    @Mock private BetaTestService mockBetaTestService;
    @Mock private EventLogService mockEventLogService;
    @Mock private AnalyticsModule.Analytics mockAnalytics;
    @Mock private FomesUrlHelper mockFomesUrlHelper;
    @Mock private ImageLoader mockImageLoader;

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

        betaTests.add(new BetaTest().setId("1").setTitle("베타테스트1").setCloseDate(new Date()).setAttended(true).setCompleted(false));
        betaTests.add(new BetaTest().setId("2").setTitle("베타테스트2").setCloseDate(new Date()).setAttended(false).setCompleted(false));
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(betaTests));

        when(mockAdapterModel.getItem(0)).thenReturn(betaTests.get(0));
        when(mockAdapterModel.getItem(1)).thenReturn(betaTests.get(1));
        when(mockAdapterModel.getPositionById(anyString())).thenReturn(-1);
        when(mockAdapterModel.getPositionById("1")).thenReturn(0);
        when(mockAdapterModel.getPositionById("2")).thenReturn(1);

        subject = new BetaTestPresenter(mockView, mockBetaTestService, mockEventLogService, mockAnalytics, mockFomesUrlHelper, mockImageLoader, Single.just(dummyUser.getNickName()));
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void initialize__호출시__유저정보를_가져온다() {
        subject.initialize();

        verify(mockView).setUserNickName(eq("dummyNickName"));
    }

    @Test
    public void loadToBetaTestList__호출시__테스트존_리스트를_요청한다() {
        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
    }

    @Test
    public void loadToBetaTestList__호출시__결과로_받은_테스트존_리스트를_정렬된_순서로_화면에_보여준다() {
        List<BetaTest> unsortedBetaTestList = new ArrayList();
        // 참여가능
        unsortedBetaTestList.add(new BetaTest().setId("1").setAttended(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-30T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId("2").setAttended(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-31T00:00:00.000Z"))));

        // 참여 완료 - 미종료
        unsortedBetaTestList.add(new BetaTest().setId("3").setAttended(true).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-12-28T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId("4").setAttended(true).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-12-29T00:00:00.000Z"))));

        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(unsortedBetaTestList));

        subject.loadToBetaTestList(Date.from(Instant.parse("2018-12-01T00:00:00.000Z"))).subscribe(new TestSubscriber<>());

        ArgumentCaptor<List<BetaTest>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel).clear();
        verify(mockAdapterModel, times(2)).addAll(captor.capture());
        List<BetaTest> sortedBetaTestList = captor.getAllValues().get(0);
        sortedBetaTestList.addAll(captor.getAllValues().get(1));

        assertThat(sortedBetaTestList.get(0).getId()).isEqualTo("1");
        assertThat(sortedBetaTestList.get(1).getId()).isEqualTo("2");
        assertThat(sortedBetaTestList.get(2).getId()).isEqualTo("3");
        assertThat(sortedBetaTestList.get(3).getId()).isEqualTo("4");

        verify(mockView).refreshBetaTestList();
        verify(mockView).showBetaTestListView();
    }

    @Test
    public void loadToBetaTestList__호출시__빈리스트가_올_경우에는__비었다는_내용을_알리는_화면을_보여준다() {
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(Lists.emptyList()));

        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
        verify(mockView).showEmptyView();
    }

    @Test
    public void loadToBetaTestList__호출시__에러일_경우__비었다는_내용을_알리는_화면을_보여준다() {
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.error(new Throwable()));

        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
        verify(mockView).showEmptyView();
    }


    @Test
    public void getBetaTestItem__호출시__해당_위치의_베타테스트를_리턴한다() {
        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());
        BetaTest betaTest = subject.getBetaTestItem(0);

        assertThat(betaTest.getTitle()).isEqualTo("베타테스트1");
    }

    @Test
    public void getBetaTestPostitionById__호출시__해당_아이디의_리스트_포지션을_리턴한다() {
        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        assertThat(subject.getBetaTestPostitionById("1")).isEqualTo(0);
        assertThat(subject.getBetaTestPostitionById("2")).isEqualTo(1);
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

    @Test
    public void getInterpretedUrl_호출시__예약어를_해석한_새로운_URL을_반환한다() {
        subject.getInterpretedUrl("http://www.naver.com?email={email}");

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.naver.com?email={email}"));
    }

    @Test
    public void requestBetaTestProgress_호출시__진행상태_뷰를_업데이트한다() {
        BetaTestAPI.BetaTestProgressResponseVO responseVO = new BetaTestAPI.BetaTestProgressResponseVO();
        responseVO.isAttended = true;
        responseVO.isCompleted = false;

        when(mockBetaTestService.getBetaTestProgress("1", false))
                .thenReturn(Single.just(responseVO));

        subject.requestBetaTestProgress("1");

        verify(mockView).refreshBetaTestProgress(0);
    }
    @Test
    public void requestBetaTestProgress_호출시__해당_테스트가_없으면__아무것도_하지않는다() {
        BetaTestAPI.BetaTestProgressResponseVO responseVO = new BetaTestAPI.BetaTestProgressResponseVO();
        responseVO.isAttended = true;
        responseVO.isCompleted = false;

        when(mockBetaTestService.getBetaTestProgress("99999999999", false))
                .thenReturn(Single.just(responseVO));

        subject.requestBetaTestProgress("99999999999");

        verify(mockView, never()).refreshBetaTestProgress(anyInt());
    }
}