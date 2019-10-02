package com.formakers.fomes.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import static com.formakers.fomes.common.constant.FomesConstants.Broadcast.ACTION_NOTI_CANCELLED;
import static com.formakers.fomes.common.constant.FomesConstants.Broadcast.ACTION_NOTI_CLICKED;

public class LocalBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "LocalBroadcastReceiver";

    @Inject
    ChannelManager channelManager;

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
                Intent destIntent = channelManager.onNotificationClick(bundle);
                context.startActivity(destIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            }
            case ACTION_NOTI_CANCELLED: {
                channelManager.onNotificationCancel(bundle);
                break;
            }
        }
    }
}
