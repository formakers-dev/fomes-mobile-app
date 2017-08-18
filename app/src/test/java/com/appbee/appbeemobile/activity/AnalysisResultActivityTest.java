package com.appbee.appbeemobile.activity;


import android.app.Fragment;
import android.os.Bundle;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.fragment.OverviewFragment;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.LongTermStat;
import com.appbee.appbeemobile.model.NativeAppInfo;

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
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(resourceDir = "src/main/res", constants = BuildConfig.class)
public class AnalysisResultActivityTest extends ActivityTest {
    private AnalysisResultActivity subject;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);

        List<NativeAppInfo> nativeAppInfos = new ArrayList<>();
        nativeAppInfos.add(new NativeAppInfo("com.package.name1", "app_name_1"));
        nativeAppInfos.add(new NativeAppInfo("com.package.name2", "app_name_2"));

        List<LongTermStat> longTermStats = new ArrayList<>();
        longTermStats.add(new LongTermStat("com.package.test", "", 999_999_999L));

        when(appUsageDataHelper.getAppList()).thenReturn(nativeAppInfos);
        when(appUsageDataHelper.getAppCountMessage(2)).thenReturn(R.string.app_count_few_msg);
        when(appUsageDataHelper.getAppCountMessage(200)).thenReturn(R.string.app_count_proper_msg);
        when(appUsageDataHelper.getAppCountMessage(400)).thenReturn(R.string.app_count_many_msg);
        when(appUsageDataHelper.getAppUsageAverageHourPerDay(any())).thenReturn(8);
        when(appUsageDataHelper.getAppUsageAverageMessage(8)).thenReturn(R.string.app_usage_average_time_proper_msg);
        when(appUsageDataHelper.getLongTermStats()).thenReturn(longTermStats);
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);
    }

    @Test
    public void onCreate앱시작시_OverViewFragment가_나타난다() throws Exception {
        Fragment fragment = subject.getFragmentManager().findFragmentByTag(subject.OVERVIEW_FRAGMENT_TAG);
        Bundle bundle = fragment.getArguments();
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_LIST_COUNT)).isEqualTo(2);
        assertThat(bundle.getString(OverviewFragment.EXTRA_APP_LIST_COUNT_MSG)).isEqualTo("적기도 하네 진짜...");
        assertThat(bundle.getInt(OverviewFragment.EXTRA_APP_AVG_TIME)).isEqualTo(8);
        assertThat(bundle.getString(OverviewFragment.EXTRA_APP_USAGE_AVG_TIME_MSG)).isEqualTo("짱 적당한 편");
        assertThat(bundle.getString(OverviewFragment.EXTRA_LONGEST_USED_APP_PACKAGE_NAME)).isEqualTo("com.package.test");
        assertThat(bundle.getLong(OverviewFragment.EXTRA_LONGEST_USED_APP_TIME)).isEqualTo(999_999_999L);
        assertThat(fragment.isAdded()).isTrue();
    }
}