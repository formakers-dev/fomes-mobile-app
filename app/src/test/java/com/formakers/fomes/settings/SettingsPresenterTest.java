package com.formakers.fomes.settings;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.noti.ChannelManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingsPresenterTest {

    @Mock SettingsContract.View mockView;
    @Mock ChannelManager mockChannerManager;
    @Mock AnalyticsModule.Analytics mockAnalytics;

    SettingsPresenter subject;

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

        subject = new SettingsPresenter(mockView, mockChannerManager, mockAnalytics);
    }

    @Test
    public void isSubscribedTopic_호출시__해당_주제_구독여부를_리턴한다() {
        subject.isSubscribedTopic("anyTopic");

        verify(mockChannerManager).isSubscribedTopic("anyTopic");
    }

    @Test
    public void toggleNotification_호출시__알림_설정이_꺼져있었다면__해당_주제를_구독한다() {
        when(mockChannerManager.isSubscribedTopic("anyTopic")).thenReturn(false);

        subject.toggleNotification("anyTopic");

        verify(mockChannerManager).subscribeTopic("anyTopic");
    }

    @Test
    public void toggleNotification_호출시__알림_설정이_켜져있었다면__해당_주제를_구독해제한다() {
        when(mockChannerManager.isSubscribedTopic("anyTopic")).thenReturn(true);

        subject.toggleNotification("anyTopic");

        verify(mockChannerManager).unsubscribeTopic("anyTopic");
    }
}