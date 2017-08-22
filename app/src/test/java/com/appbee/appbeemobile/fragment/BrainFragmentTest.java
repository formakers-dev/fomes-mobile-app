package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BrainFragmentTest {

    private BrainFragment subject;

    @BindView(R.id.most_installed_categories)
    TextView mostInstalledCategories;

    @BindView(R.id.least_installed_categories)
    TextView leastInstalledCategories;

    @BindView(R.id.installed_app_count)
    TextView installedAppCount;

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

        subject = new BrainFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
        ButterKnife.bind(this, subject.getView());
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
    public void onViewCreated호출시_설치된_앱의_갯수를_표시한다() throws Exception {
        assertThat(installedAppCount.getText()).isEqualTo("400");
    }
}
