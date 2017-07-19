package com.appbee.appbeemobile.manager;


import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.UsageStatEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.USAGE_STATS_SERVICE;

public class SystemServiceBridge {
    private Context context;

    public SystemServiceBridge(Context context) {
        this.context = context;
    }

    public List<UsageStatEvent> getUsageStatEvents(long startTime, long endTime) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        List<UsageStatEvent> usageStatEvents = new ArrayList<>();

        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            boolean hasNextEvent = usageEvents.getNextEvent(event);

            if (hasNextEvent) {
                usageStatEvents.add(new UsageStatEvent(event.getPackageName(), event.getEventType(), event.getTimeStamp()));
            }
        }

        return usageStatEvents;
    }

    public List<AppInfo> getInstalledLaunchableApps() {
        final PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        List<AppInfo> appInfoList = new ArrayList<>();
        for(ResolveInfo resolveInfo : resolveInfoList) {
            appInfoList.add(new AppInfo(resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(packageManager).toString()));
        }

        return appInfoList;
    }

    public List<UsageStats> getUsageStats(long startTime, long endTime) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        return usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
    }
}
