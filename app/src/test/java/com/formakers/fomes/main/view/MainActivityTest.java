package com.formakers.fomes.main.view;

import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.common.view.FomesBaseActivityTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

//    @Test
//    public void 액션바의_왼쪽상단메뉴_클릭시__사이드메뉴가_열린다() {
//        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);
//        subject.toolbar.getChildAt(0).performClick();
//
//        assertThat(subject.drawerLayout.isDrawerOpen(GravityCompat.START)).isTrue();
//    }

    @Test
    public void 사이드메뉴의_아이템_클릭시__열려있는_사이드_메뉴를_닫고_아이템의_타이틀을_토스트로_띄운다() throws Exception {
        MenuItem item = mock(MenuItem.class);
        when(item.getTitle()).thenReturn("사이드메뉴아이템1");

        launchActivity();
        subject.onNavigationItemSelected(item);

        assertThat(subject.drawerLayout.isDrawerOpen(GravityCompat.START)).isFalse();

        subject.onDrawerClosed(subject.drawerLayout);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("사이드메뉴아이템1");
    }

    @Test
    public void 백버튼클릭시__사이드메뉴가_열려있을경우__사이드메뉴를_닫는다() {
        launchActivity();
        subject.drawerLayout.openDrawer(GravityCompat.START);
        subject.onBackPressed();

        assertThat(subject.drawerLayout.isDrawerOpen(GravityCompat.START)).isFalse();
    }
}