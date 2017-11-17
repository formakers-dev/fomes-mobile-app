package com.appbee.appbeemobile.repository.helper;

import com.appbee.appbeemobile.model.AppUsage;
import com.appbee.appbeemobile.model.DailyStatSummary;
import com.appbee.appbeemobile.repository.model.AppUsageRealmObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

public class AppRepositoryHelper {

    @Inject
    public AppRepositoryHelper() {
    }

    public void updateTotalUsedTime(List<DailyStatSummary> dailyStatSummaryList) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction(realm -> {
                for (DailyStatSummary dailyStatSummary : dailyStatSummaryList) {
                    AppUsageRealmObject appUsageRealmObject = realm.where(AppUsageRealmObject.class)
                            .equalTo("appUsageKey", dailyStatSummary.getPackageName().concat(String.valueOf(dailyStatSummary.getYyyymmdd())))
                            .findFirst();
                    if (appUsageRealmObject != null) {
                        appUsageRealmObject.setTotalUsedTime(appUsageRealmObject.getTotalUsedTime() + dailyStatSummary.getTotalUsedTime());
                    } else {
                        appUsageRealmObject = new AppUsageRealmObject();
                        appUsageRealmObject.setAppUsageKey(dailyStatSummary.getPackageName().concat(String.valueOf(dailyStatSummary.getYyyymmdd())));
                        appUsageRealmObject.setPackageName(dailyStatSummary.getPackageName());
                        appUsageRealmObject.setYyyymmdd(dailyStatSummary.getYyyymmdd());
                        appUsageRealmObject.setTotalUsedTime(dailyStatSummary.getTotalUsedTime());
                        realm.copyToRealmOrUpdate(appUsageRealmObject);
                    }
                }
            });
        }
    }

    public List<AppUsage> getAppUsages() {
        List<AppUsage> appUsageList = new ArrayList<>();
        Map<String, Long> appUsageSummary = new HashMap<>();

        try (Realm realmInstance = Realm.getDefaultInstance()) {
            RealmResults<AppUsageRealmObject> appUsageRealmObjectList = realmInstance.where(AppUsageRealmObject.class).findAll();
            for (AppUsageRealmObject appUsageRealmObject : appUsageRealmObjectList) {
                final String packageName = appUsageRealmObject.getPackageName();
                appUsageSummary.put(packageName, appUsageRealmObject.getTotalUsedTime() + ((appUsageSummary.containsKey(packageName)) ? appUsageSummary.get(packageName) : 0L));
            }
        }

        for (Map.Entry<String, Long> appUsageSummaryEntry : appUsageSummary.entrySet()) {
            appUsageList.add(new AppUsage(appUsageSummaryEntry.getKey(), appUsageSummaryEntry.getValue()));
        }

        return appUsageList;
    }
}
