package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.CommonPagerAdapter;
import com.appbee.appbeemobile.adapter.CommonRecyclerViewAdapter;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.util.FormatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.title_banner_view_pager)
    ViewPager titleBannerViewPager;

    @BindView(R.id.recommendation_apps_recyclerview)
    RecyclerView recommendationAppsRecyclerview;

    @BindView(R.id.introducing_apps_title)
    TextView introducingAppsTitle;

    @BindView(R.id.introducing_apps_subtitle)
    TextView introducingAppsSubtitle;

    TextView userIdTextView;

    @Inject
    LocalStorageHelper localStorageHelper;

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

        titleBannerViewPager.setAdapter(new CommonPagerAdapter(this, BANNER_IMAGES));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recommendationAppsRecyclerview.setLayoutManager(llm);

        // TODO : API 로 프로젝트 정보 로딩후 처리
        List<Project> projectList = new ArrayList<>();
        projectList.add(new Project("유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("https://firebasestorage.googleapis.com/v0/b/dragonwebapp.appspot.com/o/images%2Frecommandation_app.png?alt=media&token=58b315db-519d-43d8-8a8a-24c89ef5b21f"), Collections.singletonList("지그재그"), "인터뷰 신청 가능"));
        projectList.add(new Project("유어커스텀2", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("https://firebasestorage.googleapis.com/v0/b/dragonwebapp.appspot.com/o/images%2Frecommandation_app.png?alt=media&token=58b315db-519d-43d8-8a8a-24c89ef5b21f"), Collections.singletonList("지그재그"), "인터뷰 신청 가능"));
        projectList.add(new Project("유어커스텀3", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("https://firebasestorage.googleapis.com/v0/b/dragonwebapp.appspot.com/o/images%2Frecommandation_app.png?alt=media&token=58b315db-519d-43d8-8a8a-24c89ef5b21f"), Collections.singletonList("지그재그"), "인터뷰 신청 가능"));

        CommonRecyclerViewAdapter commonRecyclerViewAdapter = new CommonRecyclerViewAdapter(this, projectList);
        recommendationAppsRecyclerview.setAdapter(commonRecyclerViewAdapter);
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

        if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
