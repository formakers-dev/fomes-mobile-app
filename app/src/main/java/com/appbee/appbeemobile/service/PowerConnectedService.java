package com.appbee.appbeemobile.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiver;

import javax.inject.Inject;

public class PowerConnectedService extends Service {

    @Inject
    Context context;

    @Inject
    PowerConnectedReceiver powerConnectedReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);

        context.registerReceiver(powerConnectedReceiver, intentFilter);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
