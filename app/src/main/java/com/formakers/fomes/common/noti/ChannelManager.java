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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.LocalBroadcastReceiver;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.formakers.fomes.common.FomesConstants.Notification.TOPIC_NOTICE_ALL;

@Singleton
public class ChannelManager {

    private static final int REQUEST_CODE_OPEN = 1001;
    private static final int REQUEST_CODE_DISMISS = 1002;

    public enum Channel {
        DEFAULT("channel_default"),
        ANNOUNCE("channel_announce", "공지사항"),
        BETATEST("channel_betatest", "테스트 관련");

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

    @Inject
    public ChannelManager(Context context, AnalyticsModule.Analytics analytics) {
        this.context = context;
        this.analytics = analytics;
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationCompat.Builder getNotificationBuilder(Channel channel, int importance) {
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
        NotificationCompat.Builder builder;

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

    public void subscribePublicTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NOTICE_ALL);
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
}