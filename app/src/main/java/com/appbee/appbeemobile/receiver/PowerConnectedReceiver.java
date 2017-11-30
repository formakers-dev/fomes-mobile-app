package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;

import javax.inject.Inject;

public class PowerConnectedReceiver extends BroadcastReceiver {
    private static final String TAG = PowerConnectedReceiver.class.getSimpleName();

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    public PowerConnectedReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "PowerConnectedReceiver OnReceive");
        ((AppBeeApplication) context.getApplicationContext()).getComponent().inject(this);

        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            Log.d(TAG, "PowerConnectedReceiver Action equals");
            if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
                Log.d(TAG, "PowerConnectedReceiver has permission");
                appUsageDataHelper.sendShortTermStatAndAppUsages(new AppUsageDataHelper.SendDataCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "PowerConnectedReceiver send ShortTermStats successfully");
                    }

                    @Override
                    public void onFail() {
                        Log.e(TAG, "PowerConnectedReceiver fail to send ShortTermStats");
                    }
                });
            }
        }
    }
}
