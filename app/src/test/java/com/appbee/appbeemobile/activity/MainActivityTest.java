package com.appbee.appbeemobile.activity;

import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.ClabAppsAdapter;
import com.appbee.appbeemobile.adapter.RecommendationAppsAdapter;
import com.appbee.appbeemobile.helper.LocalStorageHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private MainActivity subject;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Inject
    RecommendationAppsAdapter mockRecommendationAppsAdapter;

    @Inject
    ClabAppsAdapter mockClabAppsAdapter;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        when(mockLocalStorageHelper.getEmail()).thenReturn("anyEmail");

        subject = Robolectric.buildActivity(MainActivity.class).create().postCreate(null).get();
    }

    @Test
    public void onPostCreate시_3페이지를_갖는_배너가_나타난다() throws Exception {
        assertThat(subject.titleBannerViewPager.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.titleBannerViewPager.getAdapter().getCount()).isEqualTo(3);
    }

    @Test
    public void onPostCreate시_당신의참여를기다리는프로젝트를_표시하기위한_Adapter를_매핑한다() throws Exception {
        assertThat(subject.recommendationAppsRecyclerview.getAdapter().getClass().getSimpleName()).contains(RecommendationAppsAdapter.class.getSimpleName());
    }

    @Test
    public void onPostCreate시_취향저격Clab프로젝트둘러보기를_표시하기위한_Adapter를_매핑한다() throws Exception {
        assertThat(subject.clabAppsRecyclerview.getAdapter().getClass().getSimpleName()).contains(ClabAppsAdapter.class.getSimpleName());
    }

    @Test
    public void onResume시_프로젝트목록정보_갱신을_요청한다() throws Exception {
        subject.onResume();

        verify(mockRecommendationAppsAdapter).refreshProjectList();
        verify(mockClabAppsAdapter).refreshProjectList();
    }
}