package com.formakers.fomes.common.noti;

import android.content.Context;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.view.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import javax.inject.Inject;

public class MessagingService extends FirebaseMessagingService {
    private final static String TAG = "MessagingService";

    @Inject Context context;
    @Inject ChannelManager channelManager;
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject UserService userService;
    @Inject EventLogService eventLogService;

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

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        // 로그인하지 않은 사용자는 노티를 수신하지 못하도록 한다.
        if (!sharedPreferencesHelper.hasAccessToken()) {
            return;
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> dataMap = remoteMessage.getData();

            channelManager.sendNotification(dataMap, MainActivity.class);
            eventLogService.sendEventLog(new EventLog().setCode(FomesConstants.FomesEventLog.Code.NOTIFICATION_RECEIVED))
                    .subscribe(() -> Log.d(TAG, "Event log is sent successfully!!"),
                            (e) -> Log.e(TAG, String.valueOf(e)));
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

        // 로그인한 사용자만 노티 토큰을 서버에 업데이트 시킨다.
        if (sharedPreferencesHelper.hasAccessToken()) {
            userService.updateRegistrationToken(newToken)
                    .subscribe(() -> Log.d(TAG, "Token Refresh is Completed!"), e -> Log.e(TAG, String.valueOf(e)));
        }
    }
}
