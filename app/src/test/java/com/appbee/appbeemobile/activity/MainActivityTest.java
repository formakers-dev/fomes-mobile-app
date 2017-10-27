package com.appbee.appbeemobile.activity;

import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
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
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private MainActivity subject;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

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
    public void onPostCreate시_당신을위한추천앱_영역이_나타난다() throws Exception {
        assertThat(subject.recommendationAppsTitleTextView.getText()).isEqualTo("당신을 위한 추천 앱*");
        assertThat(subject.recommendationAppsSubtitleTextView.getText()).isEqualTo("인터뷰나 테스트 참여가 가능한 프로젝트입니다.");
    }

    @Test
    public void onPostCreate시_이런앱은어때요_영역이_나타난다() throws Exception {
        assertThat(subject.introducingAppsTitle.getText()).isEqualTo("이런 앱은 어때요*");
        assertThat(subject.introducingAppsSubtitle.getText()).isEqualTo("당신의 응원을 기다리고 있는 프로젝트 입니다.");
    }
}