package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.CommonPagerAdapter;
import com.appbee.appbeemobile.adapter.ContentsPagerAdapter;
import com.appbee.appbeemobile.fragment.InterviewListFragment;
import com.appbee.appbeemobile.fragment.ProjectListFragment;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.FormatUtil;

import javax.inject.Inject;

import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @DrawableRes
    static final int[] BANNER_IMAGES = {
            R.drawable.banner_title_1,
            R.drawable.banner_title_2,
            R.drawable.banner_title_3
    };
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.main_nested_scrollview)
    NestedScrollView mainNestedScrollView;

    @BindView(R.id.title_banner_view_pager)
    ViewPager titleBannerViewPager;

    @BindView(R.id.contents_view_pager)
    ViewPager contentsViewPager;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    TextView userIdTextView;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    ProjectService projectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        userIdTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_id);
        userIdTextView.setText(FormatUtil.parseEmailName(localStorageHelper.getEmail()));

        mainNestedScrollView.setFillViewport(true);

        titleBannerViewPager.setAdapter(new CommonPagerAdapter(this, BANNER_IMAGES));

        ContentsPagerAdapter contentsPagerAdapter = new ContentsPagerAdapter(getSupportFragmentManager());
        contentsPagerAdapter.addFragment(new InterviewListFragment(), getString(R.string.contents_title_interview));
        contentsPagerAdapter.addFragment(new ProjectListFragment(), getString(R.string.contents_title_project));
        contentsViewPager.setAdapter(contentsPagerAdapter);

        tabLayout.setupWithViewPager(contentsViewPager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_interview) {
            Intent intent = new Intent(this, MyInterviewActivity.class);
            startActivity(intent);
        } else if (id == R.id.my_app_usage_pattern) {

        } else if (id == R.id.appbee_question) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@appbee.info"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "[문의]");
            intent.putExtra(Intent.EXTRA_TEXT, "앱비에게 문의해주세요");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
