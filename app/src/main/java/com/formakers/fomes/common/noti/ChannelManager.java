package com.formakers.fomes.common.noti;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.formakers.fomes.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChannelManager {

    public enum Channel {
        // TODO : 노티 타입 컨셉 정의 후 정리 필요
//        ALWAYS("channel_always"),
        ANNOUNCE("channel_announce"),
        MATCHED("channel_matched");

        String name;

        Channel(String name) {
            this.name = name;
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
        prepareChannel(channel.name, importance);
        return new NotificationCompat.Builder(context, channel.name);
    }

    public void sendNotification(int notiId, Notification notification) {
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager != null) {
            notificationManager.notify(notiId, notification);
        }
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