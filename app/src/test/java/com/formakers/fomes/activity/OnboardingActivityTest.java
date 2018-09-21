package com.formakers.fomes.activity;

import com.formakers.fomes.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@Ignore
public class OnboardingActivityTest extends BaseActivityTest<OnboardingActivity> {
    private OnboardingActivity subject;

    public OnboardingActivityTest() {
        super(OnboardingActivity.class);
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
    public void next버튼_클릭시_PermissionGuideActivity로_이동한다() throws Exception {
        subject.findViewById(R.id.next_button).performClick();
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName())
                .isEqualTo(PermissionGuideActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}