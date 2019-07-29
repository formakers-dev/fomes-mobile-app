package com.formakers.fomes.common.noti;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.main.view.MainActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ChannelManagerTest {

    @Mock AnalyticsModule.Analytics mockAnalytics;

    ChannelManager subject;

    @Before
    public void setUp() throws Exception {

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.trampoline());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.trampoline();
            }
        });

        MockitoAnnotations.initMocks(this);

        subject = new ChannelManager(ApplicationProvider.getApplicationContext(), mockAnalytics);
    }

    @Ignore
    @Test
    public void sendNotification_호출시__전달된_정보대로_알림을_띄운다() {
    }

    @Test
    public void sendNotification_호출시__이벤트_로깅을_전송한다() {
        Map<String, String> notificationDataMap = new HashMap<>();
        notificationDataMap.put(FomesConstants.Notification.CHANNEL, "channel_betatest");
        notificationDataMap.put(FomesConstants.Notification.TITLE, "타이틀");
        notificationDataMap.put(FomesConstants.Notification.SUB_TITLE, "서브타이틀");
        notificationDataMap.put(FomesConstants.Notification.MESSAGE, "메세지");
        notificationDataMap.put(FomesConstants.Notification.IS_SUMMARY, "false");
        notificationDataMap.put(FomesConstants.Notification.SUMMARY_SUB_TEXT, "서머리서브텍스트");
        notificationDataMap.put(FomesConstants.Notification.DEEPLINK, "딥링크");

        subject.sendNotification(notificationDataMap, MainActivity.class);

        verify(mockAnalytics).sendNotificationEventLog(
                eq(FomesConstants.Notification.Log.ACTION_RECEIVE),
                eq(ChannelManager.Channel.BETATEST),
                eq("타이틀")
        );
    }

    @Test
    public void onNotificationClick_호출시__이벤트_로깅을_전송한다() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "타이틀");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);
        bundle.putString(FomesConstants.Notification.DEEPLINK, "deeplink");

        subject.onNotificationClick(bundle);

        verify(mockAnalytics).sendNotificationEventLog(
                eq(FomesConstants.Notification.Log.ACTION_OPEN),
                eq(ChannelManager.Channel.BETATEST),
                eq("타이틀")
        );
    }

    @Test
    public void onNotificationClick_호출시__deeplink필드가_존재하면__수행해야할_액션을_리턴한다() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "타이틀");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);
        bundle.putString(FomesConstants.Notification.DEEPLINK, "deeplink");

        Intent destIntent = subject.onNotificationClick(bundle);

        assertThat(destIntent.getData()).isNotNull();
        assertThat(destIntent.getData().toString()).isEqualTo("deeplink");
    }

    @Test
    public void onNotificationClick_호출시__deeplink필드가_없으면__이동해야할_화면을_리턴한다() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "타이틀");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);

        Intent destIntent = subject.onNotificationClick(bundle);

        assertThat(destIntent.getData()).isNull();
        assertThat(destIntent.getComponent()).isNotNull();
        assertThat(destIntent.getComponent().getClassName()).contains(MainActivity.class.getSimpleName());
    }

    @Test
    public void onNotificationCancel_호출시__이벤트_로깅을_전송한다() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "타이틀");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);
        bundle.putString(FomesConstants.Notification.DEEPLINK, "deeplink");

        subject.onNotificationCancel(bundle);

        verify(mockAnalytics).sendNotificationEventLog(
                eq(FomesConstants.Notification.Log.ACTION_DISMISS),
                eq(ChannelManager.Channel.BETATEST),
                eq("타이틀")
        );
    }
}