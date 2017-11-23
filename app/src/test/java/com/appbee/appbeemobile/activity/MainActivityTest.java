package com.appbee.appbeemobile.activity;

import android.view.MenuItem;
import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.ProjectService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private MainActivity subject;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Inject
    ProjectService mockProjectService;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockLocalStorageHelper.getEmail()).thenReturn("anyEmail@gmail.com");

        subject = Robolectric.buildActivity(MainActivity.class).create().postCreate(null).get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onPostCreate시_메뉴에_사용자의_아이디가_포맷에_맞게_보여진다() throws Exception {
        assertThat(subject.userIdTextView.getText()).isEqualTo("anyEmail");
    }

    @Test
    public void onPostCreate시_3페이지를_갖는_배너가_나타난다() throws Exception {
        assertThat(subject.titleBannerViewPager.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.titleBannerViewPager.getAdapter().getCount()).isEqualTo(3);
    }

    @Test
    public void onPostCreate시_2개의_탭과_페이저가_나타난다() throws Exception {
        assertThat(subject.contentsViewPager.getAdapter().getCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabAt(0).getText()).isEqualTo("체험하기");
        assertThat(subject.tabLayout.getTabAt(1).getText()).isEqualTo("둘러보기");
    }

    @Test
    public void onNavigationItemSelected시_다가오는_인터뷰_버튼이_클릭되었을_경우_신청한_인터뷰리스트_페이지로_이동한다() throws Exception {
        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(R.id.my_interview);
        subject.onNavigationItemSelected(item);
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).contains("MyInterviewActivity");
    }
}