package com.formakers.fomes.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.adapter.FragmentPagerAdapter;
import com.formakers.fomes.main.adapter.EventPagerAdapter;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.main.presenter.MainPresenter;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.settings.SettingsActivity;
import com.formakers.fomes.wishList.view.WishListActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends FomesBaseActivity implements MainContract.View,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_WISHLIST = 1000;

    public static final int EVENT_AUTO_SLIDE_MILLISECONDS = 3000;

    @BindView(R.id.main_drawer_layout)          DrawerLayout drawerLayout;
    @BindView(R.id.main_side_bar_layout)        NavigationView navigationView;
    @BindView(R.id.main_event_view_pager)       ViewPager eventViewPager;
    @BindView(R.id.main_toolbar)                Toolbar toolbar;
    @BindView(R.id.main_tab_layout)             TabLayout tabLayout;
    @BindView(R.id.main_contents_view_pager)    ViewPager contentsViewPager;

    private Subscription eventPagerAutoSlideSubscription;

    private MainContract.Presenter presenter;

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate");

        this.setTitle(R.string.common_empty_string);
        this.setContentView(R.layout.activity_main);
        setPresenter(new MainPresenter(this));

        // 혹시나 모를 상황을 대비해 런치화면 진입시 토큰체크
        verifyAccessToken();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.v(TAG,"onPostCreate");

        boolean isRegisteredSendDataJob = presenter.checkRegisteredSendDataJob();
        Log.i(TAG, "isRegisteredSendDataJob=" + isRegisteredSendDataJob);
        if (!isRegisteredSendDataJob) {
            this.presenter.registerSendDataJob();
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerToggle.setDrawerSlideAnimationEnabled(true);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().clear();

        navigationView.inflateMenu(R.menu.main_nav);

        addToCompositeSubscription(
            presenter.requestUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_nickname))
                            .setText(user.getNickName());
                    ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email))
                            .setText(user.getEmail());
                })
        );

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());

        fragmentPagerAdapter.addFragment(BetaTestFragment.TAG, new BetaTestFragment(), getString(R.string.main_tab_betatest));
        fragmentPagerAdapter.addFragment(RecommendFragment.TAG, new RecommendFragment(), getString(R.string.main_tab_recommend));

        contentsViewPager.setAdapter(fragmentPagerAdapter);

        this.tabLayout.setupWithViewPager(contentsViewPager);
        this.tabLayout.addOnTabSelectedListener(this);

        EventPagerAdapter eventPagerAdapter = new EventPagerAdapter(this);

        eventViewPager.setAdapter(eventPagerAdapter);

        presenter.requestPromotions();

        if (getIntent().getBooleanExtra(FomesConstants.EXTRA.IS_FROM_NOTIFICATION, false)) {
            presenter.sendEventLog(FomesConstants.EventLog.Code.NOTIFICATION_TAP);
        } else {
            presenter.sendEventLog(FomesConstants.EventLog.Code.MAIN_ACTIVITY_ENTER);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Start Event AutoSlide
        eventPagerAutoSlideSubscription = Observable.interval(EVENT_AUTO_SLIDE_MILLISECONDS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seq -> showNextEventBanner());
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Stop Event AutoSlide
        if (eventPagerAutoSlideSubscription != null && !eventPagerAutoSlideSubscription.isUnsubscribed()) {
            eventPagerAutoSlideSubscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (presenter != null) {
            presenter.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.my_wish_list).setVisible(true);
        menu.findItem(R.id.my_recent_analysis).setVisible(false);
        menu.findItem(R.id.my_wish_list).getIcon().setTint(getResources().getColor(R.color.fomes_white));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onNavigationItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_recent_analysis: {
                startActivity(new Intent(this, RecentAnalysisReportActivity.class));
                break;
            }
            case R.id.my_wish_list: {
                startActivityForResult(new Intent(this, WishListActivity.class), REQUEST_CODE_WISHLIST);
                break;
            }
            case R.id.settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_WISHLIST) {
            Fragment fragment = ((FragmentPagerAdapter) contentsViewPager.getAdapter()).getItem(RecommendFragment.TAG);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        presenter.sendEventLog((tab.getPosition() == 0) ?
                FomesConstants.EventLog.Code.MAIN_ACTIVITY_TAP_BETA_TEST :
                FomesConstants.EventLog.Code.MAIN_ACTIVITY_TAP_RECOMMEND);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((FomesApplication) getApplication()).getComponent();
    }

    public void showNextEventBanner() {
        if (eventViewPager == null || eventViewPager.getAdapter() == null || eventViewPager.getAdapter().getCount() < 2)
            return;

        int nextItem = (eventViewPager.getCurrentItem() < eventViewPager.getAdapter().getCount() - 1) ? eventViewPager.getCurrentItem() + 1 : 0;

        Log.v(TAG, "showNextEventBanner) position=" + nextItem);
        eventViewPager.setCurrentItem(nextItem);
    }

    @Override
    public void setPromotionViews(List<Post> promotions) {
        EventPagerAdapter adapter = (EventPagerAdapter) eventViewPager.getAdapter();

        for (Post post : promotions) {
            adapter.addEvent(post);
        }

        adapter.notifyDataSetChanged();
    }

    private void verifyAccessToken() {
        addToCompositeSubscription(
            presenter.requestVerifyAccessToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, e -> {
                    if (e instanceof HttpException) {
                        int code = ((HttpException) e).code();

                        // 401 : 토큰이 만료되었으므로 재발급을 위해 로그인 화면으로 보낸다.
                        // 403 : 만료 외 토큰 인증 실패이므로 메인화면에 진입할 수 없어야한다. -> 아무 권한 없이도 볼 수 있는 화면으로 보낸다. (로그인 화면)
                        if (code == 401 || code == 403) {
                            Log.d(TAG, "인증 오류가 발생하였습니다. 재로그인이 필요합니다.");
                            startActivity(LoginActivity.class);
                            finish();
                            return;
                        }
                    }

                    Log.e(TAG, "예상치 못한 에러가 발생하였습니다. e=" + String.valueOf(e));
                })
        );
    }

    private <T> void startActivity(Class<T> destActivity) {
        Intent intent = new Intent(this, destActivity);
        startActivity(intent);
    }
}
