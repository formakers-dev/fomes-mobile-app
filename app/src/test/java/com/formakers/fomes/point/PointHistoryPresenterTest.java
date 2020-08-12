package com.formakers.fomes.point;

import com.formakers.fomes.common.model.FomesPoint;
import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.point.history.PointHistoryContract;
import com.formakers.fomes.point.history.PointHistoryListAdapterContract;
import com.formakers.fomes.point.history.PointHistoryPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PointHistoryPresenterTest {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Mock PointHistoryContract.View mockView;
    @Mock PointHistoryListAdapterContract.Model mockAdapterModel;
    @Mock PointService mockPointService;

    PointHistoryPresenter subject;

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

        when(mockPointService.getAvailablePoint()).thenReturn(Single.just(3000L));

        List<FomesPoint> pointList = new ArrayList<>();

        pointList.add(new FomesPoint().setPoint(1000L).setDate(dateFormat.parse("2020-06-01")));
        pointList.add(new FomesPoint().setPoint(2000L).setDate(dateFormat.parse("2020-09-01")));
        pointList.add(new FomesPoint().setPoint(3000L).setDate(dateFormat.parse("2020-07-01")));
        when(mockPointService.getPointHistory()).thenReturn(Observable.from(pointList));

        subject = new PointHistoryPresenter(mockView, mockPointService);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트를_가져와서__뷰에_셋팅한다() {
        subject.bindAvailablePoint();

        verify(this.mockView).setAvailablePoint(3000L);
    }

    @Test
    public void bindHistory_호출시__포인트_내역을_가져와서__리스트에_셋팅한다() throws Exception {
        subject.bindHistory();

        ArgumentCaptor<List<FomesPoint>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel).addAll(argumentCaptor.capture());
        List<FomesPoint> actual = argumentCaptor.getValue();

        assertThat(actual.get(0).getPoint()).isEqualTo(2000L);
        assertThat(actual.get(0).getDate()).isEqualTo(dateFormat.parse("2020-09-01"));
        assertThat(actual.get(1).getPoint()).isEqualTo(3000L);
        assertThat(actual.get(1).getDate()).isEqualTo(dateFormat.parse("2020-07-01"));
        assertThat(actual.get(2).getPoint()).isEqualTo(1000L);
        assertThat(actual.get(2).getDate()).isEqualTo(dateFormat.parse("2020-06-01"));

        verify(mockView).refreshHistory();
    }

    @Test
    public void bindHistory_호출시__로딩상태를_표시한다() {
        subject.bindHistory();

        verify(mockView).showLoading();
        verify(mockView).hideLoading();
    }

    @Test
    public void bindHistory_호출시__포인트내역이_없으면__내역없음_화면을_보여준다() {
        when(mockPointService.getPointHistory()).thenReturn(Observable.empty());

        subject.bindHistory();

        verify(mockView).hideLoading();
        verify(mockView).showEmpty();
    }
}