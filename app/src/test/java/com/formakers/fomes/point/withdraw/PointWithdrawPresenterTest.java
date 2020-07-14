package com.formakers.fomes.point.withdraw;

import com.formakers.fomes.common.network.PointService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PointWithdrawPresenterTest {

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

        subject = new PointWithdrawPresenter(mockView, mockPointService);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트를_가져와서__뷰에_셋팅한다() {
        subject.bindAvailablePoint();

        verify(this.mockView).setAvailablePoint(3000L);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트가_5000원이상이면__뷰의_입력컴포넌트를_활성화한다() {
        when(mockPointService.getAvailablePoint()).thenReturn(Single.just(5000L));

        subject.bindAvailablePoint();

        verify(this.mockView).setInputComponentsEnabled(true);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트가_5000원미만이면__뷰의_입력컴포넌트를_비활성화한다() {
        subject.bindAvailablePoint();

        verify(this.mockView).setInputComponentsEnabled(false);
    }

    @Test
    public void bindAvailablePoint_호출시__뷰에_최대입력가능매수를_5000원단위로_세팅한다() {
        for(int i=0; i<5; i++) {
            reset(this.mockView);
            long availablePoint = 5000L * i + i;
            when(mockPointService.getAvailablePoint()).thenReturn(Single.just(availablePoint));

            subject.bindAvailablePoint();

            int expectedMaxCount = (int)(availablePoint / 5000);
            verify(this.mockView).setMaxWithdrawCount(expectedMaxCount);
        }
    }
}