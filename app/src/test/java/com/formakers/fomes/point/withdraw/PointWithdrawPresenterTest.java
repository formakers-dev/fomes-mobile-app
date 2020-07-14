package com.formakers.fomes.point.withdraw;

import com.formakers.fomes.common.model.FomesPoint;
import com.formakers.fomes.common.network.PointService;

import org.junit.Before;
import org.junit.Test;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PointWithdrawPresenterTest {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Mock PointWithdrawContract.View mockView;
    @Mock PointService mockPointService;

    PointWithdrawPresenter subject;

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

        subject = new PointWithdrawPresenter(mockView, mockPointService);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트를_가져와서__뷰에_셋팅한다() {
        subject.bindAvailablePoint();

        verify(this.mockView).setAvailablePoint(3000L);
    }
}