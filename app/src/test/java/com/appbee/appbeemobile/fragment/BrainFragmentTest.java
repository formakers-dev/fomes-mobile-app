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

        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("categoryId1");
        categoryList.add("categoryId2");
        categoryList.add("categoryId3");

        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_USED_CATEGORIES, categoryList);

        subject = new BrainFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
    }

    @Test
    public void onViewCreated호출시_가장많이설치한카테고리목록이_나타난다() throws Exception {
        assertThat(((TextView) subject.getView().findViewById(R.id.most_installed_categories)).getText()).isEqualTo("[categoryId1, categoryId2, categoryId3]");
    }
}
