package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

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
public class FlowerFragmentTest {
    private FlowerFragment subject;
    private Unbinder binder;

    @BindView(R.id.most_used_time_categories)
    LinearLayout linearLayout;
    @BindView(R.id.least_used_time_category)
    TextView leastUsedTimeCategoryTextView;
    @BindView(R.id.most_used_time_category_summary)
    TextView mostUsedTimeCategorySummaryView;
    @BindView(R.id.most_used_time_category_description)
    TextView mostUsedTimeCategoryDescriptionView;

    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostUsedTimeCategoryList = new ArrayList<>();
        mostUsedTimeCategoryList.add("금융");
        mostUsedTimeCategoryList.add("소셜");
        mostUsedTimeCategoryList.add("게임");
        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);
        ArrayList<String> leastUsedTimeCategoryList = new ArrayList<>();
        leastUsedTimeCategoryList.add("데이트");
        bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, leastUsedTimeCategoryList);
        bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_SUMMARY, "금융 앱이 전체 앱 개수의 20%");
        bundle.putString(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_DESC, "금융 입니다");
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
        assertThat(((TextView) linearLayout.getChildAt(0)).getText()).isEqualTo("금융");
        assertThat(((TextView) linearLayout.getChildAt(1)).getText()).isEqualTo("소셜");
        assertThat(((TextView) linearLayout.getChildAt(2)).getText()).isEqualTo("게임");
    }

    @Test
    public void onViewCreate호출시_가장_적은_시간_사용한_카테코리_목록이_나타난다() throws Exception {
        assertThat(leastUsedTimeCategoryTextView.getText()).isEqualTo("데이트");
    }

    @Test
    public void onViewCreate호출시_가장_많은_시간_사용한_카테코리의_정보가_나타난다() throws Exception {
        assertThat(mostUsedTimeCategorySummaryView.getText()).isEqualTo("금융 앱이 전체 앱 개수의 20%");
        assertThat(mostUsedTimeCategoryDescriptionView.getText()).isEqualTo("금융 입니다");
    }
}