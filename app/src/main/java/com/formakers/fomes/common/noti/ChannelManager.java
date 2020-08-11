package com.formakers.fomes.common.noti;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.formakers.fomes.R;
import com.formakers.fomes.common.LocalBroadcastReceiver;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChannelManager {

    private static final int REQUEST_CODE_OPEN = 1001;
    private static final int REQUEST_CODE_DISMISS = 1002;

    public enum Channel {
        DEFAULT("channel_default"),
        ANNOUNCE("channel_announce", "공지사항"),
        BETATEST("channel_betatest", "테스트 관련"),
        POINT("channel_point", "포인트 안내");

        final String id;
        final String title;

        Channel(String id) {
            this.id = id;
            this.title = null;
        }

        Channel(String id, String title) {
            this.id = id;
            this.title = title;
        }

        @Nullable
        public static Channel findById(String id) {
            if (TextUtils.isEmpty(id)) {
                return DEFAULT;
            }

            for (Channel channel : values()) {
                if (id.equals(channel.id)) {
                    return channel;
                }
            }

            return DEFAULT;
        }
    }

    private Context context;
    private AnalyticsModule.Analytics analytics;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private FirebaseMessaging firebaseMessaging;

    @Inject
    public ChannelManager(Context context,
                          AnalyticsModule.Analytics analytics,
                          SharedPreferencesHelper sharedPreferencesHelper,
                          FirebaseMessaging firebaseMessaging) {
        this.context = context;
        this.analytics = analytics;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.firebaseMessaging = firebaseMessaging;
    }

    @TargetApi(26)
    private void prepareChannel(String channelId, int importance) {
        final String appName = context.getString(R.string.app_name);
        String description = "채널디스크립션";
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            NotificationChannel notiChannel = notificationManager.getNotificationChannel(channelId);

            if (notiChannel == null) {
                notiChannel = new NotificationChannel(channelId, appName, importance);
                notiChannel.setDescription(description);
                notificationManager.createNotificationChannel(notiChannel);
            }
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationCompat.Builder getNotificationBuilder(Channel channel, int importance) {
        prepareChannel(channel.id, importance);
        return new NotificationCompat.Builder(context, channel.id);
    }

    public void sendNotification(Map<String, String> dataMap, Class<?> destActivity) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry: dataMap.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        bundle.putSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY, destActivity);
        bundle.putSerializable(FomesConstants.Notification.CHANNEL, ChannelManager.Channel.findById(dataMap.get(FomesConstants.Notification.CHANNEL)));

        sendNotification(bundle);
    }

    public void sendNotification(Bundle notiDataBundle) {
        if (notiDataBundle == null) {
            return;
        }

        // mandatory
        Channel channel = (ChannelManager.Channel) notiDataBundle.getSerializable(FomesConstants.Notification.CHANNEL);
        String title = notiDataBundle.getString(FomesConstants.Notification.TITLE);
        String subTitle = notiDataBundle.getString(FomesConstants.Notification.SUB_TITLE);
        String message = notiDataBundle.getString(FomesConstants.Notification.MESSAGE);

        // optional
        String isSummaryString = notiDataBundle.getString(FomesConstants.Notification.IS_SUMMARY);
        boolean isSummary = isSummaryString != null && Boolean.parseBoolean(isSummaryString);
        String summarySubText = notiDataBundle.getString(FomesConstants.Notification.SUMMARY_SUB_TEXT);
        String deeplink = notiDataBundle.getString(FomesConstants.Notification.DEEPLINK);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = getNotificationBuilder(channel, NotificationManager.IMPORTANCE_MAX);
        } else {
            builder = getNotificationBuilder();
        }

        // 노티 이벤트 중첩을 막기위해 identical hash code 생성
        int id = Long.valueOf(new Date().getTime()).hashCode();

        // 노티 클릭 시 수행할 이벤트
        Intent clickIntent = new Intent(context, LocalBroadcastReceiver.class);
        clickIntent.setAction(FomesConstants.Broadcast.ACTION_NOTI_CLICKED);
        clickIntent.putExtras(notiDataBundle);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, id + REQUEST_CODE_OPEN, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 노티 취소 시 수행할 이벤트
        Intent cancelIntent = new Intent(context, LocalBroadcastReceiver.class);
        cancelIntent.setAction(FomesConstants.Broadcast.ACTION_NOTI_CANCELLED);
        cancelIntent.putExtras(notiDataBundle);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, id + REQUEST_CODE_DISMISS, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder = builder.setContentIntent(clickPendingIntent)
                .setDeleteIntent(cancelPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_noti)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_app))
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(subTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setGroup("ALL")     // 채널별로 그룹핑 하고싶으면 channel.id 사용하기
                .setGroupSummary(isSummary);

        if (!isSummary) {
            builder = builder.setSubText(channel.title);
        } else {
            builder = builder.setSubText(summarySubText);
        }

        Notification notification = builder.build();

        sendNotification(notification.hashCode(), notification);

        analytics.sendNotificationEventLog(FomesConstants.Notification.Log.ACTION_RECEIVE, channel, title);
    }

    public void sendNotification(int notiId, Notification notification) {
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager != null) {
            notificationManager.notify(notiId, notification);
        }
    }

    // TODO : 뭔가 설정 관련 같은데... 셋팅 매니저를 만들어서 관리할지 고민되네..
    public void subscribeTopic(String topic) {
        firebaseMessaging.subscribeToTopic(topic);
        sharedPreferencesHelper.setSettingNotificationTopic(topic, true);
    }

    public void unsubscribeTopic(String topic) {
        firebaseMessaging.unsubscribeFromTopic(topic);
        sharedPreferencesHelper.setSettingNotificationTopic(topic, false);
    }

    public boolean isSubscribedTopic(String topic) {
        return sharedPreferencesHelper.getSettingNotificationTopic(topic);
    }
    // TODO : end of 뭔가 설정 관련 같은데... 셋팅 매니저를 만들어서 관리할지 고민되네..

    public Intent onNotificationClick(Bundle notificationDataBundle) {
        if ( notificationDataBundle ==  null) {
            return new Intent(context, MainActivity.class);
        }

        Channel channel = (Channel) notificationDataBundle.getSerializable(FomesConstants.Notification.CHANNEL);
        String title = notificationDataBundle.getString(FomesConstants.Notification.TITLE);
        Class destActivity = (Class) notificationDataBundle.getSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY);
        String deeplink = notificationDataBundle.getString(FomesConstants.Notification.DEEPLINK);

        analytics.sendNotificationEventLog(FomesConstants.Notification.Log.ACTION_OPEN, channel, title);

        Intent destIntent;
        if (TextUtils.isEmpty(deeplink)) {
            destIntent = new Intent(context, destActivity);
        } else {
            destIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink));
        }

        destIntent.putExtra(FomesConstants.EXTRA.IS_FROM_NOTIFICATION, true);

        return destIntent;
    }

    public void onNotificationCancel(Bundle notificationDataBundle) {
        if (notificationDataBundle == null) {
            return;
        }

        ChannelManager.Channel channel = (ChannelManager.Channel) notificationDataBundle.getSerializable(FomesConstants.Notification.CHANNEL);
        String title = notificationDataBundle.getString(FomesConstants.Notification.TITLE);

        analytics.sendNotificationEventLog(FomesConstants.Notification.Log.ACTION_DISMISS, channel, title);
    }
}