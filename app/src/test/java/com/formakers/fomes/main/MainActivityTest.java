package com.formakers.fomes.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.core.view.MotionEventBuilder;
import androidx.viewpager.widget.ViewPager;

import com.formakers.fomes.R;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.analysis.RecentAnalysisReportActivity;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.common.view.FomesBaseActivityTest;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.formakers.fomes.provisioning.login.LoginActivity;
import com.formakers.fomes.settings.SettingsActivity;
import com.formakers.fomes.wishList.WishListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class MainActivityTest extends FomesBaseActivityTest<MainActivityTest.ShadowMainActivity> {

    private TestScheduler testScheduler;

    @Mock MainContract.Presenter mockPresenter;

    public MainActivityTest() { super(ShadowMainActivity.class); }

    @Override
    public void launchActivity() {
        subject = getActivityController().get();
//        subject = getActivity(LIFECYCLE_TYPE_CREATE);
        subject.setMockPresenter(mockPresenter);
        ActivityController<ShadowMainActivity> a = getActivityController().create().start().postCreate(null).resume();
    }

    @Before
    public void setUp() throws Exception {
        testScheduler = new TestScheduler();

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
//        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> testScheduler);

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        MockitoAnnotations.initMocks(this);
        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);
        super.setUp();

        User user = new User().setNickName("testUserNickName").setEmail("test@email.com");
        when(mockPresenter.requestVerifyAccessToken()).thenReturn(Completable.complete());
        when(mockPresenter.checkRegisteredSendDataJob()).thenReturn(true);
        when(mockPresenter.getInterpretedUrl(anyString())).thenReturn("http://www.naver.com");
    }

    @Override
    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
        super.tearDown();
    }

    @Test
    public void MainActivity_진입시__단기통계데이터전송Job가_등록되어있지않으면_등록한다() {
        when(mockPresenter.checkRegisteredSendDataJob()).thenReturn(false);
        launchActivity();
        verify(mockPresenter).registerSendDataJob();
    }

    @Test
    public void MainActivity_진입시__토큰검증을_시도한다() {
        launchActivity();
        verify(mockPresenter).requestVerifyAccessToken();
    }

    @Test
    public void MainActivity_진입시__토큰_만료_오류_발생시__로그인화면으로_이동하고_종료한다() {
        when(mockPresenter.requestVerifyAccessToken())
                .thenReturn(Completable.error(new HttpException(Response.error(401, ResponseBody.create(null, "")))));

        launchActivity();

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void MainActivity_진입시__토큰_만료_외_오류_발생시__로그인화면으로_이동하고_종료한다() {
        when(mockPresenter.requestVerifyAccessToken())
                .thenReturn(Completable.error(new HttpException(Response.error(403, ResponseBody.create(null, "")))));

        launchActivity();

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(LoginActivity.class.getSimpleName());
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void MainActivity_시작시__유저정보를_바인딩한다() {
        launchActivity();

        verify(mockPresenter).bindUserInfo();
    }

    @Test
    public void setUserInfoToNavigationView_호출시__사이드메뉴에_유저정보가_셋팅된다() {
        launchActivity();

        subject.setUserInfoToNavigationView("test@email.com", "testUserNickName");

        View sideHeaderView = subject.navigationView.getHeaderView(0);
        assertThat(((TextView) sideHeaderView.findViewById(R.id.user_nickname)).getText())
                .isEqualTo("testUserNickName");
        assertThat(((TextView) sideHeaderView.findViewById(R.id.user_email)).getText())
                .isEqualTo("test@email.com");
    }

    @Test
    public void 사이드메뉴의_아이템_클릭시__열려있는_사이드_메뉴를_닫는다() {
        MenuItem item = mock(MenuItem.class);
        when(item.getTitle()).thenReturn("사이드메뉴아이템1");

        launchActivity();
        subject.onNavigationItemSelected(item);

        assertThat(subject.drawerLayout.isDrawerOpen(GravityCompat.START)).isFalse();
    }

    @Test
    public void 사이드메뉴의_분석화면_클릭시__분석화면으로_이동한다() {
        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(R.id.my_recent_analysis);
        when(item.getTitle()).thenReturn("게임 성향 분석");

        launchActivity();
        subject.onNavigationItemSelected(item);

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(RecentAnalysisReportActivity.class.getSimpleName());
    }

    @Test
    public void 사이드메뉴의_설정화면_클릭시__설정화면으로_이동한다() {
        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(R.id.settings);
        when(item.getTitle()).thenReturn("설정");

        launchActivity();
        subject.onNavigationItemSelected(item);

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(SettingsActivity.class.getSimpleName());
    }

    @Test
    public void 사이드메뉴의_즐겨찾기_클릭시__즐겨찾기화면으로_이동한다() {
        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(R.id.my_wish_list);

        launchActivity();
        subject.onNavigationItemSelected(item);

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(WishListActivity.class.getSimpleName());
    }

    @Test
    public void 메뉴의_우체통_클릭시__우체통_화면으로_이동한다() {
        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(R.id.fomes_postbox);

        launchActivity();
        subject.onOptionsItemSelected(item);

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(WebViewActivity.class.getSimpleName());
        assertThat(intent.getStringExtra(FomesConstants.WebView.EXTRA_TITLE)).isEqualTo("포메스 우체통");
        assertThat(intent.getStringExtra(FomesConstants.WebView.EXTRA_CONTENTS)).startsWith("http");
    }

    @Test
    public void 백버튼클릭시__사이드메뉴가_열려있을경우__사이드메뉴를_닫는다() {
        launchActivity();
        subject.drawerLayout.openDrawer(GravityCompat.START);
        subject.onBackPressed();

        assertThat(subject.drawerLayout.isDrawerOpen(GravityCompat.START)).isFalse();
    }

    @Test
    public void onStart시__이벤트_페이저를_3초간격으로_넘긴다() {
        when(mockPresenter.getImageLoader()).thenReturn(mock(ImageLoader.class));
        when(mockPresenter.getPromotionCount()).thenReturn(3);

        launchActivity();

        List<Post> dummyPosts = new ArrayList<>();
        dummyPosts.add(new Post());
        dummyPosts.add(new Post());
        dummyPosts.add(new Post());

        ViewPager viewPager = subject.findViewById(R.id.main_event_view_pager);
        ((EventPagerAdapter) viewPager.getAdapter()).addAll(dummyPosts);

        assertThat(viewPager.getCurrentItem()).isEqualTo(0);
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(0);

        // 최초는 1번은 5초 간격
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(1);

        // 그 이후는 3초 간격
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(2);
    }

    @Test
    public void 이벤트_페이지가_터치스와이프_되었을때__딜레이시간을_초기화시킨다() {
        //given
        when(mockPresenter.getImageLoader()).thenReturn(mock(ImageLoader.class));
        when(mockPresenter.getPromotionCount()).thenReturn(3);

        launchActivity();

        List<Post> dummyPosts = new ArrayList<>();
        dummyPosts.add(new Post());
        dummyPosts.add(new Post());
        dummyPosts.add(new Post());

        ViewPager viewPager = subject.findViewById(R.id.main_event_view_pager);
        ((EventPagerAdapter) viewPager.getAdapter()).addAll(dummyPosts);

        // 최초 5초
        assertThat(viewPager.getCurrentItem()).isEqualTo(0);
        testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(1);

        // 그 이후는 3초 간격으로 진행
        // 2초가 지났다
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(1);

        // when - 도중에 스와이프 이벤트 발생!
        viewPager.onTouchEvent(MotionEventBuilder.newBuilder().setAction(ACTION_MOVE).build());
        viewPager.onTouchEvent(MotionEventBuilder.newBuilder().setAction(ACTION_UP).build());

        // 1초가 추가적으로 지남 => 스와이프 되지 않았다면 2로 넘어가야하는 상황. but 초기화 되었으니 넘어가지 않음
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(1);

        // 스와이프 이후 3초가 지나야 2로 넘어감
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(2);
    }

    public static class ShadowMainActivity extends MainActivity {
        MainContract.Presenter mockPresenter;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            this.setTheme(R.style.FomesTheme_NoActionBar);
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void injectDependency() {
            this.setPresenter(mockPresenter);
        }

        void setMockPresenter(MainContract.Presenter mockPresenter) {
            this.mockPresenter = mockPresenter;
        }
    }
}