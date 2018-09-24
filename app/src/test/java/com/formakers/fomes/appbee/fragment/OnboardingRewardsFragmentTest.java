package com.formakers.fomes.appbee.fragment;

import android.widget.TextView;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@Ignore
public class OnboardingRewardsFragmentTest {

    private OnboardingRewardsFragment subject;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new OnboardingRewardsFragment();

        SupportFragmentTestUtil.startFragment(subject);
        unbinder = ButterKnife.bind(this, subject.getView());
    }

    @Test
    public void OnboardingRewardsFragment생성시_리워드소개화면이보인다() throws Exception {
        assertThat(((TextView) subject.getView().findViewById(R.id.reward_title)).getText()).isEqualTo("신상앱 유저인터뷰 체험하고\n리워드를 받아가세요.");
    }

    @After
    public void tearDown() throws Exception {
        unbinder.unbind();
    }
}