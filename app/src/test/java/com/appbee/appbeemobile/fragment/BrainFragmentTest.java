package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
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
public class BrainFragmentTest {

    private BrainFragment subject;
    private Unbinder binder;

    @BindView(R.id.most_installed_categories)
    TextView mostInstalledCategoriesView;

    @BindView(R.id.least_installed_categories)
    TextView leastInstalledCategoriesView;

    @BindView(R.id.most_installed_category_summary)
    TextView mostInstalledCategorySummaryView;

    @BindView(R.id.most_installed_category_description)
    TextView mostInstalledCategoryDescriptionView;

    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostInstalledCategoryList = new ArrayList<>();
        mostInstalledCategoryList.add("금융");
        mostInstalledCategoryList.add("게임");
        mostInstalledCategoryList.add("만화");
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, mostInstalledCategoryList);

        ArrayList<String> leastInstalledCategoryList = new ArrayList<>();
        leastInstalledCategoryList.add("건강");
        bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, leastInstalledCategoryList);

        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY, "금융 앱이 전체 앱 개수의 25%");
        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, "금융앱 설명입니다");

        subject = new BrainFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
        binder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }

    @Test
    public void onViewCreated호출시_가장많이설치한카테고리목록이_나타난다() throws Exception {
        assertThat(mostInstalledCategoriesView.getText()).isEqualTo("[금융, 게임, 만화]");
    }

    @Test
    public void onViewCreated호출시_가장적게설치한카테고리목록이_나타난다() throws Exception {
        assertThat(leastInstalledCategoriesView.getText()).isEqualTo("[건강]");
    }

    @Test
    public void onViewCreated호출시_가장_많이_설치된_카테고리에_대한_정보를_표시한다() throws Exception {
        assertThat(String.valueOf(mostInstalledCategorySummaryView.getText())).isEqualTo("금융 앱이 전체 앱 개수의 25%");
        assertThat(mostInstalledCategoryDescriptionView.getText()).isEqualTo("금융앱 설명입니다");
    }
}
