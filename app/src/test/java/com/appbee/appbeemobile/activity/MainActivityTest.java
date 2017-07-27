package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.manager.AppStatServiceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private ActivityController<MainActivity> activityController;

    @Inject
    AppStatServiceManager appStatServiceManager;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
         activityController = Robolectric.buildActivity(MainActivity.class);
    }

    @Test
    public void onCreate앱시작시_앱목록데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatServiceManager).sendAppList();
    }

    @Test
    public void onCreate앱시작시_연간일별통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatServiceManager).sendLongTermStats();
    }

    @Test
    public void onCreate앱시작시_단기통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatServiceManager).sendEventStats();
    }

    @Test
    public void onCreate앱시작시_가공된_단기통계데이터를_전송요청한다() throws Exception {
        activityController.create();

        verify(appStatServiceManager).sendShortTermStats();
    }
}