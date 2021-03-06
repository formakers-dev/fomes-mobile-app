package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FinishedBetaTestPresenterTest {

    @Mock private FinishedBetaTestContract.View mockView;
    @Mock private FinishedBetaTestListAdapter mockAdapterModel;
    @Mock private BetaTestService mockBetaTestService;
    @Mock private EventLogService mockEventLogService;
    @Mock private AnalyticsModule.Analytics mockAnalytics;
    @Mock private ImageLoader mockImageLoader;
    @Mock private FomesUrlHelper mockFomesUrlHelper;
    @Mock private AndroidNativeHelper mockAndroidNativeHelper;
    @Mock private FirebaseRemoteConfig mockFirebaseRemoteConfig;

    private List<BetaTest> finishedBetaTests = new ArrayList<>();
    private FinishedBetaTestPresenter subject;

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

        finishedBetaTests.add(new BetaTest().setId("2").setAttended(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-30T00:00:00.000Z"))).setCurrentDate(Date.from(Instant.parse("2018-12-25T00:00:00.000Z"))));
        finishedBetaTests.add(new BetaTest().setId("1").setAttended(true).setCompleted(false).setCloseDate(Date.from(Instant.parse("2018-12-31T00:00:00.000Z"))).setCurrentDate(Date.from(Instant.parse("2018-12-25T00:00:00.000Z"))));
        finishedBetaTests.add(new BetaTest().setId("4").setAttended(true).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-12-28T00:00:00.000Z"))).setCurrentDate(Date.from(Instant.parse("2018-12-25T00:00:00.000Z"))));
        finishedBetaTests.add(new BetaTest().setId("3").setAttended(true).setCompleted(true).setCloseDate(Date.from(Instant.parse("2018-12-29T00:00:00.000Z"))).setCurrentDate(Date.from(Instant.parse("2018-12-25T00:00:00.000Z"))));
        when(mockBetaTestService.getFinishedBetaTestList()).thenReturn(Single.just(finishedBetaTests));

        when(mockView.isNeedAppliedCompletedFilter()).thenReturn(false);

        when(mockAdapterModel.getItem(0)).thenReturn(finishedBetaTests.get(1));
        when(mockAdapterModel.getItem(1)).thenReturn(finishedBetaTests.get(0));

        subject = new FinishedBetaTestPresenter(mockView, mockBetaTestService, mockEventLogService, mockAnalytics, mockImageLoader, mockFomesUrlHelper, mockAndroidNativeHelper, mockFirebaseRemoteConfig);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void load__?????????__?????????_?????????_????????????_????????????() {
        subject.load().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getFinishedBetaTestList();
    }

    @Test
    public void load__?????????__?????????_??????_????????????_????????????_?????????_?????????_?????????_????????????() {
        subject.load().subscribe(new TestSubscriber<>());

        ArgumentCaptor<List<BetaTest>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel).clear();
        verify(mockAdapterModel).addAll(captor.capture());
        List<BetaTest> sortedBetaTestList = captor.getAllValues().get(0);

        assertThat(sortedBetaTestList.get(0).getId()).isEqualTo("1");
        assertThat(sortedBetaTestList.get(1).getId()).isEqualTo("2");
        assertThat(sortedBetaTestList.get(2).getId()).isEqualTo("3");
        assertThat(sortedBetaTestList.get(3).getId()).isEqualTo("4");

        verify(mockView).refresh();
        verify(mockView).showListView();
    }

    @Test
    public void load__?????????__????????????_?????????_??????????????????_??????__???????????????_????????????_????????????_????????????() {
        when(mockView.isNeedAppliedCompletedFilter()).thenReturn(true);

        subject.load().subscribe(new TestSubscriber<>());

        ArgumentCaptor<List<BetaTest>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel).clear();
        verify(mockAdapterModel).addAll(captor.capture());
        List<BetaTest> sortedBetaTestList = captor.getAllValues().get(0);

        assertThat(sortedBetaTestList.get(0).getId()).isEqualTo("3");
        assertThat(sortedBetaTestList.get(1).getId()).isEqualTo("4");
    }

    @Test
    public void load__?????????__???????????????_???_????????????__????????????_?????????_?????????_?????????_????????????() {
        when(mockBetaTestService.getFinishedBetaTestList()).thenReturn(Single.just(Lists.emptyList()));

        subject.load().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getFinishedBetaTestList();
        verify(mockView).showEmptyView();
    }

    @Test
    public void load__?????????__?????????_??????__????????????_?????????_?????????_?????????_????????????() {
        when(mockBetaTestService.getFinishedBetaTestList()).thenReturn(Single.error(new Throwable()));

        subject.load().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getFinishedBetaTestList();
        verify(mockView).showEmptyView();
    }

    @Test
    public void applyCompletedFilter__?????????__????????????_?????????_?????????_??????__???????????????_????????????_????????????() {
        subject.load().subscribe(new TestSubscriber<>());
        subject.applyCompletedFilter(true);

        ArgumentCaptor<List<BetaTest>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel, times(2)).clear();
        verify(mockAdapterModel, times(2)).addAll(captor.capture());
        List<BetaTest> filteredBetaTestList = captor.getAllValues().get(1);
        assertThat(filteredBetaTestList.size()).isEqualTo(2);
        assertThat(filteredBetaTestList.get(0).isCompleted()).isTrue();
        assertThat(filteredBetaTestList.get(1).isCompleted()).isTrue();
    }

    @Test
    public void applyCompletedFilter__?????????__????????????_?????????_????????????_??????_??????__?????????_??????_????????????_????????????() {
        subject.load().subscribe(new TestSubscriber<>());
        subject.applyCompletedFilter(false);

        ArgumentCaptor<List<BetaTest>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel, times(2)).clear();
        verify(mockAdapterModel, times(2)).addAll(captor.capture());
        List<BetaTest> filteredBetaTestList = captor.getAllValues().get(1);
        assertThat(filteredBetaTestList.size()).isEqualTo(4);
    }

    @Test
    public void getItem__?????????__??????_?????????_??????????????????_????????????() {
        subject.load().subscribe(new TestSubscriber<>());
        BetaTest betaTest = subject.getItem(0);

        assertThat(betaTest.getId()).isEqualTo("1");
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
}