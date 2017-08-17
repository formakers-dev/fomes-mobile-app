package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.AnalysisResultActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OverviewFragmentTest {

    private OverviewFragment subject;

    @Before
    public void setUp() throws Exception {
        ActivityController<AnalysisResultActivity> activityController = Robolectric.buildActivity(AnalysisResultActivity.class);
        AnalysisResultActivity activity = activityController.create().start().visible().get();
        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, 10);
        subject = new OverviewFragment();
        subject.setArguments(bundle);
        activity.getFragmentManager().beginTransaction().add(subject, "OVERVIEW_FRAGMENT_TAG").commit();
    }

    @Test
    public void fragment시작시_설치된_앱개수를_표시한다() throws Exception {
        TextView textView = (TextView) subject.getView().findViewById(R.id.text_view_app_list_count);
        assertThat(textView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(textView.getText()).isEqualTo("10");
    }
}