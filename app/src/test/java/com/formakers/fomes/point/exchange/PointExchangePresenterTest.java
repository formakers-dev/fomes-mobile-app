package com.formakers.fomes.point.exchange;

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

public class PointExchangePresenterTest {

    @Mock PointExchangeContract.View mockView;
    @Mock PointService mockPointService;

    PointExchangePresenter subject;

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

        when(mockPointService.getAvailablePoint()).thenReturn(Single.just(5000L));
        when(mockPointService.requestExchange(any(FomesPoint.class))).thenReturn(Completable.complete());

        subject = new PointExchangePresenter(mockView, mockPointService);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트를_가져와서__뷰에_셋팅한다() {
        subject.bindAvailablePoint();

        verify(this.mockView).setAvailablePoint(5000L);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트가_5000원이상이면__뷰의_입력컴포넌트를_활성화한다() {
        subject.bindAvailablePoint();

        verify(this.mockView).setInputComponentsEnabled(true);
    }

    @Test
    public void bindAvailablePoint_호출시__총_가용_포인트가_5000원미만이면__뷰의_입력컴포넌트를_비활성화한다() {
        when(mockPointService.getAvailablePoint()).thenReturn(Single.just(3000L));

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
            verify(this.mockView).setMaxExchangeCount(expectedMaxCount);
        }
    }

    @Test
    public void exchange_호출시__입력된_수만큼의_문상_교환을_신청하는_API를_호출한다() {
        subject.exchange(2, "010-1111-2222");

        ArgumentCaptor<FomesPoint> captor = ArgumentCaptor.forClass(FomesPoint.class);
        verify(mockPointService).requestExchange(captor.capture());

        FomesPoint actualPoint = captor.getValue();
        assertThat(actualPoint.getPoint()).isEqualTo(10000);
        assertThat(actualPoint.getDescription()).isEqualTo("문화상품권 5000원권 2장 교환");
        assertThat(actualPoint.getPhoneNumber()).isEqualTo("010-1111-2222");

        verify(mockView).showToast(contains("완료"));
        verify(mockView).successfullyFinish();
    }

    @Test
    public void exchange_호출시__API요청에_실패하면__에러메시지를_view에_띄운다() {
        when(mockPointService.requestExchange(any(FomesPoint.class))).thenReturn(Completable.error(new IllegalArgumentException()));

        subject.exchange(2, "010-1111-2222");

        verify(mockView).showToast(contains("실패"));
        verify(mockView, never()).successfullyFinish();
    }

    @Test
    public void isAvailableToExchange_호출시__출금가능여부를_반환한다() {
        subject.bindAvailablePoint();

        boolean actual = subject.isAvailableToExchange(1, "010-111-2222");

        assertThat(actual).isTrue();
    }

    @Test
    public void isAvailableToExchange_호출시__출금가능여부를_반환한다__예외상황() {
        subject.bindAvailablePoint();

        // currentExchangeCount <= min
        assertThat(subject.isAvailableToExchange(0, "010-111-2222")).isFalse();
        // currentExchangeCount > max
        assertThat(subject.isAvailableToExchange(2, "010-111-2222")).isFalse();
        // wrong phone number
        assertThat(subject.isAvailableToExchange(1, "99")).isFalse();
    }
}