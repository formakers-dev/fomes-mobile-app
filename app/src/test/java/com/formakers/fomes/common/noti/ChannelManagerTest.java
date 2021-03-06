package com.formakers.fomes.common.noti;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

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
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;
    @Mock FirebaseMessaging mockFirebaseMessaging;

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

        subject = new ChannelManager(ApplicationProvider.getApplicationContext(), mockAnalytics, mockSharedPreferencesHelper, mockFirebaseMessaging);
    }

    @Ignore
    @Test
    public void sendNotification_?????????__?????????_????????????_?????????_?????????() {
    }

    @Test
    public void sendNotification_?????????__?????????_?????????_????????????() {
        Map<String, String> notificationDataMap = new HashMap<>();
        notificationDataMap.put(FomesConstants.Notification.CHANNEL, "channel_betatest");
        notificationDataMap.put(FomesConstants.Notification.TITLE, "?????????");
        notificationDataMap.put(FomesConstants.Notification.SUB_TITLE, "???????????????");
        notificationDataMap.put(FomesConstants.Notification.MESSAGE, "?????????");
        notificationDataMap.put(FomesConstants.Notification.IS_SUMMARY, "false");
        notificationDataMap.put(FomesConstants.Notification.SUMMARY_SUB_TEXT, "????????????????????????");
        notificationDataMap.put(FomesConstants.Notification.DEEPLINK, "?????????");

        subject.sendNotification(notificationDataMap, MainActivity.class);

        verify(mockAnalytics).sendNotificationEventLog(
                eq(FomesConstants.Notification.Log.ACTION_RECEIVE),
                eq(ChannelManager.Channel.BETATEST),
                eq("?????????")
        );
    }

    @Test
    public void onNotificationClick_?????????__?????????_?????????_????????????() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "?????????");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);
        bundle.putString(FomesConstants.Notification.DEEPLINK, "deeplink");

        subject.onNotificationClick(bundle);

        verify(mockAnalytics).sendNotificationEventLog(
                eq(FomesConstants.Notification.Log.ACTION_OPEN),
                eq(ChannelManager.Channel.BETATEST),
                eq("?????????")
        );
    }

    @Test
    public void onNotificationClick_?????????__deeplink?????????_????????????__???????????????_?????????_????????????() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "?????????");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);
        bundle.putString(FomesConstants.Notification.DEEPLINK, "deeplink");

        Intent destIntent = subject.onNotificationClick(bundle);

        assertThat(destIntent.getData()).isNotNull();
        assertThat(destIntent.getData().toString()).isEqualTo("deeplink");
    }

    @Test
    public void onNotificationClick_?????????__deeplink?????????_?????????__???????????????_?????????_????????????() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "?????????");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);

        Intent destIntent = subject.onNotificationClick(bundle);

        assertThat(destIntent.getData()).isNull();
        assertThat(destIntent.getComponent()).isNotNull();
        assertThat(destIntent.getComponent().getClassName()).contains(MainActivity.class.getSimpleName());
    }

    @Test
    public void onNotificationCancel_?????????__?????????_?????????_????????????() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.BETATEST);
        bundle.putString(FomesConstants.Notification.TITLE, "?????????");
        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, MainActivity.class);
        bundle.putString(FomesConstants.Notification.DEEPLINK, "deeplink");

        subject.onNotificationCancel(bundle);

        verify(mockAnalytics).sendNotificationEventLog(
                eq(FomesConstants.Notification.Log.ACTION_DISMISS),
                eq(ChannelManager.Channel.BETATEST),
                eq("?????????")
        );
    }

    @Test
    public void subscribeTopic_?????????__??????_?????????_????????????() {
        subject.subscribeTopic("anyTopic");

        verify(mockFirebaseMessaging).subscribeToTopic(eq("anyTopic"));
        verify(mockSharedPreferencesHelper).setSettingNotificationTopic(eq("anyTopic"), eq(true));
    }

    @Test
    public void unSubscribeTopic_?????????__??????_?????????_??????_????????????() {
        subject.unsubscribeTopic("anyTopic");

        verify(mockFirebaseMessaging).unsubscribeFromTopic(eq("anyTopic"));
        verify(mockSharedPreferencesHelper).setSettingNotificationTopic(eq("anyTopic"), eq(false));
    }

    @Test
    public void isSubscribedTopic_?????????_??????_?????????_???????????????_????????????() {
        subject.isSubscribedTopic("anyTopic");

        verify(mockSharedPreferencesHelper).getSettingNotificationTopic(eq("anyTopic"));
    }
}