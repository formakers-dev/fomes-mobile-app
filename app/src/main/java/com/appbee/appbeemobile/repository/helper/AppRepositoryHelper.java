package com.appbee.appbeemobile.repository.helper;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.model.SocialApp;
import com.appbee.appbeemobile.repository.model.UsedApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

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

    public void insertSocialApps(List<AppInfo> appInfoList) {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction((realm) -> {
                for (AppInfo appInfo : appInfoList) {
                    SocialApp socialApp = new SocialApp();
                    socialApp.setPackageName(appInfo.getPackageName());
                    socialApp.setAppName(appInfo.getAppName());
                    realm.copyToRealmOrUpdate(socialApp);
                }
            });
        }
    }

    public AppInfo getMostUsedSocialApp() {
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            RealmResults<SocialApp> socialApps = realmInstance.where(SocialApp.class).findAll();

            long maxTotalUsedTime = Long.MIN_VALUE;
            UsedApp maxTotalTimeUseApp = null;
            for(SocialApp socialApp : socialApps) {
                UsedApp usedApp = realmInstance.where(UsedApp.class).equalTo("packageName", socialApp.getPackageName()).findFirst();
                if(usedApp != null && usedApp.getTotalUsedTime() > maxTotalUsedTime) {
                    maxTotalUsedTime = usedApp.getTotalUsedTime();
                    maxTotalTimeUseApp = usedApp;
                }
            }

            if(maxTotalTimeUseApp != null) {
                return new AppInfo(maxTotalTimeUseApp.getPackageName(), maxTotalTimeUseApp.getAppName(), "", "", "", "");
            } else {
                return new AppInfo("", "", "", "", "", "");
            }

        }
    }

    public List<AppInfo> getSortedUsedAppsByTotalUsedTime() {
        List<AppInfo> appInfoList = new ArrayList<>();

        try (Realm realmInstance = Realm.getDefaultInstance()) {
            final RealmResults<UsedApp> usedAppList = realmInstance.where(UsedApp.class).findAllSorted("totalUsedTime", Sort.DESCENDING);

            for (UsedApp usedApp: usedAppList) {
                appInfoList.add(new AppInfo(usedApp));
            }
        }

        return appInfoList;
    }
}
