package com.formakers.fomes.repository.helper;

import android.support.test.runner.AndroidJUnit4;

import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.DailyStatSummary;
import com.formakers.fomes.repository.model.AppUsageRealmObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

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
    public void updateTotalUsedTime호출시_일별_사용앱테이블의_TotalUsedTime컬럼값이_갱신된다() throws Exception {
        insertDummyData();

        List<DailyStatSummary> dailyStatSummaryList = new ArrayList<>();
        dailyStatSummaryList.add(new DailyStatSummary("com.package.name0", 20171117, 1000L));
        dailyStatSummaryList.add(new DailyStatSummary("com.package.name1", 20171118, 2000L));
        dailyStatSummaryList.add(new DailyStatSummary("com.package.name2", 20171118, 3000L));

        subject.updateTotalUsedTime(dailyStatSummaryList);

        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("appUsageKey", "com.package.name020171117").findFirst().getTotalUsedTime(), 1000L);
        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("appUsageKey", "com.package.name120171117").findFirst().getTotalUsedTime(), 100L);
        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("appUsageKey", "com.package.name120171118").findFirst().getTotalUsedTime(), 2000L);
        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("appUsageKey", "com.package.name220171118").findFirst().getTotalUsedTime(), 3200L);
        assertEquals(realm.where(AppUsageRealmObject.class).equalTo("appUsageKey", "com.package.name320171118").findFirst().getTotalUsedTime(), 300L);
    }

    //TODO:최근한달치 저장에 대한 테스트

    @Test
    public void getAppUsages호출시_저장된_전체_앱_사용시간목록을_리턴한다() throws Exception {
        insertDummyData();

        List<AppUsage> appUsageList = subject.getAppUsages();

        Collections.sort(appUsageList, (o1, o2) -> o1.getPackageName().compareTo(o2.getPackageName()));
        assertEquals(appUsageList.size(), 3);
        assertEquals(appUsageList.get(0).getPackageName(), "com.package.name1");
        assertEquals(appUsageList.get(0).getTotalUsedTime(), 100L);
        assertEquals(appUsageList.get(1).getPackageName(), "com.package.name2");
        assertEquals(appUsageList.get(1).getTotalUsedTime(), 200L);
        assertEquals(appUsageList.get(2).getPackageName(), "com.package.name3");
        assertEquals(appUsageList.get(2).getTotalUsedTime(), 700L);
    }

    @Test
    public void deleteAppUsages호출시_인풋으로_받은날짜_이전_사용기록을_삭제한다() throws Exception {
        insertDummyData();

        subject.deleteAppUsages(20171118);

        RealmResults<AppUsageRealmObject> all = realm.where(AppUsageRealmObject.class).findAll().sort("yyyymmdd", Sort.ASCENDING, "packageName", Sort.ASCENDING);

        assertEquals(3, all.size());
        assertEquals("com.package.name2", all.get(0).getPackageName());
        assertEquals(200L, all.get(0).getTotalUsedTime());
        assertEquals("com.package.name3", all.get(1).getPackageName());
        assertEquals(300L, all.get(1).getTotalUsedTime());
        assertEquals("com.package.name3", all.get(2).getPackageName());
        assertEquals(400L, all.get(2).getTotalUsedTime());
    }

    private void insertDummyData() {
        realm.executeTransaction((realmInstance) -> {
            AppUsageRealmObject appUsageRealmObject1 = new AppUsageRealmObject();
            appUsageRealmObject1.setAppUsageKey("com.package.name120171117");
            appUsageRealmObject1.setPackageName("com.package.name1");
            appUsageRealmObject1.setYyyymmdd(20171117);
            appUsageRealmObject1.setTotalUsedTime(100L);
            realmInstance.copyToRealmOrUpdate(appUsageRealmObject1);

            AppUsageRealmObject appUsageRealmObject2 = new AppUsageRealmObject();
            appUsageRealmObject2.setAppUsageKey("com.package.name220171118");
            appUsageRealmObject2.setPackageName("com.package.name2");
            appUsageRealmObject2.setYyyymmdd(20171118);
            appUsageRealmObject2.setTotalUsedTime(200L);
            realmInstance.copyToRealmOrUpdate(appUsageRealmObject2);

            AppUsageRealmObject appUsageRealmObject3 = new AppUsageRealmObject();
            appUsageRealmObject3.setAppUsageKey("com.package.name320171118");
            appUsageRealmObject3.setPackageName("com.package.name3");
            appUsageRealmObject3.setYyyymmdd(20171118);
            appUsageRealmObject3.setTotalUsedTime(300L);
            realmInstance.copyToRealmOrUpdate(appUsageRealmObject3);

            AppUsageRealmObject appUsageRealmObject4 = new AppUsageRealmObject();
            appUsageRealmObject4.setAppUsageKey("com.package.name320171119");
            appUsageRealmObject4.setPackageName("com.package.name3");
            appUsageRealmObject4.setYyyymmdd(20171119);
            appUsageRealmObject4.setTotalUsedTime(400L);
            realmInstance.copyToRealmOrUpdate(appUsageRealmObject4);
        });
    }
}