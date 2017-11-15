package com.appbee.appbeemobile.repository.helper;

import com.appbee.appbeemobile.model.AppUsage;
import com.appbee.appbeemobile.repository.model.AppUsageRealmObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

public class AppRepositoryHelper {

    @Inject
    public AppRepositoryHelper() {
    }

    public void updateTotalUsedTime(Map<String, Long> map) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction(realm -> {
                for (String packageName : map.keySet()) {
                    AppUsageRealmObject appUsageRealmObject = realm.where(AppUsageRealmObject.class).equalTo("packageName", packageName).findFirst();
                    if (appUsageRealmObject != null) {
                        appUsageRealmObject.setTotalUsedTime(appUsageRealmObject.getTotalUsedTime() + map.get(packageName));
                    } else {
                        appUsageRealmObject = new AppUsageRealmObject();
                        appUsageRealmObject.setPackageName(packageName);
                        appUsageRealmObject.setTotalUsedTime(map.get(packageName));
                        realm.copyToRealmOrUpdate(appUsageRealmObject);
                    }
                }
            });
        }
    }

    public List<AppUsage> getAppUsages() {
        List<AppUsage> appUsageList = new ArrayList<>();

        try(Realm realmInstance = Realm.getDefaultInstance()) {
            RealmResults<AppUsageRealmObject> appUsageRealmObjectList = realmInstance.where(AppUsageRealmObject.class).findAll();
            for(AppUsageRealmObject appUsageRealmObject : appUsageRealmObjectList) {
                appUsageList.add(toAppUsage(appUsageRealmObject));
            }
        }

        return appUsageList;
    }

    private AppUsage toAppUsage(AppUsageRealmObject appUsageRealmObject) {
        return new AppUsage(appUsageRealmObject.getPackageName(), appUsageRealmObject.getTotalUsedTime());
    }
}
