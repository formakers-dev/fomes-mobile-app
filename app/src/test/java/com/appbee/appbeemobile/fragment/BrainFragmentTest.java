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
    TextView mostInstalledCategories;

    @BindView(R.id.least_installed_categories)
    TextView leastInstalledCategories;

    @BindView(R.id.installed_app_count)
    TextView installedAppCount;

    @BindView(R.id.most_installed_category_rate)
    TextView mostInstalledCategoryRate;

    @BindView(R.id.most_installed_category_description)
    TextView mostInstalledCategoryDescriptioinView;

    @Before
    public void setUp() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostInstalledCategoryList = new ArrayList<>();
        mostInstalledCategoryList.add("categoryId1");
        mostInstalledCategoryList.add("categoryId2");
        mostInstalledCategoryList.add("categoryId3");
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, mostInstalledCategoryList);

        ArrayList<String> leastInstalledCategoryList = new ArrayList<>();
        leastInstalledCategoryList.add("leastInstalledCategoryId");
        bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, leastInstalledCategoryList);

        bundle.putInt(BrainFragment.EXTRA_INSTALLED_APP_COUNT, 400);
        bundle.putLong(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_RATE, 25L);
        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, "categoryId1의 설명입니다");

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
        assertThat(mostInstalledCategories.getText()).isEqualTo("[categoryId1, categoryId2, categoryId3]");
    }

    @Test
    public void onViewCreated호출시_가장적게설치한카테고리목록이_나타난다() throws Exception {
        assertThat(leastInstalledCategories.getText()).isEqualTo("[leastInstalledCategoryId]");
    }

    @Test
    public void onViewCreated호출시_가장_많이_설치된_카테고리에_대한_정보를_표시한다() throws Exception {
        assertThat(installedAppCount.getText()).isEqualTo("400");
        assertThat(String.valueOf(mostInstalledCategoryRate.getText())).isEqualTo("categoryId1앱이 25%");
        assertThat(mostInstalledCategoryDescriptioinView.getText()).isEqualTo("categoryId1의 설명입니다");
    }
}
