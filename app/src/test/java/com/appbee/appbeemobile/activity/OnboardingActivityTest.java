package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingActivityTest {

    private OnboardingActivity subject;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(OnboardingActivity.class);
    }

    @Test
    public void OnboardingActivity가_onCreate시_3페이지를갖는onboardingViewPager를표시한다() throws Exception {
        assertThat(subject.onboardingViewPager.isShown()).isTrue();
        assertThat(subject.onboardingViewPager.getAdapter().getCount()).isEqualTo(3);
    }
}