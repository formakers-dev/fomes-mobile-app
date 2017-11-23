package com.appbee.appbeemobile.fragment;

import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.activity.IFragmentManager;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppUsage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingAnalysisFragmentTest {

    @Mock
    private IFragmentManager mockFragmentManager;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    private OnboardingAnalysisFragment subject;
    private SupportFragmentController<OnboardingAnalysisFragment> controller;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        MockitoAnnotations.initMocks(this);
        subject = new OnboardingAnalysisFragment();
        subject.setFragmentManager(mockFragmentManager);

        controller = SupportFragmentController.of(subject);
        unbinder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        unbinder.unbind();
    }

    @Test
    public void onViewCreated시_가장많이사용한앱3개가_나타난다() throws Exception {
        List<AppUsage> mockSortedList = new ArrayList<>();
        mockSortedList.add(new AppUsage("com.kakao.talk", 3000));
        mockSortedList.add(new AppUsage("com.test.app", 2000));
        mockSortedList.add(new AppUsage("com.test.com", 1000));
        mockSortedList.add(new AppUsage("not.add.app", 500));
        when(mockAppUsageDataHelper.getSortedUsedApp()).thenReturn(mockSortedList);

        controller.create().start().resume();

        assertThat(subject.mostUsedAppViewGroup.getChildCount()).isEqualTo(3);

        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_name_textview)).getText()).isEqualTo("com.kakao.talk");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_name_textview)).getText()).isEqualTo("com.test.app");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(2).findViewById(R.id.app_name_textview)).getText()).isEqualTo("com.test.com");
    }

    @Test
    public void onViewCreated시_앱개수가_3개이하인_경우_가장많이사용한앱이_앱개수만큼_나타난다() throws Exception {
        List<AppUsage> mockSortedList = new ArrayList<>();
        mockSortedList.add(new AppUsage("com.kakao.talk", 3000));
        mockSortedList.add(new AppUsage("com.test.app", 2000));
        when(mockAppUsageDataHelper.getSortedUsedApp()).thenReturn(mockSortedList);

        controller.create().start().resume();

        assertThat(subject.mostUsedAppViewGroup.getChildCount()).isEqualTo(2);

        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_name_textview)).getText()).isEqualTo("com.kakao.talk");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_name_textview)).getText()).isEqualTo("com.test.app");
    }

    @Test
    public void nextButton클릭시_OnboardingRewardsFragment로_이동하도록하는_메소드를_호출한다() throws Exception {
        controller.create().start().resume();
        subject.getView().findViewById(R.id.next_button).performClick();
        verify(mockFragmentManager).replaceFragment(anyString());
    }
}