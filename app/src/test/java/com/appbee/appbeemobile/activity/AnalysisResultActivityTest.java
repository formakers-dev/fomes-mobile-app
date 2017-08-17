package com.appbee.appbeemobile.activity;


import com.appbee.appbeemobile.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AnalysisResultActivityTest extends ActivityTest {
    private AnalysisResultActivity subject;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(AnalysisResultActivity.class);
    }

    @Test
    public void onCreate앱시작시_OverViewFragment가_나타난다() throws Exception {
        assertThat(subject.getFragmentManager().findFragmentByTag(subject.OVERVIEW_FRAGMENT_TAG).isAdded()).isTrue();
    }
}