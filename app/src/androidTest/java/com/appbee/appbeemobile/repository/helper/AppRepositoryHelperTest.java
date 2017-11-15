package com.appbee.appbeemobile.repository.helper;

import android.support.test.runner.AndroidJUnit4;

import com.appbee.appbeemobile.model.AppUsage;
import com.appbee.appbeemobile.repository.model.AppUsageRealmObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AppRepositoryHelperTest {
    private Realm realm;
    private AppRepositoryHelper subject;

    @Before
    public void setUp() throws Exception {
        String testDBName = "testAppBeeDB";
        RealmConfiguration config = new RealmConfiguration.Builder().inMemory().name(testDBName).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        subject = new AppRepositoryHelper();
    }

    @After
    public void tearDown() throws Exception {
        realm.beginTransaction();
        realm.delete(AppUsageRealmObject.class);
        realm.commitTransaction();
        realm.close();
    }

    @Test
    public void updateTotalUsedTime호출시_사용앱테이블의_TotalUsedTime컬럼값이_갱신된다() throws Exception {
        insertDummyData();

        Map<String, Long> map = new HashMap<>();
        map.put("com.package.name0", 0L);
        map.put("com.package.name1", 1000L);
        map.put("com.package.name2", 2000L);

        subject.updateTotalUsedTime(map);

        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("packageName", "com.package.name0").findFirst().getTotalUsedTime(), 0L);
        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("packageName", "com.package.name1").findFirst().getTotalUsedTime(), 1100L);
        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("packageName", "com.package.name2").findFirst().getTotalUsedTime(), 2200L);
        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("packageName", "com.package.name3").findFirst().getTotalUsedTime(), 300L);
    }

    @Test
    public void getAppUsages호출시_저장된_전체_앱_사용시간목록을_리턴한다() throws Exception {
        insertDummyData();

        List<AppUsage> appUsageList = subject.getAppUsages();

        assertEquals(appUsageList.size(), 3);
        assertEquals(appUsageList.get(0).getPackageName(), "com.package.name1");
        assertEquals(appUsageList.get(0).getTotalUsedTime(), 100L);
        assertEquals(appUsageList.get(1).getPackageName(), "com.package.name2");
        assertEquals(appUsageList.get(1).getTotalUsedTime(), 200L);
        assertEquals(appUsageList.get(2).getPackageName(), "com.package.name3");
        assertEquals(appUsageList.get(2).getTotalUsedTime(), 300L);
    }

    private void insertDummyData() {
        realm.executeTransaction((realmInstance) -> {
            AppUsageRealmObject appUsageRealmObject1 = new AppUsageRealmObject();
            appUsageRealmObject1.setPackageName("com.package.name1");
            appUsageRealmObject1.setTotalUsedTime(100L);
            realmInstance.copyToRealmOrUpdate(appUsageRealmObject1);

            AppUsageRealmObject appUsageRealmObject2 = new AppUsageRealmObject();
            appUsageRealmObject2.setPackageName("com.package.name2");
            appUsageRealmObject2.setTotalUsedTime(200L);
            realmInstance.copyToRealmOrUpdate(appUsageRealmObject2);

            AppUsageRealmObject appUsageRealmObject3 = new AppUsageRealmObject();
            appUsageRealmObject3.setPackageName("com.package.name3");
            appUsageRealmObject3.setTotalUsedTime(300L);
            realmInstance.copyToRealmOrUpdate(appUsageRealmObject3);
        });
    }
}