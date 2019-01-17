package com.formakers.fomes.main.view;

import android.content.Intent;
import android.graphics.Typeface;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.adapter.FragmentPagerAdapter;
import com.formakers.fomes.main.adapter.EventPagerAdapter;
import com.formakers.fomes.event.EventActivity;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.main.presenter.MainPresenter;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.settings.SettingsActivity;
import com.formakers.fomes.wishList.view.WishListActivity;

import butterknife.BindView;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends FomesBaseActivity implements MainContract.View,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_WISHLIST = 1000;

    @BindView(R.id.main_drawer_layout)          DrawerLayout drawerLayout;
    @BindView(R.id.main_side_bar_layout)        NavigationView navigationView;
    @BindView(R.id.main_event_view_pager)       ViewPager eventViewPager;
    @BindView(R.id.main_toolbar)                Toolbar toolbar;
    @BindView(R.id.main_tab_layout)             TabLayout tabLayout;
    @BindView(R.id.main_contents_view_pager)    ViewPager contentsViewPager;

    MainContract.Presenter presenter;

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.common_empty_string);
        this.setContentView(R.layout.activity_main);
        setPresenter(new MainPresenter(this));

        // 혹시나 모를 상황을 대비해 런치화면 진입시 토큰체크
        verifyAccessToken();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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

        EventPagerAdapter eventPagerAdapter = new EventPagerAdapter();

        eventViewPager.setAdapter(eventPagerAdapter);

        View eventBanner = getLayoutInflater().inflate(R.layout.view_pager_banner_event, null);
        eventPagerAdapter.addView(eventBanner, EventActivity.class);

        eventPagerAdapter.notifyDataSetChanged();
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
        Log.d(TAG, "onTabSelected");
        Spannable wordtoSpan = new SpannableString(String.valueOf(tab.getText()));
        wordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab.setText(wordtoSpan);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabUnselected");
        Spannable wordtoSpan = new SpannableString(String.valueOf(tab.getText()));
        wordtoSpan.setSpan(new StyleSpan(Typeface.NORMAL), 0, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab.setText(wordtoSpan);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((FomesApplication) getApplication()).getComponent();
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
