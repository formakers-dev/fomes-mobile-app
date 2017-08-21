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

        assertEquals(actualData.size(), 9);

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
        assertEquals(actualData.size(), 9);

        insertDummyData();
        assertEquals(actualData.size(), 9);
    }

    @Test
    public void getCategoryListSortedByInstalls호출시_설치된개수순으로카테고리명리스트를_리턴한다() throws Exception {
        insertDummyData();

        List<String> mostInstalledCategories = subject.getCategoryListSortedByInstalls();

        assertEquals(mostInstalledCategories.size(), 5);
        assertEquals(mostInstalledCategories.get(0), "categoryId1");
        assertEquals(mostInstalledCategories.get(1), "categoryId2");
        assertEquals(mostInstalledCategories.get(2), "categoryId4");
        assertEquals(mostInstalledCategories.get(3), "categoryId5");
        assertEquals(mostInstalledCategories.get(4), "categoryId3");
    }

    @Test
    public void updateTotalUsedTime호출시_사용앱테이블의_TotalUsedTime컬럼값이_갱신된다() throws Exception {
        insertDummyData();

        Map<String, Long> map = new HashMap<>();
        map.put("com.package.name0", 0L);
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
        expectedData.add(new AppInfo("com.package.name3", "appName3", "categoryId1", "categoryName1", "", null));
        expectedData.add(new AppInfo("com.package.name4", "appName4", "categoryId2", "categoryName2", "categoryId1", "categoryName1"));
        expectedData.add(new AppInfo("com.package.name5", "appName5", "categoryId2", "categoryName2", "", null));
        expectedData.add(new AppInfo("com.package.name6", "appName6", "categoryId3", "categoryName3", "categoryId5", "categoryName5"));
        expectedData.add(new AppInfo("com.package.name7", "appName7", "categoryId4", "categoryName4", null, null));
        expectedData.add(new AppInfo("com.package.name8", "appName8", "categoryId4", "categoryName4", null, null));
        expectedData.add(new AppInfo("com.package.name9", "appName9", "categoryId5", "categoryName5", null, null));

        subject.insertUsedApps(expectedData);
    }
}