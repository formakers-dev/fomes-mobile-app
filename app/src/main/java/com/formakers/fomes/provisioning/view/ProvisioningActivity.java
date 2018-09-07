package com.formakers.fomes.provisioning.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.formakers.fomes.R;
import com.formakers.fomes.activity.BaseActivity;
import com.formakers.fomes.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ProvisioningActivity extends BaseActivity {

    @BindView(R.id.provision_viewpager) ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_provisioning);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ProvisioningPagerAdapter provisioningPagerAdapter = new ProvisioningPagerAdapter(getSupportFragmentManager());
//        provisioningPagerAdapter.addFragment();
        viewPager.setAdapter(provisioningPagerAdapter);
    }

    public class ProvisioningPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();

        public ProvisioningPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(BaseFragment fragment) {
            fragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
