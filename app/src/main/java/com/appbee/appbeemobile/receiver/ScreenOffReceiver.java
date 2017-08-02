package com.appbee.appbeemobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appbee.appbeemobile.helper.AppUsageDataHelper;

public class ScreenOffReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenOffReceiver.class.getSimpleName();
    private AppUsageDataHelper appUsageDataHelper;

    public ScreenOffReceiver() {
        Log.d(TAG, "====== ScreenOffReceiver !!!!!!");
    }

//    @Inject
//    public ScreenOffReceiver(AppUsageDataHelper appUsageDataHelper) {
//        this.appUsageDataHelper = appUsageDataHelper;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "====== onReceiveonReceive");
//        for(ShortTermStat detailUsageStat: appUsageDataHelper.getShortTermStats()){
//            Log.d(TAG, "[DetailUsageState] " + detailUsageStat.getPackageName() + ", " + detailUsageStat.getStartTimeStamp() + ", " + detailUsageStat.getEndTimeStamp() +", " + detailUsageStat.getTotalUsedTime());
//        }
    }
}
