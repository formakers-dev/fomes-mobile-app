package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.network.AppStatService;

import javax.inject.Inject;

import rx.schedulers.Schedulers;

public class PowerConnectedReceiver extends BroadcastReceiver {
    private static final String TAG = PowerConnectedReceiver.class.getSimpleName();
    private AppStatService appStatService;

    @Inject
    public PowerConnectedReceiver(AppStatService appStatService) {
        this.appStatService = appStatService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            appStatService.getLastUpdateStatTimestamp()
                    .observeOn(Schedulers.io())
                    .subscribe(lastUpdateStatTimestamp -> {
                        appStatService.sendShortTermStats(lastUpdateStatTimestamp)
                                .observeOn(Schedulers.io())
                                .subscribe(result -> {
                                    if(result) {
                                        Log.d(TAG, "PowerConnectedReceiver send ShortTermStats successfully");
                                    } else {
                                        Log.e(TAG, "PowerConnectedReceiver fail to send ShortTermStats");
                                    }
                                }, appStatService::logError);
                    }, appStatService::logError);
        }
    }
}
