package com.appbee.appbeemobile.repository.helper;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.model.UsedApp;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;

public class AppRepositoryHelper {

    private Realm realm;

    @Inject
    public AppRepositoryHelper(Realm realm) {
        this.realm = realm;
    }

    public void insertUsedApps(final List<AppInfo> appInfos) {
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
}
