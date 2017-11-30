package com.appbee.appbeemobile.activity;


import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.OnboardingAnalysisFragment;
import com.appbee.appbeemobile.fragment.OnboardingRewardsFragment;
import com.appbee.appbeemobile.shadow.ShadowOnboardingAnalysisFragment;
import com.appbee.appbeemobile.shadow.ShadowOnboardingRewardFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowOnboardingAnalysisFragment.class, ShadowOnboardingRewardFragment.class})
public class OnboardingAnalysisActivityTest {

    private OnboardingAnalysisActivity subject;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(OnboardingAnalysisActivity.class);
    }

    @Test
    public void onCreated가호출되면_OnboardingAnalysisFragment를_생성한다() throws Exception {
        assertThat(subject.getSupportFragmentManager().findFragmentByTag(OnboardingAnalysisFragment.TAG)).isNotNull();
    }

    @Test
    public void OnboardingAnalysisFragment가_시작된상태에서_nextButton클릭시_OnboardingReward로_이동한다() throws Exception {
        subject.findViewById(R.id.next_button).performClick();
        assertThat(subject.getSupportFragmentManager().findFragmentByTag(OnboardingRewardsFragment.TAG)).isNotNull();
    }

    @Test
    public void OnboardingRewardFragment가_시작된상태에서_nextButton클릭시_MainActivity로_이동한다() throws Exception {
        subject.findViewById(R.id.next_button).performClick();
        subject.findViewById(R.id.next_button).performClick();
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(MainActivity.class.getCanonicalName());
    }
}