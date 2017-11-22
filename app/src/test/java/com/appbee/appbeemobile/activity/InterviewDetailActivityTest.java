package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@Ignore
public class InterviewDetailActivityTest {
    InterviewDetailActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        Intent intent = new Intent();
        intent.putExtra("EXTRA_PROJECT_SEQ", "interviewSeq");
        subject = Robolectric.buildActivity(InterviewDetailActivity.class, intent).create().postCreate(null).get();
    }

}