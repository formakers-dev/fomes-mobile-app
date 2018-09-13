package com.formakers.fomes.main.view;

import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.common.view.FomesBaseActivityTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MainActivityTest extends FomesBaseActivityTest<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void launchActivity() {
        subject = getActivity();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        super.setUp();
    }

    @Test
    public void MainActivity_시작시_메인화면이_나타난다() throws Exception  {
        launchActivity();

        assertThat(((ViewGroup) subject.findViewById(R.id.main_side_bar_layout))).isNotNull();
        assertThat(subject.findViewById(R.id.main_content_layout).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void MainActivity_시작시__2개의_탭과_페이저가_나타난다() throws Exception {
        launchActivity();

        assertThat(subject.contentsViewPager.getAdapter().getCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabAt(0).getText()).isEqualTo("게임 추천");
        assertThat(subject.tabLayout.getTabAt(1).getText()).isEqualTo("베타테스트");
    }
}