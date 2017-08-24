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
    FlowerFragment subject;
    private Unbinder binder;

    @BindView(R.id.most_used_time_categories)
    LinearLayout linearLayout;
    @BindView(R.id.least_used_time_category)
    TextView leastUsedTimeCategoryTextView;
    @BindView(R.id.most_used_time_category_rate)
    TextView mostUsedTimeCategoryRateTextView;

    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostUsedTimeCategoryList = new ArrayList<>();
        mostUsedTimeCategoryList.add("category1");
        mostUsedTimeCategoryList.add("category2");
        mostUsedTimeCategoryList.add("category3");
        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);
        ArrayList<String> leastUsedTimeCategoryList = new ArrayList<>();
        leastUsedTimeCategoryList.add("category4");
        bundle.putStringArrayList(FlowerFragment.EXTRA_LEAST_USED_TIME_CATEGORIES, leastUsedTimeCategoryList);
        bundle.putLong(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORY_RATE, 20L);
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
        assertThat(((TextView)linearLayout.getChildAt(0)).getText()).isEqualTo("category1");
        assertThat(((TextView)linearLayout.getChildAt(1)).getText()).isEqualTo("category2");
        assertThat(((TextView)linearLayout.getChildAt(2)).getText()).isEqualTo("category3");
    }
    @Test
    public void onViewCreate호출시_가장_적은_시간_사용한_카테코리_목록이_나타난다() throws Exception {
        assertThat(leastUsedTimeCategoryTextView.getText()).isEqualTo("category4");
    }
    @Test
    public void onViewCreate호출시_가장_많은_시간_사용한_카테코리의_비율이_나타난다() throws Exception {
        assertThat(mostUsedTimeCategoryRateTextView.getText()).isEqualTo("category1앱이 20%");
    }
}