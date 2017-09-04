package com.appbee.appbeemobile.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.annotation.Config;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.appbee.appbeemobile.util.AppBeeConstants.APP_LIST_COUNT_TYPE;
import static com.appbee.appbeemobile.util.AppBeeConstants.APP_USAGE_TIME_TYPE;
import static com.appbee.appbeemobile.util.AppBeeConstants.CharacterType;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;


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

    @BindView(R.id.character_type_icon)
    ImageView characterTypeIconView;

    @BindView(R.id.character_type_name)
    TextView characterTypeView;

    @BindView(R.id.character_type_simple_description)
    TextView characterTypeSimpleDescriptionView;

    @BindView(R.id.character_type_detail_description)
    TextView characterTypeDetailDescriptionView;

    @BindView(R.id.app_count_description_textview)
    TextView appCountDescriptionView;

    @BindView(R.id.honey_pot_image)
    ImageView honeyPotImageView;

    @BindView(R.id.hive_image)
    ImageView hiveImageView;

    private Bitmap mockBitmap;

    private Bundle getInitBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, 400);
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, 495);
        bundle.putSerializable(OverviewFragment.EXTRA_CHARACTER_TYPE, CharacterType.GAMER);
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT_TYPE, APP_LIST_COUNT_TYPE.MORE);
        bundle.putInt(OverviewFragment.EXTRA_APP_USAGE_TIME_TYPE, APP_USAGE_TIME_TYPE.MOST);
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_NAME, "testApp1");
        bundle.putString(OverviewFragment.EXTRA_LONGEST_USED_APP_DESCRIPTION, "testApp1 Description");
        return bundle;
    }

    private void createFragment(boolean isLongUsedAppIcon, CharacterType characterType) throws Exception {
        Bundle bundle = getInitBundle();
        bundle.putSerializable(OverviewFragment.EXTRA_CHARACTER_TYPE, characterType);
        if (isLongUsedAppIcon) {
            mockBitmap = mock(Bitmap.class);
            bundle.putParcelable(OverviewFragment.EXTRA_LONGEST_USED_APP_ICON_BITMAP, mockBitmap);
        }

        subject = new OverviewFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
        binder = ButterKnife.bind(this, subject.getView());
    }

    private void createFragmentForHoneyPot(int appCount) throws Exception {
        Bundle bundle = getInitBundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_LIST_COUNT, appCount);
        subject = new OverviewFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
        binder = ButterKnife.bind(this, subject.getView());
    }

    private void createFragmentForHive(int appAvgTime) throws Exception {
        Bundle bundle = getInitBundle();
        bundle.putInt(OverviewFragment.EXTRA_APP_AVG_TIME, appAvgTime);
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
    public void 게이머타입유저로서_fragment시작시_적절한_캐릭터타입_정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.GAMER);

        assertThat(shadowOf(characterTypeIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.character_gamer);
        assertThat(characterTypeView.getText()).isEqualTo("덕후가 아니라능! 게이머벌");
        assertThat(characterTypeSimpleDescriptionView.getText()).isEqualTo("안녕하신가! 힘세고 강한 아침, 만일 내게 물어보면 나는…!!!");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("모바일 게임");
    }

    @Test
    public void 여왕벌타입유저로서_fragment시작시_적절한_캐릭터타입_정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.QUEEN);

        assertThat(shadowOf(characterTypeIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.character_queen);
        assertThat(characterTypeView.getText()).isEqualTo("하태하태! 내 스타일 여왕벌");
        assertThat(characterTypeSimpleDescriptionView.getText()).isEqualTo("한번뿐인 인생, 모든 순간을 소중하게!");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("스쳐지나가는 순간도 소중히 여기는 당신!");
    }

    @Test
    public void 맹독벌타입유저로서_fragment시작시_적절한_캐릭터타입_정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.POISON);

        assertThat(shadowOf(characterTypeIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.character_poison);
        assertThat(characterTypeView.getText()).isEqualTo("감성폭발! 치명치명 맹독벌");
        assertThat(characterTypeSimpleDescriptionView.getText()).isEqualTo("비가 가장 많이오는곳은 어디?\n바로..내 마음이에요");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("룰에 얽매이지 않는 감성이 정말 멋지답니다!");
    }

    @Test
    public void 룰루땡벌타입유저로서_fragment시작시_적절한_캐릭터타입_정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.SOUL);

        assertThat(shadowOf(characterTypeIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.character_soul);
        assertThat(characterTypeView.getText()).isEqualTo("나만의 소울충만! 룰루 땡벌");
        assertThat(characterTypeSimpleDescriptionView.getText()).isEqualTo("나의 밤은 당신의 낮보다 아름답다");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("자기만의 쀨로 가득 찬 특이한 당신!");
    }

    @Test
    public void 기타타입유저로서_fragment시작시_적절한_캐릭터타입_정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.ETC);

        assertThat(shadowOf(characterTypeIconView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.character_alien);
        assertThat(characterTypeView.getText()).isEqualTo("꿀빨러 왔나? 불시착 외계인!");
        assertThat(characterTypeSimpleDescriptionView.getText()).isEqualTo("꿀따리 샤바라~ 바니바니 꿍꿍꿀!");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("혹시 평소에 독특하다는 소리 좀 들으시나요?");
    }

    @Test
    public void 게이머타입유저로서_fragment시작시_게이머_캐릭터타입_정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.GAMER);

        assertThat(characterTypeView.getText()).isEqualTo("덕후가 아니라능! 게이머벌");
        assertThat(characterTypeSimpleDescriptionView.getText()).isEqualTo("안녕하신가! 힘세고 강한 아침, 만일 내게 물어보면 나는…!!!");
        assertThat(characterTypeDetailDescriptionView.getText()).contains("모바일 게임");
    }

    @Test
    public void fragment시작시_설치된_앱개수와_앱개수관련정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.GAMER);

        assertTextViewVisibleAndEquals(appCountView, "400개");
        assertTextViewVisibleAndEquals(appCountTitleView, "총 앱개수 많아요.");
        assertTextViewVisibleAndEquals(appCountDescriptionView, "호기심이 많고 항상 효율적인 방법을 모색하는 스타일이에요.");

    }

    @Test
    public void fragment시작시_설치된_앱개수가_150개_이상일_경우_FullHoneyPot_이미지를_보여준다() throws Exception {
        createFragmentForHoneyPot(150);

        assertThat(shadowOf(honeyPotImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.full_honey_pot);
    }

    @Test
    public void fragment시작시_설치된_앱개수가_100개_이상일_경우_ThreeQuaterHoneyPot_이미지를_보여준다() throws Exception {
        createFragmentForHoneyPot(100);

        assertThat(shadowOf(honeyPotImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.three_quater_honey_pot);
    }

    @Test
    public void fragment시작시_설치된_앱개수가_50개_이상일_경우_HalfHoneyPot_이미지를_보여준다() throws Exception {
        createFragmentForHoneyPot(50);

        assertThat(shadowOf(honeyPotImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.half_honey_pot);
    }

    @Test
    public void fragment시작시_설치된_앱개수가_25개_이상일_경우_QuaterHoneyPot_이미지를_보여준다() throws Exception {
        createFragmentForHoneyPot(25);

        assertThat(shadowOf(honeyPotImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.quater_honey_pot);
    }

    @Test
    public void fragment시작시_설치된_앱개수가_25개_미만일_경우_PoorHoneyPot_이미지를_보여준다() throws Exception {
        createFragmentForHoneyPot(24);

        assertThat(shadowOf(honeyPotImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.poor_honey_pot);
    }

    @Test
    public void fragment시작시_평균_앱사용_시간과_평가를_표시한다() throws Exception {
        createFragment(true, CharacterType.GAMER);

        assertTextViewVisibleAndEquals(averageAppUsageTimeView, "8시간\n15분");
        assertTextViewVisibleAndEquals(averageAppUsageTimeTitleView, "하루 앱사용시간 엄청 많은편!");
        assertTextViewVisibleAndEquals(averageAppUsageTimeDescriptionView, "가끔은 핸드폰을 덮고 하늘을 바라보는게 어떨까요?");
    }

    @Test
    public void fragment시작시_가장_오래사용한_앱의_정보를_표시한다() throws Exception {
        createFragment(true, CharacterType.GAMER);

        assertThat(longestUsedAppNameView.getText()).isEqualTo("제일 많이 쓴 앱, testApp1");
        assertThat(longestUsedAppDescriptionView.getText()).isEqualTo("testApp1 Description");
        assertThat(((BitmapDrawable) longestUsedAppIcon.getDrawable()).getBitmap()).isEqualTo(mockBitmap);
    }


    @Test
    public void fragment시작시_가장_오래사용한_앱의_사용시간이_8시간_이상인_경우_fullHive이미지를_표시한다() throws Exception {
        createFragmentForHive(480);

        assertThat(shadowOf(hiveImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.full_hive);
    }

    @Test
    public void fragment시작시_가장_오래사용한_앱의_사용시간이_4시간_이상인_경우_threeQuaterHive이미지를_표시한다() throws Exception {
        createFragmentForHive(240);

        assertThat(shadowOf(hiveImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.three_quater_hive);
    }

    @Test
    public void fragment시작시_가장_오래사용한_앱의_사용시간이_2시간_이상인_경우_halfHive이미지를_표시한다() throws Exception {
        createFragmentForHive(120);

        assertThat(shadowOf(hiveImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.half_hive);
    }

    @Test
    public void fragment시작시_가장_오래사용한_앱의_사용시간이_1시간_이상인_경우_quaterHive이미지를_표시한다() throws Exception {
        createFragmentForHive(60);

        assertThat(shadowOf(hiveImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.quater_hive);
    }

    @Test
    public void fragment시작시_가장_오래사용한_앱의_사용시간이_1시간_미만인_경우_poorHive이미지를_표시한다() throws Exception {
        createFragmentForHive(59);

        assertThat(shadowOf(hiveImageView.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.poor_hive);
    }

    @Test
    public void 가장_오래사용한_앱의_아이콘이_없는_경우_디폴트아이콘을_표시한다() throws Exception {
        createFragment(false, CharacterType.GAMER);
        assertThat(shadowOf(longestUsedAppIcon.getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.notfound_app_icon);
    }

    private void assertTextViewVisibleAndEquals(TextView textView, String text) {
        assertThat(textView.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(textView.getText()).isEqualTo(text);
    }
}