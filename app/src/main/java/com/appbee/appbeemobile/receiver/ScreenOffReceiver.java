package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.manager.StatManager;

public class ScreenOffReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenOffReceiver.class.getSimpleName();
    private StatManager statManager;

    public ScreenOffReceiver() {
        Log.d(TAG, "====== ScreenOffReceiver !!!!!!");
    }

//    @Inject
//    public ScreenOffReceiver(StatManager statManager) {
//        this.statManager = statManager;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "====== onReceiveonReceive");
//        for(ShortTermStat detailUsageStat: statManager.getShortTermStats()){
//            Log.d(TAG, "[DetailUsageState] " + detailUsageStat.getPackageName() + ", " + detailUsageStat.getStartTimeStamp() + ", " + detailUsageStat.getEndTimeStamp() +", " + detailUsageStat.getTotalUsedTime());
//        }
    }
}
