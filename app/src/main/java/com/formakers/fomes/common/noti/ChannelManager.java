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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.main.view.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.formakers.fomes.common.FomesConstants.Notification.TOPIC_NOTICE_ALL;

@Singleton
public class ChannelManager {

    public enum Channel {
        DEFAULT("channel_default"),
        ANNOUNCE("channel_announce", "공지사항"),
        BETATEST("channel_betatest", "테스트 관련");

        String id;
        String title;

        Channel(String id) {
            this.id = id;
        }

        Channel(String id, String title) {
            this.id = id;
            this.title = title;
        }

        @Nullable
        static Channel findById(String id) {
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

    @Inject
    public ChannelManager(Context context) {
        this.context = context;
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
        NotificationCompat.Builder builder;

        // mandatory
        Channel channel = ChannelManager.Channel.findById(dataMap.get(FomesConstants.Notification.CHANNEL));
        String title = dataMap.get(FomesConstants.Notification.TITLE);
        String subTitle = dataMap.get(FomesConstants.Notification.SUB_TITLE);
        String message = dataMap.get(FomesConstants.Notification.MESSAGE);

        // optional
        String isSummaryString = dataMap.get(FomesConstants.Notification.IS_SUMMARY);
        boolean isSummary = isSummaryString != null && Boolean.parseBoolean(isSummaryString);
        String summarySubText = dataMap.get(FomesConstants.Notification.SUMMARY_SUB_TEXT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = getNotificationBuilder(channel, NotificationManager.IMPORTANCE_MAX);
        } else {
            builder = getNotificationBuilder();
        }

        // 노티 클릭 시 이동할 액티비티 지정
        Intent notificationIntent = new Intent(context, destActivity);
        notificationIntent.putExtra(FomesConstants.EXTRA.IS_FROM_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = builder.setContentIntent(pendingIntent)
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