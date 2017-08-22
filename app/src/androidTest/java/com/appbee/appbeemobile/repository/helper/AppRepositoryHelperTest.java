package com.appbee.appbeemobile.repository.helper;

import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.runner.AndroidJUnit4;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.repository.model.SocialApp;
import com.appbee.appbeemobile.repository.model.UsedApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

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
        realm.delete(UsedApp.class);
        realm.commitTransaction();
        realm.close();
    }

    @Test
    public void insertUsedApps테스트_insertTest() throws Exception {
        insertDummyData();

        RealmResults<UsedApp> actualData = realm.where(UsedApp.class).findAll();

        assertEquals(actualData.size(), 10);

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
        assertEquals(actualData.size(), 10);

        insertAdditionalDummyData();
        assertEquals(actualData.size(), 11);
        assertEquals(actualData.get(0).getAppName(), "변경된AppName");
        assertEquals(actualData.get(10).getAppName(), "appName10");
    }

    @Test
    public void getCategoryMapSortedByInstalls호출시_설치된개수순으로카테고리명_맵을_리턴한다() throws Exception {
        insertDummyData();

        Map<String, Integer> appCountMap = subject.getAppCountMapByCategory();

        assertEquals(appCountMap.size(), 5);
        assertAppCountWithCategoryId(appCountMap, "categoryId1", 4);
        assertAppCountWithCategoryId(appCountMap, "categoryId2", 3);
        assertAppCountWithCategoryId(appCountMap, "categoryId3", 1);
        assertAppCountWithCategoryId(appCountMap, "categoryId4", 2);
        assertAppCountWithCategoryId(appCountMap, "categoryId5", 3);
    }

    private void assertAppCountWithCategoryId(Map<String, Integer> map, String categoryId, int appCount) {
        assertEquals(map.containsKey(categoryId), true);
        assertEquals(map.get(categoryId), Integer.valueOf(appCount));
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

    @Test
    public void getSortedUsedAppsByTotalUsedTime호출시_DB에_저장된_모든_사용앱정보를_사용시간역순으로_정렬하여_리턴한다() throws Exception {
        insert2DummyDataWithTotalUsedTime();

        List<AppInfo> usedApps = subject.getSortedUsedAppsByTotalUsedTime();

        assertEquals(usedApps.size(), 2);
        assertEquals(usedApps.get(0).getPackageName(), "com.package.name2");
        assertEquals(usedApps.get(1).getPackageName(), "com.package.name1");
    }

    @Test
    public void getAppCountByCategoryId호출시_해당_카테고리의_앱_개수를_리턴한다() throws Exception {
        insertDummyData();
        assertEquals(subject.getAppCountByCategoryId("categoryId1"), 4);
    }

    @Test
    public void insertSocial호출시_신규_소셜앱의_정보를_저장한다() throws Exception {
        insertDummyDataForSocialApp();

        RealmResults<SocialApp> socialApps = realm.where(SocialApp.class).findAll();

        assertEquals(socialApps.size(), 7);
        checkSocialAppData(socialApps.get(0), "com.facebook.katana", "Facebook");
        checkSocialAppData(socialApps.get(1), "com.instagram.android", "Instagram");
        checkSocialAppData(socialApps.get(2), "com.kakao.talk", "카카오톡 KakaoTalk");
        checkSocialAppData(socialApps.get(3), "jp.naver.line.android", "라인 LINE");
        checkSocialAppData(socialApps.get(4), "com.nhn.android.band", "밴드");
        checkSocialAppData(socialApps.get(5), "kr.co.vcnc.android.couple", "커플앱 비트윈 - Between");
        checkSocialAppData(socialApps.get(6), "com.android.chrome", "Chrome");
    }

    @Test
    public void insertSocial호출시_기존_소셜앱의_경우_정보를_업데이트한다() throws Exception {
        insertDummyDataForSocialApp();

        insertAdditionalDummyDataForSocialApp();
        RealmResults<SocialApp> socialApps = realm.where(SocialApp.class).findAll();

        assertEquals(socialApps.size(), 8);
        assertEquals(socialApps.get(0).getAppName(), "페이스북");
    }

    @Test
    public void getMostUsedSocialApp() throws Exception {
        insertDummyData();
        insertDummyDataForSocialApp();

        AppInfo appInfo = subject.getMostUsedSocialApp();
        assertEquals(appInfo.getAppName(), "Chrome");
    }

    @Test
    public void getAppCountByCategoryIds() throws Exception {
        insertDummyData();

        List<String> CategoryIds = Arrays.asList("categoryId1", "categoryId2");

        assertEquals(subject.getAppCountByCategoryIds(CategoryIds), 7);
    }

    private void checkSocialAppData(SocialApp socialApp, String packageName, String appName) {
        assertEquals(socialApp.getPackageName(), packageName);
        assertEquals(socialApp.getAppName(), appName);
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
        expectedData.add(new AppInfo("com.android.chrome", "Chrome", "categoryId5", "categoryName5", null, null));

        subject.insertUsedApps(expectedData);
    }

    private void insertAdditionalDummyData() {
        List<AppInfo> expectedData = new ArrayList<>();
        expectedData.add(new AppInfo("com.package.name1", "변경된AppName", "categoryId1", "categoryName1", "categoryId2", "categoryName2"));
        expectedData.add(new AppInfo("com.package.name10", "appName10", "categoryId1", "categoryName1", null, null));

        subject.insertUsedApps(expectedData);
    }

    private void insert2DummyDataWithTotalUsedTime() {
        List<AppInfo> expectedData = new ArrayList<>();
        expectedData.add(new AppInfo("com.package.name1", "appName1", "categoryId1", "categoryName1", "categoryId2", "categoryName2"));
        expectedData.add(new AppInfo("com.package.name2", "appName2", "categoryId1", "categoryName1", null, null));
        subject.insertUsedApps(expectedData);

        Map<String, Long> map = new HashMap<>();
        map.put("com.package.name0", 0L);
        map.put("com.package.name1", 1000L);
        map.put("com.package.name2", 2000L);

        subject.updateTotalUsedTime(map);
    }

    private void insertDummyDataForSocialApp() {
        List<AppInfo> dummyData = new ArrayList<>();
        dummyData.add(new AppInfo("com.facebook.katana", "Facebook", "", "", "", ""));
        dummyData.add(new AppInfo("com.instagram.android", "Instagram", "", "", "", ""));
        dummyData.add(new AppInfo("com.kakao.talk", "카카오톡 KakaoTalk", "", "", "", ""));
        dummyData.add(new AppInfo("jp.naver.line.android", "라인 LINE", "", "", "", ""));
        dummyData.add(new AppInfo("com.nhn.android.band", "밴드", "", "", "", ""));
        dummyData.add(new AppInfo("kr.co.vcnc.android.couple", "커플앱 비트윈 - Between", "", "", "", ""));
        dummyData.add(new AppInfo("com.android.chrome", "Chrome", "", "", "", ""));
        subject.insertSocialApps(dummyData);
    }

    private void insertAdditionalDummyDataForSocialApp() {
        List<AppInfo> dummyData = new ArrayList<>();
        dummyData.add(new AppInfo("com.facebook.katana", "페이스북", "", "", "", ""));
        dummyData.add(new AppInfo("com.social.app", "소셜앱앱", "", "", "", ""));
        subject.insertSocialApps(dummyData);
    }
}