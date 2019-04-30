package com.formakers.fomes.common.view.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.formakers.fomes.common.view.BaseFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

    @Override
    public int getCount() {
        return contentsMap.values().size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof BaseFragment) {
            List<ContentsFragment> contentsList = new ArrayList<>(contentsMap.values());

            for (ContentsFragment contentsFragment: contentsList) {
                if (contentsFragment.fragment == object) {
                    return contentsList.indexOf(contentsFragment);
                }
            }
        } else {
            throw new IllegalArgumentException("this object (" + object + ") is not a child of this adapter");
        }

        return super.getItemPosition(object);
    }

    // TODO : 리팩토링 필요. 타이틀을 프래그먼트에서 가져오는걸로 하는게 어떨까?
    private class ContentsFragment {
        BaseFragment fragment;
        String title;

        ContentsFragment(BaseFragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }
    }
}
