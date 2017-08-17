package com.appbee.appbeemobile.activity;


import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppInfo;

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

        List<AppInfo> appInfoList = new ArrayList<>();
        appInfoList.add(new AppInfo("com.package.name1", "app_name_1"));
        appInfoList.add(new AppInfo("com.package.name2", "app_name_2"));

        when(appUsageDataHelper.getAppList()).thenReturn(appInfoList);
        when(appUsageDataHelper.getAppCountMessage(2)).thenReturn(R.string.app_count_few_msg);
        when(appUsageDataHelper.getAppCountMessage(200)).thenReturn(R.string.app_count_proper_msg);
        when(appUsageDataHelper.getAppCountMessage(400)).thenReturn(R.string.app_count_many_msg);

        subject = Robolectric.setupActivity(AnalysisResultActivity.class);
    }

    @Test
    public void onCreate앱시작시_OverViewFragment가_나타난다() throws Exception {
        assertThat(subject.getFragmentManager().findFragmentByTag(subject.OVERVIEW_FRAGMENT_TAG).isAdded()).isTrue();
    }
}