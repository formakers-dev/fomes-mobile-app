package com.appbee.appbeemobile.repository.helper;

import android.text.TextUtils;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.mapper.UsedAppMapper;
import com.appbee.appbeemobile.repository.model.UsedApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AppRepositoryHelper {

    @Inject
    public AppRepositoryHelper() {
    }

    public void insertUsedApps(final List<AppInfo> appInfos) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction((realm) -> {
                for (AppInfo appInfo : appInfos) {
                    realm.copyToRealmOrUpdate(UsedAppMapper.toUsedApp(appInfo));
                }
            });
        }
    }

    public Map<String, Integer> getAppCountMapByCategory() {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            RealmResults<UsedApp> usedAppList = realmInstance.where(UsedApp.class).findAll();

            Map<String, Integer> categoryCountMap = new HashMap<>();
            for (UsedApp usedApp : usedAppList) {
                String categoryId1Key = usedApp.getCategoryId1();
                Integer categoryId1Value = categoryCountMap.get(categoryId1Key);
                categoryCountMap.put(categoryId1Key, categoryId1Value == null ? 1 : categoryId1Value + 1);

                String categoryId2Key = usedApp.getCategoryId2();
                if (categoryId2Key != null && !categoryId2Key.isEmpty()) {
                    Integer categoryId2Value = categoryCountMap.get(categoryId2Key);
                    categoryCountMap.put(categoryId2Key, categoryId2Value == null ? 1 : categoryId2Value + 1);
                }
            }

            return categoryCountMap;
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

    public List<AppInfo> getSortedUsedAppsByTotalUsedTime() {
        List<AppInfo> appInfoList = new ArrayList<>();

        try (Realm realmInstance = Realm.getDefaultInstance()) {
            final RealmResults<UsedApp> usedAppList = realmInstance.where(UsedApp.class).findAllSorted("totalUsedTime", Sort.DESCENDING);

            for (UsedApp usedApp: usedAppList) {
                appInfoList.add(UsedAppMapper.toAppInfo(usedApp));
            }
        }

        return appInfoList;
    }

    public Map<String, Long> getUsedTimeMapByCategory() {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            final RealmResults<UsedApp> usedAppList = realmInstance.where(UsedApp.class).findAll();

            Map<String, Long> usedTimeMap = new HashMap<>();
            for (UsedApp usedApp : usedAppList) {
                String key = usedApp.getCategoryName1();
                Long value = usedApp.getTotalUsedTime();
                Long oldValue = usedTimeMap.get(usedApp.getCategoryId1());

                usedTimeMap.put(key, oldValue != null ? oldValue + value : value);

                if (!TextUtils.isEmpty(usedApp.getCategoryId2())) {
                    key = usedApp.getCategoryName2();
                    value = usedApp.getTotalUsedTime();
                    oldValue = usedTimeMap.get(usedApp.getCategoryId2());

                    usedTimeMap.put(key, oldValue != null ? oldValue + value : value);
                }
            }

            return usedTimeMap;
        }
    }
}
