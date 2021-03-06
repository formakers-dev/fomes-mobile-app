package com.formakers.fomes.betatest;

import android.content.Context;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.helper.ShareHelper;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

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

    @Mock private Context mockContext;
    @Mock private BetaTestContract.View mockView;
    @Mock private BetaTestListAdapter mockAdapterModel;
    @Mock private BetaTestService mockBetaTestService;
    @Mock private EventLogService mockEventLogService;
    @Mock private AnalyticsModule.Analytics mockAnalytics;
    @Mock private FomesUrlHelper mockFomesUrlHelper;
    @Mock private ImageLoader mockImageLoader;
    @Mock private ShareHelper mockShareHelper;
    @Mock private FirebaseRemoteConfig mockRemoteConfig;

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

        betaTests.add(new BetaTest().setId("1").setTitle("???????????????1").setCloseDate(new Date()).setAttended(true).setCompleted(false));
        betaTests.add(new BetaTest().setId("2").setTitle("???????????????2").setCloseDate(new Date()).setAttended(false).setCompleted(false));
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(betaTests));

        when(mockAdapterModel.getItem(0)).thenReturn(betaTests.get(0));
        when(mockAdapterModel.getItem(1)).thenReturn(betaTests.get(1));
        when(mockAdapterModel.getPositionById(anyString())).thenReturn(-1);
        when(mockAdapterModel.getPositionById("1")).thenReturn(0);
        when(mockAdapterModel.getPositionById("2")).thenReturn(1);

        subject = new BetaTestPresenter(mockContext, mockView, mockBetaTestService, mockEventLogService, mockAnalytics, mockFomesUrlHelper, mockImageLoader, mockShareHelper, Single.just(dummyUser.getNickName()), mockRemoteConfig);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void initialize__?????????__???????????????_????????????() {
        subject.initialize();

        verify(mockView).setUserNickName(eq("dummyNickName"));
    }

    @Test
    public void loadToBetaTestList__?????????__????????????_????????????_????????????() {
        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
    }

    @Test
    public void loadToBetaTestList__?????????__?????????_??????_????????????_????????????_?????????_?????????_?????????_????????????() {
        List<BetaTest> unsortedBetaTestList = new ArrayList();
        // ????????????
        unsortedBetaTestList.add(new BetaTest().setId("1").setAttended(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-30T00:00:00.000Z"))));
        unsortedBetaTestList.add(new BetaTest().setId("2").setAttended(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-31T00:00:00.000Z"))));

        // ?????? ?????? - ?????????
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
    public void loadToBetaTestList__?????????__???????????????_???_????????????__????????????_?????????_?????????_?????????_????????????() {
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.just(Lists.emptyList()));

        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
        verify(mockView).showEmptyView();
    }

    @Test
    public void loadToBetaTestList__?????????__?????????_??????__????????????_?????????_?????????_?????????_????????????() {
        when(mockBetaTestService.getBetaTestList()).thenReturn(Single.error(new Throwable()));

        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getBetaTestList();
        verify(mockView).showEmptyView();
    }


    @Test
    public void getBetaTestItem__?????????__??????_?????????_??????????????????_????????????() {
        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());
        BetaTest betaTest = subject.getBetaTestItem(0);

        assertThat(betaTest.getTitle()).isEqualTo("???????????????1");
    }

    @Test
    public void getBetaTestPostitionById__?????????__??????_????????????_?????????_????????????_????????????() {
        subject.loadToBetaTestList(new Date()).subscribe(new TestSubscriber<>());

        assertThat(subject.getBetaTestPostitionById("1")).isEqualTo(0);
        assertThat(subject.getBetaTestPostitionById("2")).isEqualTo(1);
    }


    @Test
    public void sendEventLog_?????????__????????????_?????????_??????_???????????????_?????????_????????????() {
        when(mockEventLogService.sendEventLog(any())).thenReturn(Completable.complete());

        subject.sendEventLog("ANY_CODE", "ANY_REF");

        ArgumentCaptor<EventLog> eventLogCaptor = ArgumentCaptor.forClass(EventLog.class);

        verify(mockEventLogService).sendEventLog(eventLogCaptor.capture());
        assertEquals(eventLogCaptor.getValue().getCode(), "ANY_CODE");
        assertEquals(eventLogCaptor.getValue().getRef(), "ANY_REF");
    }

    @Test
    public void getInterpretedUrl_?????????__????????????_?????????_?????????_URL???_????????????() {
        subject.getInterpretedUrl("http://www.naver.com?email={email}");

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.naver.com?email={email}"));
    }

    @Test
    public void requestBetaTestProgress_?????????__????????????_??????_??????????????????() {
        BetaTestAPI.BetaTestProgressResponseVO responseVO = new BetaTestAPI.BetaTestProgressResponseVO();
        responseVO.isAttended = true;
        responseVO.isCompleted = false;

        when(mockBetaTestService.getBetaTestProgress("1", false))
                .thenReturn(Single.just(responseVO));

        subject.requestBetaTestProgress("1");

        verify(mockView).refreshBetaTestProgress(0);
    }
    @Test
    public void requestBetaTestProgress_?????????__??????_????????????_?????????__????????????_???????????????() {
        BetaTestAPI.BetaTestProgressResponseVO responseVO = new BetaTestAPI.BetaTestProgressResponseVO();
        responseVO.isAttended = true;
        responseVO.isCompleted = false;

        when(mockBetaTestService.getBetaTestProgress("99999999999", false))
                .thenReturn(Single.just(responseVO));

        subject.requestBetaTestProgress("99999999999");

        verify(mockView, never()).refreshBetaTestProgress(anyInt());
    }

    @Test
    public void shareToKakao_?????????__???????????????_?????????_????????????() {
        BetaTest betaTest = new BetaTest();

        subject.shareToKaKao(betaTest);

        verify(mockShareHelper).sendBetaTestToKaKao(eq(mockContext), eq(betaTest));
    }
}