package com.appbee.appbeemobile.activity;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.FlowerFragment;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.appbee.appbeemobile.util.AppBeeConstants.APP_LIST_COUNT_TYPE;
import static com.appbee.appbeemobile.util.AppBeeConstants.APP_USAGE_TIME_TYPE;
import static com.appbee.appbeemobile.util.AppBeeConstants.CHARACTER_TYPE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(resourceDir = "src/main/res", constants = BuildConfig.class)
public class AnalysisResultActivityTest extends ActivityTest {
    private AnalysisResultActivity subject;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppRepositoryHelper appRepositoryHelper;

    @Inject
    NativeAppInfoHelper mockNativeAppInfoHelper;

    private Bitmap mockIconBitmap;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        mockDummyData();
    }

    @Test
    public void onCreate앱시작시_OverViewFragment가_나타난다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment fragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.OVERVIEW_FRAGMENT_TAG);
        assertThat(fragment).isNotNull();
        assertThat(fragment.isAdded()).isTrue();

        Bundle bundle = fragment.getArguments();
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_LIST_COUNT)).isEqualTo(4);
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_LIST_COUNT_TYPE)).isEqualTo(APP_LIST_COUNT_TYPE.LEAST);
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_AVG_TIME)).isEqualTo(480);
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_USAGE_TIME_TYPE)).isEqualTo(APP_USAGE_TIME_TYPE.MOST);
        assertThat(bundle.getString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME)).isEqualTo("app_name_1");
        assertThat(bundle.getString(OverviewFragment.EXTRA_LONGEST_USED_APP_DESCRIPTION)).isEqualTo("역시 당신은 덕…아니, 게이머라구요.");
        assertThat(((Bitmap) bundle.getParcelable(OverviewFragment.EXTRA_LONGEST_USED_APP_ICON_BITMAP))).isEqualTo(mockIconBitmap);

        assertThat(bundle.getInt(OverviewFragment.EXTRA_CHARACTER_TYPE)).isEqualTo(CHARACTER_TYPE.QUEEN);
    }

    @Test
    public void OverViewFragment생성시_아이콘이없는_앱은_아이콘비트맵정보를_전달하지_않는다() throws Exception {
        mockNativeAppInfo(false);
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment fragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.OVERVIEW_FRAGMENT_TAG);
        Bundle bundle = fragment.getArguments();
        assertThat(bundle.containsKey(OverviewFragment.EXTRA_LONGEST_USED_APP_ICON_BITMAP)).isFalse();
    }

    @Test
    public void onCreate앱시작시_BrainFragment가_나타난다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment brainFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.BRAIN_FRAGMENT_TAG);
        assertThat(brainFragment).isNotNull();
        assertThat(brainFragment.isAdded()).isTrue();

        Bundle bundle = brainFragment.getArguments();
        ArrayList<String> actualMostUsedCategories = bundle.getStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES);
        assertThat(actualMostUsedCategories).isNotNull();
        assertThat(actualMostUsedCategories.size()).isEqualTo(3);
        assertThat(actualMostUsedCategories.get(0)).isEqualTo("사진");
        assertThat(actualMostUsedCategories.get(1)).isEqualTo("쇼핑");
        assertThat(actualMostUsedCategories.get(2)).isEqualTo("음악");

        ArrayList<String> actualLeastInstalledCategories = bundle.getStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES);
        assertThat(actualLeastInstalledCategories).isNotNull();
        assertThat(actualLeastInstalledCategories.size()).isEqualTo(1);
        assertThat(actualLeastInstalledCategories.get(0)).isEqualTo("고양이");

        assertThat(bundle.getInt(BrainFragment.EXTRA_INSTALLED_APP_COUNT)).isEqualTo(4);
        assertThat(bundle.getLong(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_RATE)).isEqualTo(25);
    }

    @Test
    public void onCreate_앱시작시_FlowerFragment가_나타난다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment flowerFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.FLOWER_FRAGMENT_TAG);
        assertThat(flowerFragment).isNotNull();
        assertThat(flowerFragment.isAdded()).isTrue();

        Bundle bundle = flowerFragment.getArguments();
        assertThat(bundle).isNotNull();

        ArrayList<String> mostUsedTimeCategoryList = bundle.getStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES);
        assertThat(mostUsedTimeCategoryList).isNotNull();
        assertThat(mostUsedTimeCategoryList.size()).isEqualTo(3);
        assertThat(mostUsedTimeCategoryList.get(0)).isEqualTo("com.package.name1");
        assertThat(mostUsedTimeCategoryList.get(1)).isEqualTo("com.package.name2");
        assertThat(mostUsedTimeCategoryList.get(2)).isEqualTo("com.package.name3");
        ArrayList<String> leastUsedTimeCategory = bundle.getStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES);
        assertThat(leastUsedTimeCategory).isNotNull();
        assertThat(leastUsedTimeCategory.get(0)).isEqualTo("com.package.name4");

        assertThat(bundle.getLong(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_RATE)).isEqualTo(46L);
    }

    @Test
    public void onCreate_앱시작시_ShareFragment가_나타난다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment shareFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.SHARE_FRAGMENT_TAG);
        assertThat(shareFragment).isNotNull();
        assertThat(shareFragment.isAdded()).isTrue();

        Button button = (Button) shareFragment.getView().findViewById(R.id.share_button);
        assertThat(button.isShown()).isTrue();
    }

    @Test
    public void getAppUsageTimeType호출시_앱사용시간별_타입을_리턴한다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        assertThat(subject.getAppUsageTimeType(0)).isEqualTo(APP_USAGE_TIME_TYPE.LEAST);
        assertThat(subject.getAppUsageTimeType(30)).isEqualTo(APP_USAGE_TIME_TYPE.LEAST);
        assertThat(subject.getAppUsageTimeType(60)).isEqualTo(APP_USAGE_TIME_TYPE.LESS);
        assertThat(subject.getAppUsageTimeType(120)).isEqualTo(APP_USAGE_TIME_TYPE.NORMAL);
        assertThat(subject.getAppUsageTimeType(240)).isEqualTo(APP_USAGE_TIME_TYPE.MORE);
        assertThat(subject.getAppUsageTimeType(480)).isEqualTo(APP_USAGE_TIME_TYPE.MOST);
        assertThat(subject.getAppUsageTimeType(600)).isEqualTo(APP_USAGE_TIME_TYPE.MOST);
    }

    @Test
    public void getAppCountType호출시_설치앱개수별_타입을_리턴한다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        assertThat(subject.getAppCountType(0)).isEqualTo(APP_LIST_COUNT_TYPE.LEAST);
        assertThat(subject.getAppCountType(10)).isEqualTo(APP_LIST_COUNT_TYPE.LEAST);
        assertThat(subject.getAppCountType(25)).isEqualTo(APP_LIST_COUNT_TYPE.LESS);
        assertThat(subject.getAppCountType(50)).isEqualTo(APP_LIST_COUNT_TYPE.NORMAL);
        assertThat(subject.getAppCountType(100)).isEqualTo(APP_LIST_COUNT_TYPE.MORE);
        assertThat(subject.getAppCountType(150)).isEqualTo(APP_LIST_COUNT_TYPE.MOST);
        assertThat(subject.getAppCountType(200)).isEqualTo(APP_LIST_COUNT_TYPE.MOST);
    }

    @Test
    public void getLongestUsedAppDescription호출시_카테고리별_description을_리턴한다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FINANCE")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[0]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_EDUCATIONAL")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_WORD")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ROLE_PLAYING")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_BOARD")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_SPORTS")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_SIMULATION")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ARCADE")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ACTION")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ADVENTURE")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_MUSIC")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_RACING")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_STRATEGY")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_CARD")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_CASINO")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_CASUAL")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_TRIVIA")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_PUZZLE")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[1]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/VIDEO_PLAYERS")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[2]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/MUSIC_AND_AUDIO")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[2]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/SOCIAL")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[3]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/PHOTOGRAPHY")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[4]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/PERSONALIZATION")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[5]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/SHOPPING")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[6]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/COMMUNICATION")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[7]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/ENTERTAINMENT")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[8]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/HEALTH_AND_FITNESS")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[9]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/SPORTS")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[9]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/EDUCATION")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[10]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/WEATHER")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[11]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/TRAVEL_AND_LOCAL")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[12]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/BUSINESS")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[13]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/PRODUCTIVITY")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[13]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/TOOLS")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[14]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/BOOKS_AND_REFERENCE")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[15]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/NEWS_AND_MAGAZINES")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[15]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/LIBRARIES_AND_DEMO")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[16]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/LIFESTYLE")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[17]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/COMICS")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[18]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/HOUSE_AND_HOME")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[19]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/BEAUTY")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[20]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/ART_AND_DESIGN")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[20]);
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/DATING")).isEqualTo(subject.getResources().getStringArray(R.array.longest_used_app_category_descriptions)[21]);
    }

    private void mockDummyData() {

        mockNativeAppInfo(true);

        List<LongTermStat> longTermStats = new ArrayList<>();
        longTermStats.add(new LongTermStat("com.package.test", "", 999_999_999L));

        List<AppInfo> appInfoList = new ArrayList<>();
        appInfoList.add(new AppInfo("com.package.name1", "app_name_1", "/store/apps/category/GAME", null, null, null));
        appInfoList.add(new AppInfo("com.package.name2", "app_name_2", "category_id_2", null, null, null));
        appInfoList.add(new AppInfo("com.package.name3", "app_name_3", null, null, null, null));
        appInfoList.add(new AppInfo("com.package.name4", "app_name_4", null, null, null, null));

        when(appUsageDataHelper.getSortedUsedAppsByTotalUsedTime()).thenReturn(appInfoList);

        when(appUsageDataHelper.getAppUsageAverageMinutesPerDay(any())).thenReturn(480);
        when(appUsageDataHelper.getLongTermStats()).thenReturn(longTermStats);

        when(appUsageDataHelper.getCharacterType()).thenReturn(CHARACTER_TYPE.QUEEN);
        ArrayList<String> mostUsedCategories = new ArrayList<>();
        mostUsedCategories.add("사진");
        mostUsedCategories.add("쇼핑");
        mostUsedCategories.add("음악");
        when(appUsageDataHelper.getMostInstalledCategories(anyInt())).thenReturn(mostUsedCategories);

        ArrayList<String> leastUsedCategories = new ArrayList<>();
        leastUsedCategories.add("고양이");
        when(appUsageDataHelper.getLeastInstalledCategories(anyInt())).thenReturn(leastUsedCategories);

        when(appUsageDataHelper.getAppCountByCategoryId("사진")).thenReturn(1L);
        when(appUsageDataHelper.getAppCountByCategoryId("com.package.name1")).thenReturn(2L);

        Map<String, Long> usedTimeCategoryMap = new LinkedHashMap<>();
        usedTimeCategoryMap.put("com.package.name1", 3000L);
        usedTimeCategoryMap.put("com.package.name2", 2000L);
        usedTimeCategoryMap.put("com.package.name3", 1000L);
        usedTimeCategoryMap.put("com.package.name4", 500L);
        when(appUsageDataHelper.getSortedCategoriesByUsedTime()).thenReturn(usedTimeCategoryMap);
    }

    private void mockNativeAppInfo(boolean hasIconDrawable) {
        NativeAppInfo nativeApp1 = new NativeAppInfo("com.package.name1", "app_name_1");
        if (hasIconDrawable) {
            mockIconBitmap = mock(Bitmap.class);
            Drawable iconDrawable = new BitmapDrawable(RuntimeEnvironment.application.getResources(), mockIconBitmap);
            nativeApp1.setIcon(iconDrawable);
        }
        when(mockNativeAppInfoHelper.getNativeAppInfo("com.package.name1")).thenReturn(nativeApp1);
    }
}