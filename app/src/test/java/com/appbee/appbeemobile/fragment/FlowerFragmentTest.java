package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.After;
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
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FlowerFragmentTest {
    private FlowerFragment subject;
    private Unbinder binder;

    @BindView(R.id.most_used_category1)
    TextView mostUsedCategory1View;

    @BindView(R.id.most_used_category2)
    TextView mostUsedCategory2View;

    @BindView(R.id.most_used_category3)
    TextView mostUsedCategory3View;

    @BindView(R.id.least_used_category)
    TextView leastUsedCategoryView;

    @BindView(R.id.most_used_time_category_summary)
    TextView mostUsedTimeCategorySummaryView;

    @BindView(R.id.most_used_time_category_description)
    TextView mostUsedTimeCategoryDescriptionView;

    @BindView(R.id.rank1_layout)
    LinearLayout rank1Layout;

    @BindView(R.id.rank2_layout)
    LinearLayout rank2Layout;

    @BindView(R.id.rank3_layout)
    LinearLayout rank3Layout;

    @BindView(R.id.last_rank_title_view)
    TextView lastRankTitleView;

    @BindView(R.id.flower_background_layout)
    RelativeLayout flowerBackgroundLayout;

    public void setUpWithEnoughData() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostUsedTimeCategoryList = new ArrayList<>();
        mostUsedTimeCategoryList.add("금융/컨설팅");
        mostUsedTimeCategoryList.add("소셜");
        mostUsedTimeCategoryList.add("게임");
        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);
        ArrayList<String> leastUsedTimeCategoryList = new ArrayList<>();
        leastUsedTimeCategoryList.add("데이트");
        bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, leastUsedTimeCategoryList);
        bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY, "금융 앱이 전체 앱 개수의 20%");
        bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, "금융 입니다");
        createFragment(bundle);
    }

    public void setUpWithNotEnoughData() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostUsedTimeCategoryList = new ArrayList<>();
        mostUsedTimeCategoryList.add("게임");
        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);
        ArrayList<String> leastUsedTimeCategoryList = new ArrayList<>();
        leastUsedTimeCategoryList.add("게임");
        bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, leastUsedTimeCategoryList);
        bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY, "아이코, 꽃이 시들어 버렸어요.");
        bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, "가지고 있는 앱의 카테고리 수가 충분치 않거나 분석할만한 충분한 앱 사용정보가 없는 것 같아요. 앱 사용 패턴이 조금 더 쌓이면, 예쁜 꽃이 다시 피어날꺼에요. 그때 앱비랑 다시 만나요!");
        createFragment(bundle);
    }

    private void createFragment(Bundle bundle) {
        subject = new FlowerFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
        binder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }

    @Test
    public void onViewCreate호출시_가장_많은_시간_사용한_카테코리_목록이_나타난다() throws Exception {
        setUpWithEnoughData();

        assertThat(mostUsedCategory1View.getText()).isEqualTo("금융/\n컨설팅");
        assertThat(mostUsedCategory2View.getText()).isEqualTo("소셜");
        assertThat(mostUsedCategory3View.getText()).isEqualTo("게임");
    }

    @Test
    public void onViewCreate호출시_가장_적은_시간_사용한_카테코리_목록이_나타난다() throws Exception {
        setUpWithEnoughData();

        assertThat(leastUsedCategoryView.getText()).contains("데이트 : 저도 있어요");
        assertThat(leastUsedCategoryView.getText()).contains("있지만 사용시간이 낮은 앱");
    }

    @Test
    public void onViewCreate호출시_가장_많은_시간_사용한_카테코리의_정보가_나타난다() throws Exception {
        setUpWithEnoughData();

        assertThat(mostUsedTimeCategorySummaryView.getText()).isEqualTo("금융 앱이 전체 앱 개수의 20%");
        assertThat(mostUsedTimeCategoryDescriptionView.getText()).isEqualTo("금융 입니다");
    }

    @Test
    public void 세건미만의카테고리정보가주어지는경우_onViewCreated호출시_카테고리정보부족_레이아웃이_나타난다() throws Exception {
        setUpWithNotEnoughData();

        assertThat(rank1Layout.getVisibility()).isEqualTo(View.GONE);
        assertThat(rank2Layout.getVisibility()).isEqualTo(View.GONE);
        assertThat(rank3Layout.getVisibility()).isEqualTo(View.GONE);
        assertThat(lastRankTitleView.getVisibility()).isEqualTo(View.GONE);
        assertThat(leastUsedCategoryView.getVisibility()).isEqualTo(View.GONE);

        assertThat(shadowOf(flowerBackgroundLayout.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.dead_flower_background);
        assertThat(mostUsedTimeCategorySummaryView.getText()).isEqualTo("아이코, 꽃이 시들어 버렸어요.");
        assertThat(mostUsedTimeCategoryDescriptionView.getText()).isEqualTo("가지고 있는 앱의 카테고리 수가 충분치 않거나 분석할만한 충분한 앱 사용정보가 없는 것 같아요. 앱 사용 패턴이 조금 더 쌓이면, 예쁜 꽃이 다시 피어날꺼에요. 그때 앱비랑 다시 만나요!");
    }
}