package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.manager.StatManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {
    private MainActivity subject;

    @Inject
    StatManager statManager;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
        subject = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void 앱시작시_일별통계데이터를_조회한다() throws Exception {
        verify(statManager).getUserAppUsageInDetail();
    }
}