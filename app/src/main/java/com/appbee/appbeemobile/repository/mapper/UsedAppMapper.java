package com.appbee.appbeemobile.repository.mapper;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.model.UsedApp;

public class UsedAppMapper {
    public static UsedApp toUsedApp(final AppInfo appInfo) {
        final UsedApp usedApp = new UsedApp();
        usedApp.setPackageName(appInfo.getPackageName());
        usedApp.setAppName(appInfo.getAppName());
        usedApp.setCategoryId1(appInfo.getCategoryId1());
        usedApp.setCategoryName1(appInfo.getCategoryName1());
        usedApp.setCategoryId2(appInfo.getCategoryId2());
        usedApp.setCategoryName2(appInfo.getCategoryName2());
        return usedApp;
    }
}
