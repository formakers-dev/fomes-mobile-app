package com.appbee.appbeemobile.helper;

import android.app.usage.UsageStats;
import android.support.annotation.NonNull;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.model.EventStat;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.usage.UsageEvents.Event.MOVE_TO_BACKGROUND;
import static android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppUsageDataHelperTest {
    private AppUsageDataHelper subject;

    @Captor
    ArgumentCaptor<Long> startTimeCaptor = ArgumentCaptor.forClass(Long.class);

    private AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;
    private LocalStorageHelper mockLocalStorageHelper;
    private AppRepositoryHelper mockAppRepositoryHelper;

    @Before
    public void setUp() throws Exception {
        this.mockAppBeeAndroidNativeHelper = mock(AppBeeAndroidNativeHelper.class);
        this.mockLocalStorageHelper = mock(LocalStorageHelper.class);
        this.mockAppRepositoryHelper = mock(AppRepositoryHelper.class);
        subject = new AppUsageDataHelper(mockAppBeeAndroidNativeHelper, mockLocalStorageHelper, mockAppRepositoryHelper);
    }

    @Test
    public void getLongTermStatsForYear호출시_연간_일별통계정보를_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockAppBeeAndroidNativeHelper.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        List<LongTermStat> actualResult = subject.getLongTermStats();

        assertEquals(actualResult.size(), 2);
    }

    @Test
    public void getLongTermStatsForYear호출시_동일앱의_일간사용정보가_두개이상인경우_사용시간을_합산하여_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 200L, 1499934615000L));    //2017-07-13 17:30:15
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 400L, 1500001200000L));    //2017-07-14 12:00:00
        when(mockAppBeeAndroidNativeHelper.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        List<LongTermStat> actualResult = subject.getLongTermStats();

        assertEquals(actualResult.size(), 2);
        assertEquals(actualResult.get(0).getTotalUsedTime(), 400L);
        assertEquals(actualResult.get(1).getTotalUsedTime(), 300L);
    }

    @Test
    public void getLongTermStatsForYear호출시_연간_일별통계정보_사용시간이_많은순서대로_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-13 12:00:00
        preStoredUsageStats.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00

        when(mockAppBeeAndroidNativeHelper.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        List<LongTermStat> actualResult = subject.getLongTermStats();

        assertEquals(actualResult.size(), 2);
        assertEquals(actualResult.get(0).getPackageName(), "bbbbb");
    }

    @Test
    public void getShortTermStats호출시_앱사용정보를_시간대별로_조회하여_리턴한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1100L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageA", 1000L, 1100L);
    }


    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행된경우_A앱의_실행시간은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1250L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1300L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageB", 1100L, 1250L);
    }

    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_기록이_종료된_경우_A앱의_사용기록은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1300L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertThat(shortTermStats.get(0).getPackageName()).isEqualTo("packageB");
    }

    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_B앱이_실행되고_B앱이_떠있는상태에서_C앱이_실행될경우_AB앱의_사용시간은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_FOREGROUND, 1200L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1250L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_BACKGROUND, 1375L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_BACKGROUND, 1400L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageC", 1200L, 1375L);
    }

    @Test
    public void getShortTermStats호출시_A앱이_떠있는상태에서_백그라운드로_가지않고_A앱이_다시실행되는경우_기존시작시간은_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1300L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

        assertThat(shortTermStats.size()).isEqualTo(1);
        assertConfirmDetailUsageStat(shortTermStats.get(0), "packageA", 1100L, 1300L);
    }

    @Test
    public void getShortTermStats호출시_여러앱이_FOREGROUND기록만있고_종료기록이없는경우_전체데이터를_무시한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageB", MOVE_TO_FOREGROUND, 1100L));
        mockEventStatList.add(new EventStat("packageC", MOVE_TO_FOREGROUND, 1300L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<ShortTermStat> shortTermStats = subject.getShortTermStats(0L);

        assertThat(shortTermStats.size()).isEqualTo(0);
    }

    @Test
    public void getAppList호출시_설치된_앱리스트조회를_요청한다() throws Exception {
        subject.getAppList();

        verify(mockAppBeeAndroidNativeHelper).getInstalledLaunchableApps();
    }

    @Test
    public void getShortTermStats호출시_파라미터로_넘어온_조회시작시간을_기준으로_통계데이터를_조회한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        subject.getShortTermStats(200L);

        verify(mockAppBeeAndroidNativeHelper).getUsageStatEvents(startTimeCaptor.capture(), anyLong());
        assertThat(startTimeCaptor.getValue()).isEqualTo(200L);
    }

    @Test
    public void getEventStats호출시_가공되지_않은_앱사용정보를_리턴한다() throws Exception {
        List<EventStat> mockEventStatList = new ArrayList<>();
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_FOREGROUND, 1000L));
        mockEventStatList.add(new EventStat("packageA", MOVE_TO_BACKGROUND, 1100L));
        when(mockAppBeeAndroidNativeHelper.getUsageStatEvents(anyLong(), anyLong())).thenReturn(mockEventStatList);

        List<EventStat> eventStatList = subject.getEventStats(0L);

        assertThat(eventStatList.size()).isEqualTo(2);
        assertThat(eventStatList.get(0).getPackageName()).isEqualTo("packageA");
        assertThat(eventStatList.get(0).getEventType()).isEqualTo(MOVE_TO_FOREGROUND);
        assertThat(eventStatList.get(0).getTimeStamp()).isEqualTo(1000L);
    }

    @Test
    public void getAppCountMessage호출시_알맞은_메세지를_리턴한다() throws Exception {
        assertThat(RuntimeEnvironment.application.getString(subject.getAppCountMessage(10))).contains("적기도 하네 진짜…");
        assertThat(RuntimeEnvironment.application.getString(subject.getAppCountMessage(200))).contains("적당도 하네 진짜…");
        assertThat(RuntimeEnvironment.application.getString(subject.getAppCountMessage(400))).contains("많기도 하네 진짜…");
    }

    @Test
    public void getAppUsageAverageTime호출시_앱사용평균시간을_리턴한다() throws Exception {
        when(mockLocalStorageHelper.getMinStartedStatTimeStamp()).thenReturn(1436788800000L); // 2015년 July 13일 Monday PM 12:00:00
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("com.package.name1", 5_000_000_000L, 1436788800000L));
        preStoredUsageStats.add(createMockUsageStats("com.package.name2", 8_000_000_000L, 1499934615000L));
        preStoredUsageStats.add(createMockUsageStats("com.package.name3", 9_000_000_000L, 1500001200000L));
        when(mockAppBeeAndroidNativeHelper.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);
        ShadowSystemClock.setCurrentTimeMillis(1503014400000L); // 2017년 August 18일 Friday AM 12:00:00

        List<LongTermStat> longTermStatList = subject.getLongTermStats();

        // (앱 사용시간의 총합 / 디바이스 총 사용일) 의 반올림.....
        // 앱 사용시간의 총합 = 22000000000 / 1000 / 60 / 60 = 6,111.1111111111
        // 디바이스 총 사용일 = (1503014400000 - 1436788800000) / 1000/ 60 / 60 / 24 = 766.5
        assertThat(subject.getAppUsageAverageHourPerDay(longTermStatList)).isEqualTo(8);
    }

    @Test
    public void getAppUsageAverageMessage호출시_알맞은_메세지를_리턴한다() throws Exception {
        assertThat(RuntimeEnvironment.application.getString(subject.getAppUsageAverageMessage(1))).contains("짱 적은 편");
        assertThat(RuntimeEnvironment.application.getString(subject.getAppUsageAverageMessage(5))).contains("짱 적당한 편");
        assertThat(RuntimeEnvironment.application.getString(subject.getAppUsageAverageMessage(10))).contains("짱 많은 편");
    }

    @Test
    public void getMostInstalledCategories호출시_많이_설치되어있는_카테고리순으로_정렬된_리스트를_요청한다() throws Exception {
        when(mockAppRepositoryHelper.getAppCountMapByCategory()).thenReturn(createCategoryDummyData());

        ArrayList<String> mostInstalledCategories = subject.getMostInstalledCategories(3);

        verify(mockAppRepositoryHelper).getAppCountMapByCategory();
        assertThat(mostInstalledCategories).isNotNull();
        assertThat(mostInstalledCategories.size()).isEqualTo(3);
        assertThat(mostInstalledCategories.get(0)).isEqualTo("사진");
        assertThat(mostInstalledCategories.get(1)).isEqualTo("쇼핑");
        assertThat(mostInstalledCategories.get(2)).isEqualTo("음악");
    }

    private Map<String, Integer> createCategoryDummyData() {
        Map<String, Integer> dummyCategoryData = new HashMap<>();
        dummyCategoryData.put("게임", 1);
        dummyCategoryData.put("사진", 5);
        dummyCategoryData.put("교육", 2);
        dummyCategoryData.put("음악", 3);
        dummyCategoryData.put("쇼핑", 4);
        return dummyCategoryData;
    }

    @Test
    public void getMostInstalledCategories호출시_요청한리스트크기가_설치된카테고리의수보다큰경우_설치된카테고리수만큼_리턴한다() throws Exception {
        when(mockAppRepositoryHelper.getAppCountMapByCategory()).thenReturn(createCategoryDummyData());

        ArrayList<String> mostInstalledCategories = subject.getMostInstalledCategories(6);

        assertThat(mostInstalledCategories).isNotNull();
        assertThat(mostInstalledCategories.size()).isEqualTo(5);
    }

    @Test
    public void getMostInstalledCategories호출시_카테고리맵이_비어있는경우_비어있는_리스트를_리턴한다() throws Exception {
        when(mockAppRepositoryHelper.getAppCountMapByCategory()).thenReturn(mock(Map.class));

        ArrayList<String> leastInstalledCategories = subject.getMostInstalledCategories(1);

        assertThat(leastInstalledCategories).isNotNull();
        assertThat(leastInstalledCategories.size()).isEqualTo(0);
    }

    @Test
    public void getLeastInstalledCategories호출시_가장_적게_설치된_카테고리리스트를_리턴한다() throws Exception {
        when(mockAppRepositoryHelper.getAppCountMapByCategory()).thenReturn(createCategoryDummyData());

        ArrayList<String> leastInstalledCategories = subject.getLeastInstalledCategories(1);

        assertThat(leastInstalledCategories).isNotNull();
        assertThat(leastInstalledCategories.size()).isEqualTo(1);
        assertThat(leastInstalledCategories.get(0)).isEqualTo("게임");
    }

    @Test
    public void getLeastInstalledCategories호출시_요청한리스트크기가_설치된카테고리의수보다큰경우_설치된카테고리수만큼만_리턴한다() throws Exception {
        when(mockAppRepositoryHelper.getAppCountMapByCategory()).thenReturn(createCategoryDummyData());

        ArrayList<String> leastInstalledCategories = subject.getLeastInstalledCategories(6);

        assertThat(leastInstalledCategories).isNotNull();
        assertThat(leastInstalledCategories.size()).isEqualTo(5);
    }

    @Test
    public void getLeastInstalledCategories호출시_카테고리맵이_비어있는경우_비어있는_리스트를_리턴한다() throws Exception {
        when(mockAppRepositoryHelper.getAppCountMapByCategory()).thenReturn(mock(Map.class));

        ArrayList<String> leastInstalledCategories = subject.getLeastInstalledCategories(1);

        assertThat(leastInstalledCategories).isNotNull();
        assertThat(leastInstalledCategories.size()).isEqualTo(0);
    }

    @Test
    public void 오름차순sortByValue호출시_입력된_맵의_값을_기준으로_오름차순_정렬된_맵을_리턴한다() throws Exception {
        Map<String, Integer> map = subject.sortByValue(createDummyMapForSortTest(), AppUsageDataHelper.ASC);

        assertSortedMap(map);

        Object[] keySet = map.keySet().toArray();
        assertEquals(keySet[0].toString(), "categoryId1");
        assertEquals(keySet[1].toString(), "categoryId3");
        assertEquals(keySet[2].toString(), "categoryId2");
    }

    @Test
    public void 내림차순sortByValue호출시_입력된_맵의_값을_기준으로_내림차순_정렬된_맵을_리턴한다() throws Exception {
        Map<String, Integer> map = subject.sortByValue(createDummyMapForSortTest(), AppUsageDataHelper.DESC);
        assertSortedMap(map);

        Object[] keySet = map.keySet().toArray();
        assertEquals(keySet[0].toString(), "categoryId2");
        assertEquals(keySet[1].toString(), "categoryId3");
        assertEquals(keySet[2].toString(), "categoryId1");
    }

    @Test
    public void getLongTermStatsSummary호출시_package별_totalUsedTime의_합을_리턴한다() throws Exception {
        List<UsageStats> preStoredUsageStats = new ArrayList<>();
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 100L, 1499914800000L));    //2017-07-12 12:00:00
        preStoredUsageStats.add(createMockUsageStats("bbbbb", 200L, 1500001200000L));    //2017-07-14 12:00:00
        preStoredUsageStats.add(createMockUsageStats("aaaaa", 300L, 1500001200000L));    //2017-07-14 12:00:00

        when(mockAppBeeAndroidNativeHelper.getUsageStats(anyLong(),anyLong())).thenReturn(preStoredUsageStats);

        Map map = subject.getLongTermStatsSummary();

        assertThat(map.get("aaaaa")).isEqualTo(400L);
        assertThat(map.get("bbbbb")).isEqualTo(200L);
    }

    @Test
    public void getAppInstallsByCategoryId호출시_categoryId별_install수를_리턴한다() throws Exception {
        when(mockAppRepositoryHelper.getAppCountByCategoryId(anyString())).thenReturn(100L);

        long installs = subject.getAppCountByCategoryId("categoryId");

        assertThat(installs).isEqualTo(100);
    }

    @Test
    public void getMostUsedSocialAppMessage호출시_app별_평가정보를_리턴한다() throws Exception {
        assertThat(subject.getMostUsedSocialAppMessage("Facebook")).contains("당신, 잘 살고 계시네요.");
        assertThat(subject.getMostUsedSocialAppMessage("네이버 블로그 - Naver Blog")).contains("당신은 세상의 훌륭한 이웃입니다. ");
        assertThat(subject.getMostUsedSocialAppMessage("티스토리 - TISTORY")).contains("당신은 세상의 훌륭한 이웃입니다. ");
        assertThat(subject.getMostUsedSocialAppMessage("브런치 - 좋은 글과 작가를 만나는 공간")).contains("당신은 세상의 훌륭한 이웃입니다. ");
        assertThat(subject.getMostUsedSocialAppMessage("Instagram")).contains("유행을 놓치지 않는 영한 감성의 소유자!");
        assertThat(subject.getMostUsedSocialAppMessage("카카오스토리 KakaoStory")).contains("혹시 자녀가 있으신가요?");
        assertThat(subject.getMostUsedSocialAppMessage("트위터")).contains("현실에는 말 못할 이야기가 많으신가요?");
        assertThat(subject.getMostUsedSocialAppMessage("밴드")).contains("여러가지 동아리를 하고 계신 액티브한 당신!");
        assertThat(subject.getMostUsedSocialAppMessage("커플앱 비트윈 - Between")).contains("정말 부러워요..전 모쏠이거든요..");
        assertThat(subject.getMostUsedSocialAppMessage("디폴트")).contains("요즘엔 이게 유행인가요?");
    }

    private void assertSortedMap(Map<String, Integer> map) {
        assertEquals(map.size(), 3);
        assertEquals(map.get("categoryId1"), Integer.valueOf(1));
        assertEquals(map.get("categoryId2"), Integer.valueOf(3));
        assertEquals(map.get("categoryId3"), Integer.valueOf(2));
    }

    @NonNull
    private Map<String, Integer> createDummyMapForSortTest() {
        Map<String, Integer> dummyMap = new HashMap<>();
        dummyMap.put("categoryId1", 1);
        dummyMap.put("categoryId2", 3);
        dummyMap.put("categoryId3", 2);
        return dummyMap;
    }

    private void assertConfirmDetailUsageStat(ShortTermStat shortTermStat, String packageName, long startTimeStamp, long endTimeStamp) {
        assertThat(shortTermStat.getPackageName()).isEqualTo(packageName);
        assertThat(shortTermStat.getStartTimeStamp()).isEqualTo(startTimeStamp);
        assertThat(shortTermStat.getEndTimeStamp()).isEqualTo(endTimeStamp);
        assertThat(shortTermStat.getTotalUsedTime()).isEqualTo(endTimeStamp - startTimeStamp);
    }

    @NonNull
    private UsageStats createMockUsageStats(String packageName, long totalTimeInForeground, long lastTimeUsed) {
        UsageStats mockUsageStats = mock(UsageStats.class);
        when(mockUsageStats.getPackageName()).thenReturn(packageName);
        when(mockUsageStats.getTotalTimeInForeground()).thenReturn(totalTimeInForeground);
        when(mockUsageStats.getLastTimeUsed()).thenReturn(lastTimeUsed);
        return mockUsageStats;
    }
}