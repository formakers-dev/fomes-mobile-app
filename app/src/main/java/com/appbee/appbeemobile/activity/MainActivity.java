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
import com.appbee.appbeemobile.adapter.ProjectListAdapter;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.FormatUtil;

import java.util.ArrayList;
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

    @BindView(R.id.project_list_recycler_view)
    RecyclerView projectListRecyclerView;

    TextView userIdTextView;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    ProjectService projectService;

    private List<Project> projectList = new ArrayList<>();

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

        LinearLayoutManager recommendLayoutManger = new LinearLayoutManager(this);
        recommendLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        projectListRecyclerView.setLayoutManager(recommendLayoutManger);
        projectListRecyclerView.setAdapter(new ProjectListAdapter(projectList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectService.getAllProjects().subscribe(projectList -> {
            this.projectList.clear();
            this.projectList.addAll(projectList);
            projectListRecyclerView.getAdapter().notifyDataSetChanged();
        });
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
