package com.appbee.appbeemobile.activity;

import android.app.Activity;

import com.appbee.appbeemobile.BuildConfig;

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

    @Test
    public void OnboardingActivity가_onCreate시_구글ID로시작하기버튼이보인다() throws Exception {
        assertThat(subject.loginButton.isShown()).isTrue();
        assertThat(subject.loginButton.getText()).isEqualTo("구글ID로 시작하기");
    }

    @Test
    public void 구글ID로시작하기버튼을_클릭하면_LoginActivity로이동한다() throws Exception {
        subject.loginButton.performClick();

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(LoginActivity.class.getName());
    }

    @Test
    public void OnActivityResult_resultCode가_OK이면_종료한다() throws Exception {
        subject.onActivityResult(9002, Activity.RESULT_OK, null);

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}