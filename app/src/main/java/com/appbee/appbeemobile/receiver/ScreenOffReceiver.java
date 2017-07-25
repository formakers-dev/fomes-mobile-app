package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.DetailUsageStat;

import javax.inject.Inject;

public class ScreenOffReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenOffReceiver.class.getSimpleName();
    private final StatManager statManager;

    @Inject
    public ScreenOffReceiver(StatManager statManager) {
        this.statManager = statManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        for(DetailUsageStat detailUsageStat: statManager.getDetailUsageStats()){
            Log.d(TAG, "[DetailUsageState] " + detailUsageStat.getPackageName() + ", " + detailUsageStat.getStartTimeStamp() + ", " + detailUsageStat.getEndTimeStamp() +", " + detailUsageStat.getTotalUsedTime());
        }
    }
}
