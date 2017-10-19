package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.LocalStorageHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private ActivityController<MainActivity> activityController;
    private MainActivity subject;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(MainActivity.class);
    }

    @Test
    public void LocalStorage에저장된이메일이없을경우_온보딩화면으로_이동하고_종료한다() throws Exception {
        when(mockLocalStorageHelper.getEmail()).thenReturn("");

        subject = activityController.create().get();

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(OnboardingActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}