package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.fragment.AppUsageAnalysisFragment;
import com.appbee.appbeemobile.shadow.ShadowAppUsageAnalysisFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowAppUsageAnalysisFragment.class})
public class MyAppUsageActivityTest {

    MyAppUsageActivity subject;
    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(MyAppUsageActivity.class);
    }

    @Test
    public void onCreate호출시_OnboardingAnaysisFragment가_생성된다() throws Exception {
        assertThat(subject.getSupportFragmentManager().findFragmentByTag(AppUsageAnalysisFragment.TAG)).isNotNull();
    }
}