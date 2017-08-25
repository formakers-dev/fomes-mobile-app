package com.appbee.appbeemobile.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.util.AppBeeConstants;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.annotation.Config;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OverviewFragmentTest {

    private OverviewFragment subject;
    private Unbinder binder;

    @BindView(R.id.app_count_textview)
    TextView appCountView;

    @BindView(R.id.app_count_title_textview)
    TextView appCountTitleView;

    @BindView(R.id.average_app_usage_time_textview)
    TextView averageAppUsageTimeView;

    @BindView(R.id.average_app_usage_time_title_textview)
    TextView averageAppUsageTimeTitleView;

    @BindView(R.id.average_app_usage_time_description_textview)
    TextView averageAppUsageTimeDescriptionView;

    @BindView(R.id.longest_used_app_name)
    TextView longestUsedAppNameView;

    @BindView(R.id.longest_used_app_description)
    TextView longestUsedAppDescriptionView;

    @BindView(R.id.longest_used_app_icon)
    ImageView longestUsedAppIcon;

    @BindView(R.id.character_type_name)
    TextView characterTypeView;

    @BindView(R.id.character_type_simple_description)
    TextView characterTypeSimpleDescriptionView;

    @BindView(R.id.character_type_detail_description)
    TextView characterTypeDetailDescriptionView;

    @BindView(R.id.app_count_description_textview)
    TextView appCountDescriptionView;

    private Bitmap mockBitmap;

    private void createFragment(boolean isLongUsedAppIcon) throws Exception {
        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, 400);
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, 495);
        bundle.putInt(OverviewFragment.EXTRA_CHARACTER_TYPE, AppBeeConstants.CHARACTER_TYPE.GAMER);
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT_TYPE, AppBeeConstants.APP_LIST_COUNT_TYPE.MORE);
        bundle.putInt(OverviewFragment.EXTRA_APP_USAGE_TIME_TYPE, AppBeeConstants.APP_USAGE_TIME_TYPE.MOST);

        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME, "testApp1");
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_DESCRIPTION, "testApp1 Description");
        if (isLongUsedAppIcon) {
            mockBitmap = mock(Bitmap.class);
            bundle.putParcelable(OverviewFragment.EXTRA_LONGEST_USED_APP_ICON_BITMAP, mockBitmap);
        }

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
    public void fragment시작시_설치된_앱개수와_앱개수관련정보를_표시한다() throws Exception {
        createFragment(true);

        assertTextViewVisibleAndEquals(appCountView, "400개");
        assertTextViewVisibleAndEquals(appCountTitleView, "총 앱개수 많아요.");
        assertTextViewVisibleAndEquals(appCountDescriptionView, "호기심이 많고 항상 효율적인 방법을 모색하는 스타일이에요.");
    }

    @Test
    public void fragment시작시_평균_앱사용_시간과_평가를_표시한다() throws Exception {
        createFragment(true);

        assertTextViewVisibleAndEquals(averageAppUsageTimeView, "8시간\n15분");
        assertTextViewVisibleAndEquals(averageAppUsageTimeTitleView, "하루 앱사용시간 엄청 많은편!");
        assertTextViewVisibleAndEquals(averageAppUsageTimeDescriptionView, "가끔은 핸드폰을 덮고 하늘을 바라보는게 어떨까요?");
    }

    @Test
    public void fragment시작시_가장_오래사용한_앱의_정보를_표시한다() throws Exception {
        createFragment(true);

        assertThat(longestUsedAppNameView.getText()).isEqualTo("뗄레야 뗄 수 없는 앱, testApp1");
        assertThat(longestUsedAppDescriptionView.getText()).isEqualTo("testApp1 Description");
        assertThat(((BitmapDrawable) longestUsedAppIcon.getDrawable()).getBitmap()).isEqualTo(mockBitmap);
    }

    @Test
    public void 가장_오래사용한_앱의_아이콘이_없는_경우_표시하지않는다() throws Exception {
        createFragment(false);
        assertThat(longestUsedAppIcon.getDrawable()).isNull();
    }

    @Test
    public void fragment시작시_캐릭터타입을_표시한다() throws Exception {
        createFragment(true);

        assertThat(characterTypeView.getText()).isEqualTo("덕후가 아니라능! 게이머벌");
        assertThat(characterTypeSimpleDescriptionView.getText()).isEqualTo("안녕하신가! 힘세고 강한 아침,\\n만일 내게 물어보면 나는…!!!");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("모바일 게임");
    }

    private void assertTextViewVisibleAndEquals(TextView textView, String text) {
        assertThat(textView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(textView.getText()).isEqualTo(text);
    }
}