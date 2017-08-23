package com.appbee.appbeemobile.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.fragment.BrainFragment;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.appbee.appbeemobile.util.AppBeeConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(resourceDir = "src/main/res", constants = BuildConfig.class)
public class AnalysisResultActivityTest extends ActivityTest {
    private AnalysisResultActivity subject;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppRepositoryHelper appRepositoryHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);

        mockDummyData();

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);
    }

    @Test
    public void onCreate앱시작시_OverViewFragment가_나타난다() throws Exception {
        Fragment fragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.OVERVIEW_FRAGMENT_TAG);
        assertThat(fragment).isNotNull();
        assertThat(fragment.isAdded()).isTrue();

        Bundle bundle = fragment.getArguments();
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_LIST_COUNT)).isEqualTo(4);
        assertThat(bundle.getString(OverviewFragment.EXTRA_APP_LIST_COUNT_MSG)).isEqualTo("적기도 하네 진짜…");
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_AVG_TIME)).isEqualTo(8);
        assertThat(bundle.getString(OverviewFragment.EXTRA_APP_USAGE_AVG_TIME_MSG)).isEqualTo("짱 적당한 편");
        assertThat(bundle.getStringArrayList(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME_LIST).size()).isEqualTo(3);
        assertThat(bundle.getStringArrayList(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME_LIST).get(0)).isEqualTo("app_name_1");
        assertThat(bundle.getStringArrayList(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME_LIST).get(1)).isEqualTo("app_name_2");
        assertThat(bundle.getStringArrayList(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME_LIST).get(2)).isEqualTo("app_name_3");
        assertThat(bundle.getInt(OverviewFragment.EXTRA_CHARACTER_TYPE)).isEqualTo(AppBeeConstants.CHARACTER_TYPE.QUEEN);
    }


    @Test
    public void onCreate앱시작시_BrainFragment가_나타난다() throws Exception {
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
    public void onCreate_앱시작시_ShareFragment가_나타난다() throws Exception {
        Fragment shareFragment = subject.getFragmentManager().findFragmentByTag(AnalysisResultActivity.SHARE_FRAGMENT_TAG);
        assertThat(shareFragment).isNotNull();
        assertThat(shareFragment.isAdded()).isTrue();

        Button button = (Button)shareFragment.getView().findViewById(R.id.share_button);
        assertThat(button.isShown()).isTrue();
    }

    @Test
    public void getTopUsedAppNameList호출시_지정한_개수만큼의_가장_많이_사용된_앱정보리스트를_리턴한다() throws Exception {
        ArrayList<String> appNameList = subject.getTopUsedAppNameList(3);
        assertThat(appNameList.size()).isEqualTo(3);
    }

    private void mockDummyData() {

        List<NativeAppInfo> nativeAppInfos = new ArrayList<>();
        nativeAppInfos.add(new NativeAppInfo("com.package.name1", "app_name_1"));
        nativeAppInfos.add(new NativeAppInfo("com.package.name2", "app_name_2"));

        List<LongTermStat> longTermStats = new ArrayList<>();
        longTermStats.add(new LongTermStat("com.package.test", "", 999_999_999L));

        List<AppInfo> appInfoList = new ArrayList<>();
        appInfoList.add(new AppInfo("com.package.name1", "app_name_1", null, null, null, null));
        appInfoList.add(new AppInfo("com.package.name2", "app_name_2", null, null, null, null));
        appInfoList.add(new AppInfo("com.package.name3", "app_name_3", null, null, null, null));
        appInfoList.add(new AppInfo("com.package.name4", "app_name_4", null, null, null, null));

        when(appUsageDataHelper.getSortedUsedAppsByTotalUsedTime()).thenReturn(appInfoList);

        when(appUsageDataHelper.getAppCountMessage(4)).thenReturn(R.string.app_count_few_msg);
        when(appUsageDataHelper.getAppCountMessage(200)).thenReturn(R.string.app_count_proper_msg);
        when(appUsageDataHelper.getAppCountMessage(400)).thenReturn(R.string.app_count_many_msg);
        when(appUsageDataHelper.getAppUsageAverageHourPerDay(any())).thenReturn(8);
        when(appUsageDataHelper.getAppUsageAverageMessage(8)).thenReturn(R.string.app_usage_average_time_proper_msg);
        when(appUsageDataHelper.getLongTermStats()).thenReturn(longTermStats);

        when(appUsageDataHelper.getCharacterType()).thenReturn(AppBeeConstants.CHARACTER_TYPE.QUEEN);
        ArrayList<String> mostUsedCategories = new ArrayList<>();
        mostUsedCategories.add("사진");
        mostUsedCategories.add("쇼핑");
        mostUsedCategories.add("음악");
        when(appUsageDataHelper.getMostInstalledCategories(anyInt())).thenReturn(mostUsedCategories);

        ArrayList<String> leastUsedCategories = new ArrayList<>();
        leastUsedCategories.add("고양이");
        when(appUsageDataHelper.getLeastInstalledCategories(anyInt())).thenReturn(leastUsedCategories);

        when(appUsageDataHelper.getAppCountByCategoryId("사진")).thenReturn(1L);
    }
}