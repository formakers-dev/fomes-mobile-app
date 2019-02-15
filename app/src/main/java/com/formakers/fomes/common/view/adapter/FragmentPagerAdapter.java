package com.formakers.fomes.common.view.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.formakers.fomes.common.view.BaseFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private LinkedHashMap<String, ContentsFragment> contentsMap = new LinkedHashMap<>();

    public FragmentPagerAdapter(FragmentManager fm) {
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

    public int getPosition(@NonNull Fragment fragment) {
        ArrayList<ContentsFragment> contents = new ArrayList<>(contentsMap.values());

        for (ContentsFragment content : contents) {
            if (fragment.equals(content.fragment)) {
                return contents.indexOf(content);
            }
        }

        return -1;
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
