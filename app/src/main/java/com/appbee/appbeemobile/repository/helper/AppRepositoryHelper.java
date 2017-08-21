package com.appbee.appbeemobile.repository.helper;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.model.UsedApp;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;

public class AppRepositoryHelper {

    @Inject
    public AppRepositoryHelper() {
    }

    public void insertUsedApps(final List<AppInfo> appInfos) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        appInfos.forEach(appInfo -> {
                    UsedApp usedApp = new UsedApp();
                    usedApp.setPackageName(appInfo.getPackageName());
                    usedApp.setAppName(appInfo.getAppName());
                    usedApp.setCategoryId1(appInfo.getCategoryId1());
                    usedApp.setCategoryName1(appInfo.getCategoryName1());
                    usedApp.setCategoryId2(appInfo.getCategoryId2());
                    usedApp.setCategoryName2(appInfo.getCategoryName2());
                    realm.copyToRealmOrUpdate(usedApp);
                });

        realm.commitTransaction();
        realm.close();
    }

    public int getTotalUsedApps() {
        return (int) Realm.getDefaultInstance().where(UsedApp.class).count();
    }

    public void updateTotalUsedTime(Map<String, Long> map) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        map.forEach((packageName, totalUsedTime) -> {
            final UsedApp usedApp = realm.where(UsedApp.class).equalTo("packageName", packageName).findFirst();
            if(usedApp != null) {
                usedApp.setTotalUsedTime(totalUsedTime);
            }
        });

        realm.commitTransaction();
        realm.close();
    }
}
