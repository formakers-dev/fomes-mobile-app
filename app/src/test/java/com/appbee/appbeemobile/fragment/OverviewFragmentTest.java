package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OverviewFragmentTest {

    private OverviewFragment subject;

    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, 400);
        bundle.putString(OverviewFragment.EXTRA_APP_LIST_COUNT_MSG, "많기도 하네 진짜...");
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, 8);
        subject = new OverviewFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
    }

    @Test
    public void fragment시작시_설치된_앱개수와_앱개수의평가를_표시한다() throws Exception {
        TextView textView = (TextView) subject.getView().findViewById(R.id.app_count_textview);
        assertThat(textView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(textView.getText()).isEqualTo("400");

        TextView appCountMsgTextView = (TextView) subject.getView().findViewById(R.id.app_count_msg_textview);
        assertThat(appCountMsgTextView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(appCountMsgTextView.getText()).contains("많기도");
    }

    @Test
    public void fragment시작시_평균_앱사용_시간을_표시한다() throws Exception {
        TextView averageAppUsageTimeTextView = (TextView) subject.getView().findViewById(R.id.average_app_usage_time_textview);
        assertThat(averageAppUsageTimeTextView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(averageAppUsageTimeTextView.getText()).contains("8");
    }
}