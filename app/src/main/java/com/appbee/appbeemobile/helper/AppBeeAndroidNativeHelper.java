package com.appbee.appbeemobile.helper;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Process;

import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.EventStat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static android.content.Context.APP_OPS_SERVICE;
import static android.content.Context.USAGE_STATS_SERVICE;

@Singleton
public class AppBeeAndroidNativeHelper {
    private Context context;

    @Inject
    public AppBeeAndroidNativeHelper(Context context) {
        this.context = context;
    }

    public List<EventStat> getUsageStatEvents(long startTime, long endTime) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        List<EventStat> eventStats = new ArrayList<>();

        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            boolean hasNextEvent = usageEvents.getNextEvent(event);

            if (hasNextEvent) {
                eventStats.add(new EventStat(event.getPackageName(), event.getEventType(), event.getTimeStamp()));
            }
        }

        return eventStats;
    }

    public Observable<NativeAppInfo> getInstalledLaunchableApps() {
        final PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return Observable.from(packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA))
                .map(resolveInfo -> new NativeAppInfo(resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(packageManager).toString()));
    }

    public NativeAppInfo getNativeAppInfo(String packageName) {
        NativeAppInfo nativeAppInfo = new NativeAppInfo(packageName, "");
        try {
            final PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            nativeAppInfo.setAppName(pm.getApplicationLabel(ai).toString());
            nativeAppInfo.setIcon(pm.getApplicationIcon(packageName));
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return nativeAppInfo;
    }

    public List<UsageStats> getUsageStats(int intervalType, long startTime, long endTime) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        return usageStatsManager.queryUsageStats(intervalType, startTime, endTime);
    }

    public boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);

        if (appOps != null) {
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }

        return false;
    }
}