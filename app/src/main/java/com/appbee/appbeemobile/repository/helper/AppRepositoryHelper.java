package com.appbee.appbeemobile.repository.helper;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.model.UsedApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.Sort;
import rx.Observable;
import io.realm.RealmResults;

public class AppRepositoryHelper {

    @Inject
    public AppRepositoryHelper() {
    }

    public void insertUsedApps(final List<AppInfo> appInfos) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction((realm) -> {
                for (AppInfo appInfo : appInfos) {
                    UsedApp usedApp = new UsedApp();
                    usedApp.setPackageName(appInfo.getPackageName());
                    usedApp.setAppName(appInfo.getAppName());
                    usedApp.setCategoryId1(appInfo.getCategoryId1());
                    usedApp.setCategoryName1(appInfo.getCategoryName1());
                    usedApp.setCategoryId2(appInfo.getCategoryId2());
                    usedApp.setCategoryName2(appInfo.getCategoryName2());
                    realm.copyToRealmOrUpdate(usedApp);
                }
            });
        }
    }

    public int getTotalUsedApps() {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            return (int)realmInstance.where(UsedApp.class).count();
        }
    }

    public List<String> getCategoryListSortedByInstalls() {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            RealmResults<UsedApp> usedAppList = realmInstance.where(UsedApp.class).findAll();

            Map<String, Integer> categoryCountMap = new HashMap<>();
            for (UsedApp usedApp : usedAppList) {
                String categoryId1Key = usedApp.getCategoryId1();
                Integer categoryId1Value = categoryCountMap.get(categoryId1Key);
                categoryCountMap.put(categoryId1Key, categoryId1Value == null ? 1 : categoryId1Value + 1);

                String categoryId2Key = usedApp.getCategoryId2();
                Integer categoryId2Value = categoryCountMap.get(categoryId2Key);
                categoryCountMap.put(categoryId2Key, categoryId2Value == null ? 1 : categoryId2Value + 1);
            }

            List<Map.Entry<String, Integer>> categorylist = new ArrayList<>(categoryCountMap.entrySet());
            Collections.sort(categorylist, (o1, o2) -> o1.getValue().compareTo(o2.getValue()) * -1);

            LinkedHashMap<String, Integer> sortedCategoryMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> item : categorylist) {
                if (item.getKey() != null && !item.getKey().isEmpty()) {
                    sortedCategoryMap.put(item.getKey(), item.getValue());
                }
            }

            List<String> sortedCategoryList = new ArrayList<>();
            for (String key : sortedCategoryMap.keySet()) {
                sortedCategoryList.add(key);
            }

            return sortedCategoryList;
        }
    }

    public void updateTotalUsedTime(Map<String, Long> map) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction(realm -> {
                for (String packageName : map.keySet()) {
                    final UsedApp usedApp = realm.where(UsedApp.class).equalTo("packageName", packageName).findFirst();
                    if (usedApp != null) {
                        usedApp.setTotalUsedTime(map.get(packageName));
                    }
                }
            });
        }
    }

    public List<String> getTop3UsedAppList() {
        final List<String> packageNames = new ArrayList<>();

        try (Realm realmInstance = Realm.getDefaultInstance()) {
            final List<UsedApp> usedApps = realmInstance.where(UsedApp.class).findAllSorted("totalUsedTime", Sort.DESCENDING);
            Observable.range(0, Math.min(3, usedApps.size()))
                    .forEach(i ->  packageNames.add(usedApps.get(i).getPackageName()));
        }

        return packageNames;
    }

    public long getAppCountByCategoryId(String categoryId) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            return realmInstance
                    .where(UsedApp.class)
                    .equalTo("categoryId1", categoryId)
                    .or()
                    .equalTo("categoryId2", categoryId)
                    .count();
        }
    }
}
