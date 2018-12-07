package com.formakers.fomes.common.noti;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.view.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class MessagingService extends FirebaseMessagingService {
    private final static String TAG = "MessagingService";

    @Inject Context context;
    @Inject ChannelManager channelManager;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject UserService userService;

    @Override
    public void onCreate() {
        super.onCreate();
        ((FomesApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, String.valueOf(this) + "onMessageReceived) " + (remoteMessage != null ? remoteMessage.toString() : ""));

        if (remoteMessage == null) {
            return;
        }

        // Foreground에 앱이 떠 있을 경우 메시지 수신 처리
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> dataMap = remoteMessage.getData();

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // 여기서 노티 띄우기
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = channelManager.getNotificationBuilder(ChannelManager.Channel.ANNOUNCE, NotificationManager.IMPORTANCE_MAX);
            } else {
                builder = channelManager.getNotificationBuilder();
            }

            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            Notification notification = builder.setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.ic_noti)
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setSubText("공지사항")
                    .setContentTitle(dataMap.get(FomesConstants.Notification.TITLE))
                    .setContentText(dataMap.get(FomesConstants.Notification.MESSAGE))
                    .build();

            channelManager.sendNotification(1, notification);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);

        Log.d(TAG, "Token refreshed: " + newToken);

        sharedPreferencesHelper.setRegistrationToken(newToken);

        if (sharedPreferencesHelper.hasAccessToken()) {
            userService.updateRegistrationToken(sharedPreferencesHelper.getRegistrationToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d(TAG, "Token Refresh is Completed!"));
        }
    }
//    @Override
//    public void handleIntent(Intent intent) {
//        super.handleIntent(intent);
//        Log.d(TAG, "handleIntent) " + (intent != null ? intent.toString() : ""));
//    }
}
