package com.formakers.fomes.common.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.formakers.fomes.common.view.BaseFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ContentsPagerAdapter extends FragmentPagerAdapter {

    private LinkedHashMap<String, ContentsFragment> contentsMap = new LinkedHashMap<>();

    public ContentsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(String tag, BaseFragment fragment, String title) {
        contentsMap.put(tag, new ContentsFragment(fragment, title));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new ArrayList<>(contentsMap.values()).get(position).title;
    }

    @Override
    public Fragment getItem(int position) {
        return new ArrayList<>(contentsMap.values()).get(position).fragment;
    }

    public Fragment getItem(String tag) {
        return contentsMap.get(tag).fragment;
    }

    @Override
    public int getCount() {
        return contentsMap.values().size();
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
