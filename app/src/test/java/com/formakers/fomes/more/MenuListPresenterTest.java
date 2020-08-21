package com.formakers.fomes.more;

import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.PointService;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MenuListPresenterTest {

    @Mock
    MenuListContract.View mockView;
    @Mock
    BetaTestService mockBetaTestService;
    @Mock
    PointService mockPointService;
    @Mock
    FirebaseRemoteConfig mockRemoteConfig;

    MenuListPresenter subject;

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

        when(mockBetaTestService.getCompletedBetaTestsCount()).thenReturn(Single.just(3));
        when(mockPointService.getAvailablePoint()).thenReturn(Single.just(3000L));

        subject = new MenuListPresenter(mockView,
                Single.just("email"), Single.just("nickName"),
                mockBetaTestService,
                mockPointService,
                mockRemoteConfig);
    }

    @Test
    public void bindUserInfo_호출시__유저정보를_가져와서__뷰에_셋팅한다() {
        subject.bindUserInfo();

        verify(this.mockView).setUserInfo(eq("email"), eq("nickName"));
    }

    @Test
    public void bindCompletedBetaTestsCount_호출시__총_참여횟수를_가져와서__뷰에_셋팅한다() {
        subject.bindCompletedBetaTestsCount();

        verify(this.mockView).setCompletedBetaTestsCount(3);
    }

    @Test
    public void bindAvailablePoint_호출시__포인트시스템_ON이면__총_가용_포인트를_가져와서__뷰에_셋팅한다() {
        subject.bindAvailablePoint();

        verify(this.mockView).setAvailablePoint(3000L);
    }
}