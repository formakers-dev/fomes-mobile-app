package com.appbee.appbeemobile.fragment;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.IFragmentManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingAnalysisFragmentTest {

    @Mock
    private IFragmentManager mockFragmentManager;

    private OnboardingAnalysisFragment subject;

    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new OnboardingAnalysisFragment();
        subject.setFragmentManager(mockFragmentManager);

        SupportFragmentTestUtil.startFragment(subject);
        unbinder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        unbinder.unbind();
    }

    @Test
    public void nextButton클릭시_OnboardingRewardsFragment로_이동하도록하는_메소드를_호출한다() throws Exception {
        subject.getView().findViewById(R.id.next_button).performClick();
        verify(mockFragmentManager).replaceFragment(anyString());
    }

    // TODO : replaceFragment, startMainActivityAndFinish 메서드 테스트 필요
}