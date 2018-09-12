package com.formakers.fomes.activity;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import javax.inject.Inject;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class MainActivityTest extends BaseActivityTest<MainActivity> {

    @Inject
    SharedPreferencesHelper mockSharedPreferencesHelper;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockSharedPreferencesHelper.getEmail()).thenReturn("anyEmail@gmail.com");

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        super.tearDown();
    }

    @Test
    public void onPostCreate시_메뉴에_사용자의_아이디가_포맷에_맞게_보여진다() throws Exception {
        assertThat(subject.userIdTextView.getText()).isEqualTo("anyEmail");
    }

    @Test
    public void onPostCreate시_2개의_탭과_페이저가_나타난다() throws Exception {
        assertThat(subject.contentsViewPager.getAdapter().getCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabAt(0).getText()).isEqualTo("체험하기");
        assertThat(subject.tabLayout.getTabAt(1).getText()).isEqualTo("둘러보기");
    }

    @Test
    public void onPostCreate시_toolbar의_메뉴버튼클릭시_drawer가_열린다() throws Exception {
        subject.toolbar.getChildAt(0).performClick();

        assertThat(subject.drawer.isDrawerOpen(GravityCompat.START)).isTrue();
    }

    @Test
    public void onNavigationItemSelected시_다가오는_인터뷰_버튼이_클릭되었을_경우_열려있는메뉴를_닫고_신청한_인터뷰리스트_페이지로_이동한다() throws Exception {
        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(R.id.my_interview);

        subject.onNavigationItemSelected(item);

        assertThat(subject.drawer.isDrawerOpen(GravityCompat.START)).isFalse();

        subject.onDrawerClosed(subject.drawer);

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).contains("MyInterviewActivity");
    }

    @Test
    public void onNavigationItemSelected시_앱비에게문의하기_메뉴을_클릭하면_열려있는메뉴를_닫고_메일을_보내는앱을_호출한다() throws Exception {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.appbee_question);

        subject.onNavigationItemSelected(menuItem);

        assertThat(subject.drawer.isDrawerOpen(GravityCompat.START)).isFalse();

        subject.onDrawerClosed(subject.drawer);

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedIntent.getType()).isEqualTo("message/rfc822");
        assertThat(nextStartedIntent.getStringArrayExtra(Intent.EXTRA_EMAIL)).isEqualTo(new String[]{"admin@appbee.info"});
        assertThat(nextStartedIntent.getStringExtra(Intent.EXTRA_SUBJECT)).isEqualTo("[문의]");
        assertThat(nextStartedIntent.getStringExtra(Intent.EXTRA_TEXT)).isEqualTo("앱비에게 문의해주세요");
    }

    @Test
    public void onNavigationItemSelected시_앱사용패턴다시분석하기_메뉴을_클릭하면_열려있는메뉴를_닫고_성향분석페이지로이동한다() throws Exception {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.my_app_usage_pattern);

        subject.onNavigationItemSelected(menuItem);

        assertThat(subject.drawer.isDrawerOpen(GravityCompat.START)).isFalse();

        subject.onDrawerClosed(subject.drawer);

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedIntent.getComponent().getClassName()).isEqualTo(MyAppUsageActivity.class.getCanonicalName());
    }

    @Test
    public void onNavigationItem선택하지않고_drawer를_닫으면_다음화면으로_이동하지않는다() throws Exception {
        subject.toolbar.getChildAt(0).performClick();

        assertThat(subject.drawer.isDrawerOpen(GravityCompat.START)).isTrue();

        subject.onDrawerClosed(subject.drawer);

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedIntent).isNull();
    }
}