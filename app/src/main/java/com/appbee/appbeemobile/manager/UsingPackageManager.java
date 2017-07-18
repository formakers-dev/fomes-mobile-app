package com.appbee.appbeemobile.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.appbee.appbeemobile.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class UsingPackageManager {
    public PackageManager packageManager;

    public UsingPackageManager(Context context) {
        packageManager = context.getPackageManager();
    }

    public List<AppInfo> getAppList() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        List<AppInfo> appInfoList = new ArrayList<>();
        for(ResolveInfo resolveInfo : resolveInfoList) {
            appInfoList.add(new AppInfo(resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(packageManager).toString()));
        }
        return appInfoList;
    }
}
