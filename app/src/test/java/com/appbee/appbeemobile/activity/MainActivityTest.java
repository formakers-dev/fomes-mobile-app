package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.view.MenuItem;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.LocalStorageHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
public class MainActivityTest extends ActivityTest {

    private MainActivity subject;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

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

    @Test
    public void onNavigationItemSelected시_앱비에게문의하기_메뉴을_클릭하면_메일을_보내는앱을_호출한다() throws Exception {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.appbee_question);

        subject.onNavigationItemSelected(menuItem);

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedIntent.getType()).isEqualTo("message/rfc822");
        assertThat(nextStartedIntent.getStringArrayExtra(Intent.EXTRA_EMAIL)).isEqualTo(new String[]{"admin@appbee.info"});
        assertThat(nextStartedIntent.getStringExtra(Intent.EXTRA_SUBJECT)).isEqualTo("[문의]");
        assertThat(nextStartedIntent.getStringExtra(Intent.EXTRA_TEXT)).isEqualTo("앱비에게 문의해주세요");
    }

    @Test
    @Ignore
    public void onNavigationItemSelected시_앱사용패턴다시분석하기_메뉴을_클릭하면_성향분석페이지로이동한다() throws Exception {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.my_app_usage_pattern);

        subject.onNavigationItemSelected(menuItem);

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
//        assertThat(nextStartedIntent.getComponent().getClassName()).isEqualTo()
    }
}