package com.formakers.fomes.activity;


import android.support.v4.app.Fragment;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.AppUsageAnalysisFragment;
import com.formakers.fomes.fragment.OnboardingRewardsFragment;
import com.formakers.fomes.shadow.ShadowAppUsageAnalysisFragment;
import com.formakers.fomes.shadow.ShadowOnboardingRewardFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@Config(shadows = {ShadowAppUsageAnalysisFragment.class, ShadowOnboardingRewardFragment.class})
public class OnboardingAnalysisActivityTest extends BaseActivityTest<OnboardingAnalysisActivity> {

    public OnboardingAnalysisActivityTest() {
        super(OnboardingAnalysisActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        launchActivity();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void onCreated가호출되면_OnboardingAnalysisFragment를_생성한다() throws Exception {
        Fragment appUsageAnalysisFragment = subject.getSupportFragmentManager().findFragmentByTag(AppUsageAnalysisFragment.TAG);
        assertThat(appUsageAnalysisFragment).isNotNull();
        assertThat(appUsageAnalysisFragment.getArguments().getInt(AppUsageAnalysisFragment.EXTRA_DESCRIPTION_RES_ID)).isEqualTo(R.string.analysis_description);
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