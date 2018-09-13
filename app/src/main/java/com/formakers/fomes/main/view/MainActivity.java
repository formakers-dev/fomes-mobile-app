package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.adapter.ContentsPagerAdapter;
import com.formakers.fomes.common.view.FomesBaseActivity;

import butterknife.BindView;

public class MainActivity extends FomesBaseActivity {

    @BindView(R.id.main_tab_layout)             TabLayout tabLayout;
    @BindView(R.id.main_contents_view_pager)    ViewPager contentsViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
        this.setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ContentsPagerAdapter contentsPagerAdapter = new ContentsPagerAdapter(getSupportFragmentManager());
        contentsPagerAdapter.addFragment(new RecommendFragment(), getString(R.string.main_tab_recommend));
        contentsPagerAdapter.addFragment(new BetatestFragment(), getString(R.string.main_tab_betatest));
        contentsViewPager.setAdapter(contentsPagerAdapter);

        this.tabLayout.setupWithViewPager(contentsViewPager);
    }
}
