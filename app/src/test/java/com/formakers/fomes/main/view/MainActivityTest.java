package com.formakers.fomes.main.view;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.common.view.FomesBaseActivityTest;
import com.formakers.fomes.main.adapter.EventPagerAdapter;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.settings.SettingsActivity;
import com.formakers.fomes.wishList.view.WishListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class MainActivityTest extends FomesBaseActivityTest<MainActivity> {

    private TestScheduler testScheduler;

    @Mock MainContract.Presenter mockPresenter;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void launchActivity() {
        subject = getActivityController().get();
//        subject = getActivity(LIFECYCLE_TYPE_CREATE);
        subject.setPresenter(mockPresenter);
        ActivityController<MainActivity> a = getActivityController().create().start().postCreate(null).resume();
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
        ((TestFomesApplication) RuntimeEnvironment.application).getComponent().inject(this);
        super.setUp();

        User user = new User().setNickName("testUserNickName").setEmail("test@email.com");
        when(mockPresenter.requestUserInfo()).thenReturn(Single.just(user));
        when(mockPresenter.requestVerifyAccessToken()).thenReturn(Completable.complete());

        when(mockPresenter.checkRegisteredSendDataJob()).thenReturn(true);
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
    public void MainActivity_시작시__사이드메뉴에_유저정보가_셋팅된다() {
        launchActivity();

        verify(mockPresenter).requestUserInfo();

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
    public void 옵선아이템의_즐겨찾기_클릭시__즐겨찾기화면으로_이동한다() {
        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(R.id.my_wish_list);

        launchActivity();
        subject.onOptionsItemSelected(item);

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(WishListActivity.class.getSimpleName());
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
        when(mockPresenter.getPromotionCount()).thenReturn(3);

        launchActivity();

        List<Post> dummyPosts = new ArrayList<>();
        dummyPosts.add(new Post());
        dummyPosts.add(new Post());
        dummyPosts.add(new Post());

        ViewPager viewPager = subject.findViewById(R.id.main_event_view_pager);
        ((EventPagerAdapter) viewPager.getAdapter()).addAll(dummyPosts);

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(0);

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(1);

        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        assertThat(viewPager.getCurrentItem()).isEqualTo(2);
    }
}