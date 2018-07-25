package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.AppUsageAnalysisFragment;
import com.appbee.appbeemobile.shadow.ShadowAppUsageAnalysisFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;


@Config(constants = BuildConfig.class, shadows = {ShadowAppUsageAnalysisFragment.class})
public class MyAppUsageActivityTest extends BaseActivityTest<MyAppUsageActivity> {

    MyAppUsageActivity subject;

    public MyAppUsageActivityTest() {
        super(MyAppUsageActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        subject = getActivity();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void onCreate호출시_AppUsageAnalysisFragment가_생성된다() throws Exception {
        AppUsageAnalysisFragment onboardingAnalysisFragment = (AppUsageAnalysisFragment)subject.getSupportFragmentManager().findFragmentByTag(AppUsageAnalysisFragment.TAG);
        assertThat(onboardingAnalysisFragment).isNotNull();
        assertThat(onboardingAnalysisFragment.getArguments().getInt(AppUsageAnalysisFragment.EXTRA_DESCRIPTION_RES_ID)).isEqualTo(R.string.my_analysis_description);
    }
}