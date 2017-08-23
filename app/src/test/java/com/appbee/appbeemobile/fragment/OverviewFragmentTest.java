package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.util.AppBeeConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OverviewFragmentTest {

    private OverviewFragment subject;
    private Unbinder binder;

    @BindView(R.id.app_count_textview)
    TextView appCountView;

    @BindView(R.id.app_count_msg_textview)
    TextView appCountMsgView;

    @BindView(R.id.average_app_usage_time_textview)
    TextView averageAppUsageTimeView;

    @BindView(R.id.average_app_usage_time_msg_textview)
    TextView averageAppUsageTimeMsgView;

    @BindView(R.id.longest_used_app_layout)
    LinearLayout longestUsedAppLayout;

    @BindView(R.id.character_type_name)
    TextView characterTypeView;

    @BindView(R.id.character_type_simple_description)
    TextView characterTypeSimpleDescriptionView;

    @BindView(R.id.character_type_detail_description)
    TextView characterTypeDetailDescriptionView;

    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, 400);
        bundle.putString(OverviewFragment.EXTRA_APP_LIST_COUNT_MSG, "많기도 하네 진짜...");
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, 8);
        bundle.putString(OverviewFragment.EXTRA_APP_USAGE_AVG_TIME_MSG, "짱 많은 편");
        bundle.putInt(OverviewFragment.EXTRA_CHARACTER_TYPE, AppBeeConstants.CHARACTER_TYPE.GAMER);

        ArrayList<String> appNameList = new ArrayList<>();
        appNameList.add("testApp1");
        appNameList.add("testApp2");
        appNameList.add("testApp3");
        bundle.putStringArrayList(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME_LIST, appNameList);
        subject = new OverviewFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
        binder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }

    @Test
    public void fragment시작시_설치된_앱개수와_앱개수의평가를_표시한다() throws Exception {
        assertTextViewVisibleAndContains(appCountView, "400");
        assertTextViewVisibleAndContains(appCountMsgView, "많기도");
    }

    @Test
    public void fragment시작시_평균_앱사용_시간과_평가를_표시한다() throws Exception {
        assertTextViewVisibleAndContains(averageAppUsageTimeView, "8");
        assertTextViewVisibleAndEquals(averageAppUsageTimeMsgView, "짱 많은 편");
    }

    @Test
    public void fragment시작시_가장_오래사용한_앱의_정보를_표시한다() throws Exception {
        assertThat(longestUsedAppLayout.getChildCount()).isEqualTo(3);
        assertThat(((TextView) longestUsedAppLayout.getChildAt(0)).getText()).isEqualTo("testApp1");
        assertThat(((TextView) longestUsedAppLayout.getChildAt(1)).getText()).isEqualTo("testApp2");
        assertThat(((TextView) longestUsedAppLayout.getChildAt(2)).getText()).isEqualTo("testApp3");
    }

    @Test
    public void fragment시작시_캐릭터타입을_표시한다() throws Exception {
        assertThat(characterTypeView.getText()).isEqualTo("덕후가 아니라 게이머다! 일코벌");
        assertThat(characterTypeSimpleDescriptionView.getText()).contains("안녕하신가! 힘세고 강한 아침, 만일 내게 물어보면 나는");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("모바일 게임");
    }

    private void assertTextViewVisibleAndEquals(TextView textView, String text) {
        assertThat(textView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(textView.getText()).isEqualTo(text);
    }

    private void assertTextViewVisibleAndContains(TextView textView, String text) {
        assertThat(textView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(textView.getText()).contains(text);
    }
}