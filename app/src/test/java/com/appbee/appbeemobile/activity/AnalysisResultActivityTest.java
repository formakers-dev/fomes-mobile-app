package com.appbee.appbeemobile.activity;


import android.app.Fragment;
import android.content.Intent;
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
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;
import com.appbee.appbeemobile.model.AnalysisResult;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.service.PowerConnectedService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import static com.appbee.appbeemobile.util.AppBeeConstants.CharacterType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

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

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Inject
    UserService mockUserService;

    @Inject
    AppStatService mockAppStatService;

    private Bitmap mockIconBitmap;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        mockDummyDataForAllFragment();
        mockDummyDataForOverviewFragment();
        mockDummyDataForBrainFragment();
        mockDummyDataForFlowerFragment();
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

        assertThat(bundle.getSerializable(OverviewFragment.EXTRA_CHARACTER_TYPE)).isEqualTo(CharacterType.QUEEN);
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
        assertThat(actualLeastInstalledCategories.get(0)).isEqualTo("데이트");

        assertThat(bundle.getString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY)).isEqualTo("사진 앱이 전체 앱 개수의 25%");
        assertThat(bundle.getString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION)).contains("늘 기록을 남기고 추억을 소중하게 여기는 당신.");
    }

    @Test
    public void 설치된카테고리데이터가없을경우_BrainFragment에_설치된정보가없음을_전달한다() throws Exception {
        mockNoInstallDummyData();

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment brainFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.BRAIN_FRAGMENT_TAG);
        assertThat(brainFragment).isNotNull();
        assertThat(brainFragment.isAdded()).isTrue();

        Bundle bundle = brainFragment.getArguments();
        ArrayList<String> actualMostUsedCategories = bundle.getStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES);
        assertThat(actualMostUsedCategories).isNotNull();
        assertThat(actualMostUsedCategories.size()).isEqualTo(0);

        ArrayList<String> actualLeastInstalledCategories = bundle.getStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES);
        assertThat(actualLeastInstalledCategories).isNotNull();
        assertThat(actualLeastInstalledCategories.size()).isEqualTo(0);

        assertThat(bundle.getString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY)).isEqualTo("당신, 어느별에서 오신거죠?!");
        assertThat(bundle.getString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION)).contains("분석할만한 충분한 카테고리가 보이지 않아요.");
    }

    @Test
    public void 카테고리가3개만설치된경우_BrainFragment에_가장적게사용한카테고리정보를_전달하지않는다() throws Exception {
        mockDummyDataForJust3CategoriesByInstalls();

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment brainFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.BRAIN_FRAGMENT_TAG);
        Bundle bundle = brainFragment.getArguments();

        ArrayList<String> actualLeastInstalledCategories = bundle.getStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES);
        assertThat(actualLeastInstalledCategories).isNotNull();
        assertThat(actualLeastInstalledCategories.size()).isEqualTo(0);
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
        assertThat(mostUsedTimeCategoryList.get(0)).isEqualTo("게임");
        assertThat(mostUsedTimeCategoryList.get(1)).isEqualTo("금융");
        assertThat(mostUsedTimeCategoryList.get(2)).isEqualTo("소셜");
        ArrayList<String> leastUsedTimeCategory = bundle.getStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES);
        assertThat(leastUsedTimeCategory).isNotNull();
        assertThat(leastUsedTimeCategory.get(0)).isEqualTo("쇼핑");

        assertThat(bundle.getString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY)).isEqualTo("게임이 전체 앱 사용 시간의 46%");
        assertThat(bundle.getString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC)).contains("재미있는 것에 몰두하는 당신.");
    }

    @Test
    public void onCreate_앱시작시_UUID정보가_없으면_SharedPreference에_저장한다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        verify(mockLocalStorageHelper).setUUID(anyString());
    }

    @Test
    public void onCreate_앱시작시_서버로_user정보를_전송한다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);
        verify(mockUserService).sendUser(any(User.class));
    }

    @Test
    public void onCreate_앱시작시_UUID가sharedPreferences에저장되어있지않을경우_PowerConnectedService를_시작한다() throws Exception {
        when(mockLocalStorageHelper.getUUID()).thenReturn(null);

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Intent nextStartedService = shadowOf(RuntimeEnvironment.application).getNextStartedService();
        assertThat(nextStartedService.getComponent().getClassName()).contains(PowerConnectedService.class.getSimpleName());
    }

    @Test
    public void onCreate_앱시작시_서버로_앱정보를_전송한다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);
        verify(mockAppStatService).sendLongTermStatsFor3Months();
        verify(mockAppStatService).sendLongTermStatsFor2Years();
        verify(mockAppStatService).sendShortTermStats();
        verify(mockUserService).sendAppList();
    }

    @Test
    public void onCreate_앱시작시_서버로_분석결과를_전송한다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        ArgumentCaptor<AnalysisResult> analysisResultArgumentCaptor = ArgumentCaptor.forClass(AnalysisResult.class);
        verify(mockAppStatService).sendAnalysisResult(analysisResultArgumentCaptor.capture());

        AnalysisResult analysisResult = analysisResultArgumentCaptor.getValue();
        assertThat(analysisResult.getCharacterType()).isEqualTo("QUEEN");
        assertThat(analysisResult.getTotalInstalledAppCount()).isEqualTo(4);
        assertThat(analysisResult.getAverageUsedMinutesPerDay()).isEqualTo(480);
        assertThat(analysisResult.getMostUsedApp()).isEqualTo("com.package.name1");
        assertThat(analysisResult.getMostDownloadCategories().size()).isEqualTo(3);
        assertThat(analysisResult.getMostDownloadCategories().get(0)).isEqualTo("/store/apps/category/PHOTOGRAPHY");
        assertThat(analysisResult.getMostDownloadCategories().get(1)).isEqualTo("/store/apps/category/SHOPPING");
        assertThat(analysisResult.getMostDownloadCategories().get(2)).isEqualTo("/store/apps/category/MUSIC_AND_AUDIO");
        assertThat(analysisResult.getLeastDownloadCategory()).isEqualTo("/store/apps/category/DATING");
        assertThat(analysisResult.getMostUsedCategories().size()).isEqualTo(4);
        assertThat(analysisResult.getMostUsedCategories().get(0)).isEqualTo("/store/apps/category/GAME");
        assertThat(analysisResult.getMostUsedCategories().get(1)).isEqualTo("/store/apps/category/FINANCE");
        assertThat(analysisResult.getMostUsedCategories().get(2)).isEqualTo("/store/apps/category/SOCIAL");
        assertThat(analysisResult.getMostUsedCategories().get(3)).isEqualTo("/store/apps/category/SHOPPING");
        assertThat(analysisResult.getLeastUsedCategory()).isEqualTo("/store/apps/category/SHOPPING");
    }

    @Test
    public void 가장많이사용한카테고리와가장많이설치된카테고리가동일한경우_FlowerFragment에_관련정보를전달한다() throws Exception {
        mockDummyDataWhenMostInstalledAndMostUsedTimeCategoryIsSame();

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment flowerFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.FLOWER_FRAGMENT_TAG);
        assertThat(flowerFragment).isNotNull();
        assertThat(flowerFragment.isAdded()).isTrue();

        Bundle bundle = flowerFragment.getArguments();
        assertThat(bundle).isNotNull();

        assertThat(bundle.getString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC)).contains("다른 사람보다 일관성이 있으신 분인 것 같아요!");
    }

    @Test
    public void 설치된카테고리데이터가없을경우_FlowerFragment가_설치된정보가없음을_표시한다() throws Exception {
        mockNoUsedCategoryDummyData();

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment flowerFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.FLOWER_FRAGMENT_TAG);
        assertThat(flowerFragment).isNotNull();
        assertThat(flowerFragment.isAdded()).isTrue();

        Bundle bundle = flowerFragment.getArguments();
        assertThat(bundle).isNotNull();

        ArrayList<String> mostUsedTimeCategoryList = bundle.getStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES);
        assertThat(mostUsedTimeCategoryList).isNotNull();
        assertThat(mostUsedTimeCategoryList.size()).isEqualTo(0);

        ArrayList<String> leastUsedTimeCategory = bundle.getStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES);
        assertThat(leastUsedTimeCategory).isNotNull();
        assertThat(leastUsedTimeCategory.size()).isEqualTo(0);

        assertThat(bundle.getString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY)).isEqualTo("아이코, 꽃이 시들어 버렸어요.");
        assertThat(bundle.getString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC)).contains("가지고 있는 앱의 카테고리 수가 충분치 않거나");
    }

    @Test
    public void 사용한카테고리가3개만_존재하는경우_FlowerFragment에_가장_적게사용한_카테고리_정보를_전달하지_않는다() throws Exception {
        mockDummyDataForJust3CategoriesByUsedTime();

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment flowerFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.FLOWER_FRAGMENT_TAG);
        assertThat(flowerFragment).isNotNull();

        Bundle bundle = flowerFragment.getArguments();
        ArrayList<String> leastUsedTimeCategoryList = bundle.getStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES);
        assertThat(leastUsedTimeCategoryList).isNotNull();
        assertThat(leastUsedTimeCategoryList).isEmpty();
    }

    @Test
    public void 가장적게사용한카테고리가_가장많이사용한카테고리에_포함되는경우_FlowerFragment에_가장적게사용한카테고리정보를_전달하지않는다() throws Exception {
        mockDummyDataForJust3CategoriesByInstalls();

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        Fragment brainFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.BRAIN_FRAGMENT_TAG);
        Bundle bundle = brainFragment.getArguments();

        ArrayList<String> actualLeastInstalledCategories = bundle.getStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES);
        assertThat(actualLeastInstalledCategories).isNotNull();
        assertThat(actualLeastInstalledCategories.size()).isEqualTo(0);
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

        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FINANCE")).isEqualTo(subject.getString(R.string.longest_used_app_desc_finance));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_EDUCATIONAL")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_WORD")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ROLE_PLAYING")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_BOARD")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_SPORTS")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_SIMULATION")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ARCADE")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ACTION")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_ADVENTURE")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_MUSIC")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_RACING")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_STRATEGY")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_CARD")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_CASINO")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_CASUAL")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_TRIVIA")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/GAME_PUZZLE")).isEqualTo(subject.getString(R.string.longest_used_app_desc_game));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/MUSIC_AND_AUDIO")).isEqualTo(subject.getString(R.string.longest_used_app_desc_music_video));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/VIDEO_PLAYERS")).isEqualTo(subject.getString(R.string.longest_used_app_desc_music_video));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/SOCIAL")).isEqualTo(subject.getString(R.string.longest_used_app_desc_social));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/PHOTOGRAPHY")).isEqualTo(subject.getString(R.string.longest_used_app_desc_photography));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/PERSONALIZATION")).isEqualTo(subject.getString(R.string.longest_used_app_desc_personalization));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/SHOPPING")).isEqualTo(subject.getString(R.string.longest_used_app_desc_shopping));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/COMMUNICATION")).isEqualTo(subject.getString(R.string.longest_used_app_desc_communication));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/ENTERTAINMENT")).isEqualTo(subject.getString(R.string.longest_used_app_desc_entertainment));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/HEALTH_AND_FITNESS")).isEqualTo(subject.getString(R.string.longest_used_app_desc_health_sports));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/SPORTS")).isEqualTo(subject.getString(R.string.longest_used_app_desc_health_sports));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/EDUCATION")).isEqualTo(subject.getString(R.string.longest_used_app_desc_education));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/WEATHER")).isEqualTo(subject.getString(R.string.longest_used_app_desc_weather));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/TRAVEL_AND_LOCAL")).isEqualTo(subject.getString(R.string.longest_used_app_desc_travel));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/BUSINESS")).isEqualTo(subject.getString(R.string.longest_used_app_desc_business_productivity));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/PRODUCTIVITY")).isEqualTo(subject.getString(R.string.longest_used_app_desc_business_productivity));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/TOOLS")).isEqualTo(subject.getString(R.string.longest_used_app_desc_tools));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/BOOKS_AND_REFERENCE")).isEqualTo(subject.getString(R.string.longest_used_app_desc_book_news));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/NEWS_AND_MAGAZINES")).isEqualTo(subject.getString(R.string.longest_used_app_desc_book_news));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/LIBRARIES_AND_DEMO")).isEqualTo(subject.getString(R.string.longest_used_app_desc_library));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/LIFESTYLE")).isEqualTo(subject.getString(R.string.longest_used_app_desc_lifestyle));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/COMICS")).isEqualTo(subject.getString(R.string.longest_used_app_desc_comics));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/HOUSE_AND_HOME")).isEqualTo(subject.getString(R.string.longest_used_app_desc_house));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/BEAUTY")).isEqualTo(subject.getString(R.string.longest_used_app_desc_beauty_design));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/ART_AND_DESIGN")).isEqualTo(subject.getString(R.string.longest_used_app_desc_beauty_design));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/DATING")).isEqualTo(subject.getString(R.string.longest_used_app_desc_dating));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FAMILY")).isEqualTo(subject.getString(R.string.longest_used_app_desc_kids));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FAMILY_EDUCATION")).isEqualTo(subject.getString(R.string.longest_used_app_desc_kids));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FAMILY_BRAINGAMES")).isEqualTo(subject.getString(R.string.longest_used_app_desc_kids));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FAMILY_ACTION")).isEqualTo(subject.getString(R.string.longest_used_app_desc_kids));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FAMILY_PRETEND")).isEqualTo(subject.getString(R.string.longest_used_app_desc_kids));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FAMILY_MUSICVIDEO")).isEqualTo(subject.getString(R.string.longest_used_app_desc_kids));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FAMILY_CREATE")).isEqualTo(subject.getString(R.string.longest_used_app_desc_kids));
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/MEDICAL")).isEqualTo("조금은 특수한 상황에 처해있는 당신. 이 순간도 즐기면 좋겠네요.");
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/FOOD_AND_DRINK")).isEqualTo("다 먹고 살자고 하는 일이죠! 말하다보니 배가 고프네요..");
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/EVENTS")).isEqualTo("늘 재미있는 것들을 찾으시나요? 당신은 좀 특이한분 이시네요!");
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/AUTO_AND_VEHICLES")).isEqualTo("자동차는 정말 편리하고 안락하죠. 항상 안전운전 하세요 :)");
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/MAPS_AND_NAVIGATION")).isEqualTo("차막히는것은 딱 질색! 효율성을 추구하는 사람이에요.");
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/PARENTING")).isEqualTo("부모님은 세상에서 가장 위대한 존재지요. 당신에게 찾아온 축복을 축하드려요.");
        assertThat(subject.getLongestUsedAppDescription("/store/apps/category/ANDROID_WEAR")).isEqualTo("얼리어답터이시네요! 앱비가 찾는 트랜드 리더!!");
    }

    @Test
    public void getMostUsedCategoryDesc호출시_해당_카테고리ID에_해당하는_설명이_리턴된다() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);

        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FINANCE")).isEqualTo(subject.getString(R.string.brain_flower_desc_finance));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_EDUCATIONAL")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_WORD")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_ROLE_PLAYING")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_BOARD")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_SPORTS")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_SIMULATION")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_ARCADE")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_ACTION")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_ADVENTURE")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_MUSIC")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_RACING")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_STRATEGY")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_CARD")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_CASINO")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_CASUAL")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_TRIVIA")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/GAME_PUZZLE")).isEqualTo(subject.getString(R.string.brain_flower_desc_game));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/MUSIC_AND_AUDIO")).isEqualTo(subject.getString(R.string.brain_flower_desc_music));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/VIDEO_PLAYERS")).isEqualTo(subject.getString(R.string.brain_flower_desc_video));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/SOCIAL")).isEqualTo(subject.getString(R.string.brain_flower_desc_social));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/PHOTOGRAPHY")).isEqualTo(subject.getString(R.string.brain_flower_desc_photography));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/PERSONALIZATION")).isEqualTo(subject.getString(R.string.brain_flower_desc_personalization));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/SHOPPING")).isEqualTo(subject.getString(R.string.brain_flower_desc_shopping));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/COMMUNICATION")).isEqualTo(subject.getString(R.string.brain_flower_desc_communication));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/ENTERTAINMENT")).isEqualTo(subject.getString(R.string.brain_flower_desc_entertainment));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/HEALTH_AND_FITNESS")).isEqualTo(subject.getString(R.string.brain_flower_desc_health_sports));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/SPORTS")).isEqualTo(subject.getString(R.string.brain_flower_desc_health_sports));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/EDUCATION")).isEqualTo(subject.getString(R.string.brain_flower_desc_education));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/WEATHER")).isEqualTo(subject.getString(R.string.brain_flower_desc_weather));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/TRAVEL_AND_LOCAL")).isEqualTo(subject.getString(R.string.brain_flower_desc_travel));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/BUSINESS")).isEqualTo(subject.getString(R.string.brain_flower_desc_business_producitivity));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/PRODUCTIVITY")).isEqualTo(subject.getString(R.string.brain_flower_desc_business_producitivity));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/TOOLS")).isEqualTo(subject.getString(R.string.brain_flower_desc_tools));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/BOOKS_AND_REFERENCE")).isEqualTo(subject.getString(R.string.brain_flower_desc_book_news));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/NEWS_AND_MAGAZINES")).isEqualTo(subject.getString(R.string.brain_flower_desc_book_news));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/LIBRARIES_AND_DEMO")).isEqualTo(subject.getString(R.string.brain_flower_desc_library));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/LIFESTYLE")).isEqualTo(subject.getString(R.string.brain_flower_desc_lifestyle));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/COMICS")).isEqualTo(subject.getString(R.string.brain_flower_desc_comics));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/HOUSE_AND_HOME")).isEqualTo(subject.getString(R.string.brain_flower_desc_house));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/BEAUTY")).isEqualTo(subject.getString(R.string.brain_flower_desc_beauty_design));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/ART_AND_DESIGN")).isEqualTo(subject.getString(R.string.brain_flower_desc_beauty_design));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/DATING")).isEqualTo(subject.getString(R.string.brain_flower_desc_dating));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FAMILY")).isEqualTo(subject.getString(R.string.brain_flower_desc_kids));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FAMILY_EDUCATION")).isEqualTo(subject.getString(R.string.brain_flower_desc_kids));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FAMILY_BRAINGAMES")).isEqualTo(subject.getString(R.string.brain_flower_desc_kids));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FAMILY_ACTION")).isEqualTo(subject.getString(R.string.brain_flower_desc_kids));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FAMILY_PRETEND")).isEqualTo(subject.getString(R.string.brain_flower_desc_kids));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FAMILY_MUSICVIDEO")).isEqualTo(subject.getString(R.string.brain_flower_desc_kids));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FAMILY_CREATE")).isEqualTo(subject.getString(R.string.brain_flower_desc_kids));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/MEDICAL")).isEqualTo(subject.getString(R.string.brain_flower_desc_medical));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/FOOD_AND_DRINK")).isEqualTo(subject.getString(R.string.brain_flower_desc_food_and_drink));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/EVENTS")).isEqualTo(subject.getString(R.string.brain_flower_desc_events));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/AUTO_AND_VEHICLES")).isEqualTo(subject.getString(R.string.brain_flower_desc_auto_and_vehicles));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/MAPS_AND_NAVIGATION")).isEqualTo(subject.getString(R.string.brain_flower_desc_maps_and_navigation));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/PARENTING")).isEqualTo(subject.getString(R.string.brain_flower_desc_parenting));
        assertThat(subject.getMostUsedCategoryDesc("/store/apps/category/ANDROID_WEAR")).isEqualTo(subject.getString(R.string.brain_flower_desc_android_wear));
    }

    private void mockDummyDataForAllFragment() {
        ArrayList<String> mostUsedCategories = new ArrayList<>();
        mostUsedCategories.add("/store/apps/category/PHOTOGRAPHY");
        mostUsedCategories.add("/store/apps/category/SHOPPING");
        mostUsedCategories.add("/store/apps/category/MUSIC_AND_AUDIO");
        when(appUsageDataHelper.getMostInstalledCategoryGroups(anyInt())).thenReturn(mostUsedCategories);

        List<AppInfo> appInfoList = new ArrayList<>();
        appInfoList.add(new AppInfo("com.package.name1", "app_name_1", "/store/apps/category/GAME", null, null, null));
        appInfoList.add(new AppInfo("com.package.name2", "app_name_2", "category_id_2", null, null, null));
        appInfoList.add(new AppInfo("com.package.name3", "app_name_3", null, null, null, null));
        appInfoList.add(new AppInfo("com.package.name4", "app_name_4", null, null, null, null));

        when(appUsageDataHelper.getSortedUsedAppsByTotalUsedTime()).thenReturn(appInfoList);
    }

    private void mockDummyDataForOverviewFragment() {
        List<LongTermStat> longTermStats = new ArrayList<>();
        longTermStats.add(new LongTermStat("com.package.test", "", 999_999_999L));
        when(appUsageDataHelper.getLongTermStats()).thenReturn(longTermStats);

        when(appUsageDataHelper.getAppUsageAverageMinutesPerDay(any())).thenReturn(480);

        mockNativeAppInfo(true);

        when(appUsageDataHelper.getCharacterType()).thenReturn(CharacterType.QUEEN);
    }

    private void mockDummyDataForBrainFragment() {


        ArrayList<String> leastUsedCategories = new ArrayList<>();
        leastUsedCategories.add("/store/apps/category/DATING");
        when(appUsageDataHelper.getLeastInstalledCategoryGroups(anyInt())).thenReturn(leastUsedCategories);

        when(appUsageDataHelper.getAppCountByCategoryId("/store/apps/category/PHOTOGRAPHY")).thenReturn(1L);
    }

    private void mockDummyDataForFlowerFragment() {
        Map<String, Long> usedTimeCategoryMap = new LinkedHashMap<>();
        usedTimeCategoryMap.put("/store/apps/category/GAME", 3000L);
        usedTimeCategoryMap.put("/store/apps/category/FINANCE", 2000L);
        usedTimeCategoryMap.put("/store/apps/category/SOCIAL", 1000L);
        usedTimeCategoryMap.put("/store/apps/category/SHOPPING", 500L);
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

    private void mockNoInstallDummyData() {
        ArrayList<String> emptyList = new ArrayList<>();
        when(appUsageDataHelper.getMostInstalledCategoryGroups(anyInt())).thenReturn(emptyList);
        when(appUsageDataHelper.getLeastInstalledCategoryGroups(anyInt())).thenReturn(emptyList);
    }

    private void mockNoUsedCategoryDummyData() {
        Map<String, Long> emptyCategoryMap = new LinkedHashMap<>();
        when(appUsageDataHelper.getSortedCategoriesByUsedTime()).thenReturn(emptyCategoryMap);

        ArrayList<String> emptyList = new ArrayList<>();
        when(appUsageDataHelper.getMostInstalledCategoryGroups(anyInt())).thenReturn(emptyList);
    }

    private void mockDummyDataForJust3CategoriesByInstalls() {
        ArrayList<String> mostUsedCategories = new ArrayList<>();
        mostUsedCategories.add("/store/apps/category/PHOTOGRAPHY");
        mostUsedCategories.add("/store/apps/category/SHOPPING");
        mostUsedCategories.add("/store/apps/category/MUSIC_AND_AUDIO");
        when(appUsageDataHelper.getMostInstalledCategoryGroups(anyInt())).thenReturn(mostUsedCategories);

        ArrayList<String> leastUsedCategories = new ArrayList<>();
        leastUsedCategories.add("/store/apps/category/MUSIC_AND_AUDIO");
        when(appUsageDataHelper.getLeastInstalledCategoryGroups(anyInt())).thenReturn(leastUsedCategories);
    }

    private void mockDummyDataForJust3CategoriesByUsedTime() {
        Map<String, Long> usedTimeCategoryMap = new LinkedHashMap<>();
        usedTimeCategoryMap.put("/store/apps/category/GAME", 3000L);
        usedTimeCategoryMap.put("/store/apps/category/FINANCE", 2000L);
        usedTimeCategoryMap.put("/store/apps/category/SOCIAL", 1000L);
        when(appUsageDataHelper.getSortedCategoriesByUsedTime()).thenReturn(usedTimeCategoryMap);
    }

    private void mockDummyDataWhenMostInstalledAndMostUsedTimeCategoryIsSame() {
        ArrayList<String> mostUsedCategories = new ArrayList<>();
        mostUsedCategories.add("/store/apps/category/GAME");
        mostUsedCategories.add("/store/apps/category/SHOPPING");
        mostUsedCategories.add("/store/apps/category/MUSIC_AND_AUDIO");
        when(appUsageDataHelper.getMostInstalledCategoryGroups(anyInt())).thenReturn(mostUsedCategories);
    }
}