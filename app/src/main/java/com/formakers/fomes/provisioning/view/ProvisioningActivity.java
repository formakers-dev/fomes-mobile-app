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
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.provisioning.presenter.ProvisioningPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ProvisioningActivity extends BaseActivity implements ProvisioningContract.View {

    @BindView(R.id.provision_viewpager) ViewPager viewPager;

    private ProvisioningContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPresenter(new ProvisioningPresenter(this));
        this.setContentView(R.layout.activity_provisioning);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ProvisioningPagerAdapter provisioningPagerAdapter = new ProvisioningPagerAdapter(getSupportFragmentManager());
        provisioningPagerAdapter.addFragment(new ProvisioningUserInfoFragment().setPresenter(this.presenter));
        provisioningPagerAdapter.addFragment(new ProvisioningLifeGameFragment().setPresenter(this.presenter));
        provisioningPagerAdapter.addFragment(new ProvisioningNickNameFragment().setPresenter(this.presenter));
        viewPager.setAdapter(provisioningPagerAdapter);
    }

    @Override
    public void setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void nextPage() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        Fragment currentFragment = ((ProvisioningPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        if (currentFragment instanceof FragmentCommunicator) {
            ((FragmentCommunicator) currentFragment).onNextButtonClick();
        } else {
            throw new IllegalArgumentException("Current Fragment didn't implement FragmentCommunicator!");
        }
    }

    public interface FragmentCommunicator {
        void onNextButtonClick();
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

        public List<Fragment> getFragmentList() {
            return fragmentList;
        }
    }
}
