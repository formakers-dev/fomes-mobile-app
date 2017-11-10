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

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingRewardsFragmentTest {

    @Mock
    private IFragmentManager mockFragmentManager;

    private OnboardingRewardsFragment subject;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new OnboardingRewardsFragment();
        subject.setFragmentManager(mockFragmentManager);

        SupportFragmentTestUtil.startFragment(subject);
        unbinder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        unbinder.unbind();
    }

    @Test
    public void nextButton클릭시_MainActivity로_이동하도록하는_메소드를호출한다() throws Exception {
        subject.getView().findViewById(R.id.next_button).performClick();
        verify(mockFragmentManager).startMainActivityAndFinish();
    }
}