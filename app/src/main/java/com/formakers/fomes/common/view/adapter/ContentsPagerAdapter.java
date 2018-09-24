package com.formakers.fomes.common.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.formakers.fomes.common.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class ContentsPagerAdapter extends FragmentPagerAdapter {

    private List<ContentsFragment> contentsFragmentList = new ArrayList<>();

    public ContentsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(BaseFragment fragment, String title) {
        contentsFragmentList.add(new ContentsFragment(fragment, title));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return contentsFragmentList.get(position).title;
    }

    @Override
    public Fragment getItem(int position) {
        return contentsFragmentList.get(position).fragment;
    }

    @Override
    public int getCount() {
        return contentsFragmentList.size();
    }

    private class ContentsFragment {
        BaseFragment fragment;
        String title;

        ContentsFragment(BaseFragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }
    }
}
