package com.appbee.appbeemobile.repository.helper;

import android.support.test.runner.AndroidJUnit4;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.model.UsedApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AppRepositoryHelperTest {
    private Realm realm;
    private AppRepositoryHelper subject;

    @Before
    public void setUp() throws Exception {
        RealmConfiguration config = new RealmConfiguration.Builder().inMemory().name("testAppbeeDB").build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        subject = new AppRepositoryHelper();
    }

    @After
    public void tearDown() throws Exception {
        realm.close();
    }

    @Test
    public void insertUsedApps테스트_insertTest() throws Exception {
        insertDummyData();

        RealmResults<UsedApp> actualData = realm.where(UsedApp.class).findAll();

        assertEquals(actualData.size(), 2);

        assertEquals(actualData.get(0).getPackageName(), "com.package.name1");
        assertEquals(actualData.get(0).getAppName(), "appName1");
        assertEquals(actualData.get(0).getCategoryId1(), "categoryId1");
        assertEquals(actualData.get(0).getCategoryName1(), "categoryName1");
        assertEquals(actualData.get(0).getCategoryId2(), "categoryId2");
        assertEquals(actualData.get(0).getCategoryName2(), "categoryName2");

        assertEquals(actualData.get(1).getPackageName(), "com.package.name2");
        assertEquals(actualData.get(1).getAppName(), "appName2");
        assertEquals(actualData.get(1).getCategoryId1(), "categoryId1");
        assertEquals(actualData.get(1).getCategoryName1(), "categoryName1");
        assertEquals(actualData.get(1).getCategoryId2(), null);
        assertEquals(actualData.get(1).getCategoryName2(), null);
    }

    @Test
    public void insertUsedApps테스트_upsertTest() throws Exception {
        insertDummyData();

        RealmResults<UsedApp> actualData = realm.where(UsedApp.class).findAll();
        assertEquals(actualData.size(), 2);

        insertDummyData();
        assertEquals(actualData.size(), 2);
    }

    @Test
    public void updateTotalUsedTime호출시_사용앱테이블의_TotalUsedTime컬럼값이_갱신된다() throws Exception {
        insertDummyData();

        Map<String, Long> map = new HashMap<>();
        map.put("com.package.name1", 1000L);
        map.put("com.package.name2", 2000L);

        subject.updateTotalUsedTime(map);

        assertEquals(realm.where(UsedApp.class).equalTo("packageName", "com.package.name1").findFirst().getTotalUsedTime(), 1000L);
        assertEquals(realm.where(UsedApp.class).equalTo("packageName", "com.package.name2").findFirst().getTotalUsedTime(), 2000L);
    }

    private void insertDummyData() {
        List<AppInfo> expectedData = new ArrayList<>();
        expectedData.add(new AppInfo("com.package.name1", "appName1", "categoryId1", "categoryName1", "categoryId2", "categoryName2"));
        expectedData.add(new AppInfo("com.package.name2", "appName2", "categoryId1", "categoryName1", null, null));
        subject.insertUsedApps(expectedData);
    }
}