package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingActivityTest extends ActivityTest {
    private OnboardingActivity subject;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(OnboardingActivity.class);
    }

    @Test
    public void next버튼_클릭시_PermissionGuideActivity로_이동한다() throws Exception {
        subject.findViewById(R.id.next_button).performClick();
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName())
                .isEqualTo(PermissionGuideActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}