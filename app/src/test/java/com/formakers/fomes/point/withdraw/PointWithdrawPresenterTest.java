package com.formakers.fomes.point.withdraw;

import com.formakers.fomes.common.model.FomesPoint;
import com.formakers.fomes.common.network.PointService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Completable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
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
        when(mockPointService.requestWithdraw(any(FomesPoint.class))).thenReturn(Completable.complete());

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

    @Test
    public void withdraw_호출시__입력된_수만큼의_문상_교환을_신청하는_API를_호출한다() {
        subject.withdraw(2, "010-1111-2222");

        ArgumentCaptor<FomesPoint> captor = ArgumentCaptor.forClass(FomesPoint.class);
        verify(mockPointService).requestWithdraw(captor.capture());

        FomesPoint actualPoint = captor.getValue();
        assertThat(actualPoint.getPoint()).isEqualTo(10000);
        assertThat(actualPoint.getDescription()).isEqualTo("5000원권 2장 교환");
        assertThat(actualPoint.getPhoneNumber()).isEqualTo("010-1111-2222");

        verify(mockView).showToast(contains("완료"));
        verify(mockView).finish();
    }

    @Test
    public void withdraw_호출시__API요청에_실패하면__에러메시지를_view에_띄운다() {
        when(mockPointService.requestWithdraw(any(FomesPoint.class))).thenReturn(Completable.error(new IllegalArgumentException()));

        subject.withdraw(2, "010-1111-2222");

        verify(mockView).showToast(contains("실패"));
        verify(mockView, never()).finish();
    }
}