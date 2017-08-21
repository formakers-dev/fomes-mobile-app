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

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BrainFragmentTest {

    private BrainFragment subject;

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

        subject = new BrainFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
    }

    @Test
    public void onViewCreated호출시_가장많이설치한카테고리목록이_나타난다() throws Exception {
        assertThat(((TextView) subject.getView().findViewById(R.id.most_installed_categories)).getText()).isEqualTo("[categoryId1, categoryId2, categoryId3]");
    }

    @Test
    public void onViewCreated호출시_가장적게설치한카테고리목록이_나타난다() throws Exception {
        assertThat(((TextView) subject.getView().findViewById(R.id.least_installed_categories)).getText()).isEqualTo("[leastInstalledCategoryId]");
    }
}
