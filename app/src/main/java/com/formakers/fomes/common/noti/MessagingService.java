package com.formakers.fomes.common.noti;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
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
    @Inject UserDAO userDAO;

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
            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            channelManager.sendNotification(dataMap, pendingIntent);
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
            userDAO.getUserInfo()
                    .flatMapCompletable(user -> {
                        user.setRegistrationToken(newToken);
                        return userService.updateUser(user);
                    })
                    .subscribe(() -> Log.d(TAG, "Token Refresh is Completed!"), e -> Log.e(TAG, String.valueOf(e)));
        }
    }
//    @Override
//    public void handleIntent(Intent intent) {
//        super.handleIntent(intent);
//        Log.d(TAG, "handleIntent) " + (intent != null ? intent.toString() : ""));
//    }
}
