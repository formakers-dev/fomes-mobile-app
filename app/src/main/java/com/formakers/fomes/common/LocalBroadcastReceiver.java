package com.formakers.fomes.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import static com.formakers.fomes.common.FomesConstants.Broadcast.ACTION_NOTI_CANCELLED;
import static com.formakers.fomes.common.FomesConstants.Broadcast.ACTION_NOTI_CLICKED;

public class LocalBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "LocalBroadcastReceiver";

    @Inject AnalyticsModule.Analytics analytics;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive) " + intent);

        ((FomesApplication) context.getApplicationContext()).getComponent().inject(this);

        String action = intent.getAction();

        if (action == null) {
            Log.e(TAG, "action is null!");
            return;
        }

        Bundle bundle = intent.getExtras();

        switch (action) {
            case ACTION_NOTI_CLICKED: {
                ChannelManager.Channel channel = (ChannelManager.Channel) bundle.getSerializable(FomesConstants.Notification.CHANNEL);
                String title = bundle.getString(FomesConstants.Notification.TITLE);
                Class destActivity = (Class) bundle.getSerializable(FomesConstants.Notification.DESTINATION_ACTIVITY);
                String deeplink = bundle.getString(FomesConstants.Notification.DEEPLINK);

                analytics.sendNotificationEventLog(FomesConstants.Notification.Log.ACTION_OPEN, channel, title);

                Intent destIntent;
                if (TextUtils.isEmpty(deeplink)) {
                    destIntent = new Intent(context, destActivity);
                } else {
                    destIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink));
                }

                destIntent.putExtra(FomesConstants.EXTRA.IS_FROM_NOTIFICATION, true);

                context.startActivity(destIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            }
            case ACTION_NOTI_CANCELLED: {
                ChannelManager.Channel channel = (ChannelManager.Channel) bundle.getSerializable(FomesConstants.Notification.CHANNEL);
                String title = bundle.getString(FomesConstants.Notification.TITLE);

                analytics.sendNotificationEventLog(FomesConstants.Notification.Log.ACTION_DISMISS, channel, title);
                break;
            }
        }
    }
}
