package com.formakers.fomes.betatest;

import com.formakers.fomes.R;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.Mission;

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

        subject = new FinishedBetaTestPresenter(mockView, mockBetaTestService, mockEventLogService, mockAnalytics, mockImageLoader, mockFomesUrlHelper, mockAndroidNativeHelper);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void load__호출시__종료된_테스트_리스트를_요청한다() {
        subject.load().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getFinishedBetaTestList();
    }

    @Test
    public void load__호출시__결과로_받은_테스트존_리스트를_정렬된_순서로_화면에_보여준다() {
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
    public void load__호출시__참여완료_필터가_설정되어있는_경우__참여완료한_리스트만_정렬해서_보여준다() {
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
    public void load__호출시__빈리스트가_올_경우에는__비었다는_내용을_알리는_화면을_보여준다() {
        when(mockBetaTestService.getFinishedBetaTestList()).thenReturn(Single.just(Lists.emptyList()));

        subject.load().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getFinishedBetaTestList();
        verify(mockView).showEmptyView();
    }

    @Test
    public void load__호출시__에러일_경우__비었다는_내용을_알리는_화면을_보여준다() {
        when(mockBetaTestService.getFinishedBetaTestList()).thenReturn(Single.error(new Throwable()));

        subject.load().subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).getFinishedBetaTestList();
        verify(mockView).showEmptyView();
    }

    @Test
    public void applyCompletedFilter__호출시__참여완료_필터가_설정된_경우__참여완료한_테스트만_보여준다() {
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
    public void applyCompletedFilter__호출시__참여완료_필터가_설정되지_않은_경우__종료된_모든_테스트를_보여준다() {
        subject.load().subscribe(new TestSubscriber<>());
        subject.applyCompletedFilter(false);

        ArgumentCaptor<List<BetaTest>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel, times(2)).clear();
        verify(mockAdapterModel, times(2)).addAll(captor.capture());
        List<BetaTest> filteredBetaTestList = captor.getAllValues().get(1);
        assertThat(filteredBetaTestList.size()).isEqualTo(4);
    }

    @Test
    public void getItem__호출시__해당_위치의_베타테스트를_리턴한다() {
        subject.load().subscribe(new TestSubscriber<>());
        BetaTest betaTest = subject.getItem(0);

        assertThat(betaTest.getId()).isEqualTo("1");
    }

    @Test
    public void emitRecheckMyAnswer_호출시__공지팝업을_띄운다() {
        Mission missionItem = new Mission().setTitle("test");

        subject.emitRecheckMyAnswer(missionItem);

        verify(mockView).showNoticePopup(eq(R.string.finished_betatest_recheck_my_answer_popup_title),
                eq(R.string.finished_betatest_recheck_my_answer_popup_subtitle),
                eq(R.drawable.notice_recheck_my_answer),
                eq(R.string.finished_betatest_recheck_my_answer_popup_positive_button_text),
                any());
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