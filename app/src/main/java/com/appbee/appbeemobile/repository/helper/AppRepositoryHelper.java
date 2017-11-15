package com.appbee.appbeemobile.repository.helper;

import com.appbee.appbeemobile.repository.model.UsedApp;

import java.util.Map;

import javax.inject.Inject;

import io.realm.Realm;

public class AppRepositoryHelper {

    @Inject
    public AppRepositoryHelper() {
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
}
