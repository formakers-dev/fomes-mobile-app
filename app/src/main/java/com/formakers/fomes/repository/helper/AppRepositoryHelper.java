package com.formakers.fomes.repository.helper;

import android.util.Log;

import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.DailyStatSummary;
import com.formakers.fomes.repository.model.AppUsageRealmObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

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
        } catch(RealmException e) {
            Log.d(AppRepositoryHelper.class.getSimpleName(), e.getMessage());
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

    public void deleteAppUsages(int fromDate) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction(realm -> realmInstance.where(AppUsageRealmObject.class).lessThan("yyyymmdd", fromDate).findAll().deleteAllFromRealm());
        }
    }
}
