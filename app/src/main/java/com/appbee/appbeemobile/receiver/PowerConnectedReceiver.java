package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.network.AppStatService;

import javax.inject.Inject;

public class PowerConnectedReceiver extends BroadcastReceiver {

    private AppStatService appStatService;

    @Inject
    public PowerConnectedReceiver(AppStatService appStatService) {
        this.appStatService = appStatService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            Log.d("yenarue", "Power Connected!!!");
            appStatService.sendShortTermStats();
        }
    }
}
