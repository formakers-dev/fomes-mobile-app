package com.formakers.fomes.main.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.widget.Toast;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.adapter.ContentsPagerAdapter;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.main.presenter.MainPresenter;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.settings.SettingsActivity;

import butterknife.BindView;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends FomesBaseActivity implements MainContract.View,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, TabLayout.OnTabSelectedListener {

    @BindView(R.id.main_drawer_layout)          DrawerLayout drawerLayout;
    @BindView(R.id.main_side_bar_layout)        NavigationView navigationView;
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

        ContentsPagerAdapter contentsPagerAdapter = new ContentsPagerAdapter(getSupportFragmentManager());
        contentsPagerAdapter.addFragment(new RecommendFragment(), getString(R.string.main_tab_recommend));
        contentsPagerAdapter.addFragment(new BetatestFragment(), getString(R.string.main_tab_betatest));
        contentsViewPager.setAdapter(contentsPagerAdapter);

        this.tabLayout.setupWithViewPager(contentsViewPager);
        this.tabLayout.addOnTabSelectedListener(this);
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
        menu.findItem(R.id.option_menu_my_hope_games).getIcon().setTint(getResources().getColor(R.color.fomes_white));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        switch(item.getItemId()) {
            case R.id.my_recent_analysis: {
                startActivity(new Intent(this, RecentAnalysisReportActivity.class));
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
        Spannable wordtoSpan = new SpannableString(String.valueOf(tab.getText()));
        wordtoSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab.setText(wordtoSpan);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Spannable wordtoSpan = new SpannableString(String.valueOf(tab.getText()));
        wordtoSpan.setSpan(new StyleSpan(Typeface.NORMAL), 0, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab.setText(wordtoSpan);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((AppBeeApplication) getApplication()).getComponent();
    }

    private void verifyAccessToken() {
        addToCompositeSubscription(
            presenter.requestVerifyAccessToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, e -> {
                    // TODO : 이거 넘나 공통화 시키고 싶다
                    if (e instanceof HttpException) {
                        int code = ((HttpException) e).code();
                        if (code == 401 || code == 403) {
                            Toast.makeText(this, "인증 오류가 발생하였습니다. 재로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                            startActivity(LoginActivity.class);
                            finish();
                            return;
                        }
                    }

                    Toast.makeText(this, "예상치 못한 에러가 발생하였습니다. e=" + String.valueOf(e), Toast.LENGTH_SHORT).show();
                })
        );
    }

    private <T> void startActivity(Class<T> destActivity) {
        Intent intent = new Intent(this, destActivity);
        startActivity(intent);
    }
}
