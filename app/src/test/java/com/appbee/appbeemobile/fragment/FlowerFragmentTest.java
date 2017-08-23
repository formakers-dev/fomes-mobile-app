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

    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostUsedTimeCategoryList = new ArrayList<>();
        mostUsedTimeCategoryList.add("category1");
        mostUsedTimeCategoryList.add("category2");
        mostUsedTimeCategoryList.add("category3");
        bundle.putStringArrayList(FlowerFragment.EXTRA_MOST_USED_TIME_CATEGORIES, mostUsedTimeCategoryList);
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
}