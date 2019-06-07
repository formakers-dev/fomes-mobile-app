package com.formakers.fomes.helper;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Process;

import com.formakers.fomes.model.EventStat;
import com.formakers.fomes.model.NativeAppInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static android.content.Context.APP_OPS_SERVICE;
import static android.content.Context.USAGE_STATS_SERVICE;

@Singleton
public class AndroidNativeHelper {
    private Context context;

    @Inject
    public AndroidNativeHelper(Context context) {
        this.context = context;
    }

    // ShortTermStats
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

    // pm.getInstalledApplicationInfo로 단순화가능할듯
    public Observable<NativeAppInfo> getInstalledLaunchableApps() {
        final PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return Observable.from(packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA))
                .map(resolveInfo -> new NativeAppInfo(resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(packageManager).toString()));
    }

    // 리팩토링 필요해보임
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

    // LongTermStats
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

    public String getVersionName(String packageName) {
        PackageManager packageManager = context.getPackageManager();

        String versionName = null;

        try {
            versionName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }
}