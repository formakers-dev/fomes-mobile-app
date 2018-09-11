package com.formakers.fomes.provisioning.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.activity.BaseActivity;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.provisioning.presenter.ProvisioningPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class ProvisioningActivity extends BaseActivity implements ProvisioningContract.View {

    @BindView(R.id.provision_viewpager) ViewPager viewPager;
    @BindView(R.id.next_button) Button nextButton;

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
        viewPager.setOffscreenPageLimit(3);
        viewPager.beginFakeDrag();
    }

    @Override
    public void onBackPressed() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    @Override
    public void setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void nextPage() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    @Override
    public void setNextButtonVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        if (nextButton.getVisibility() == visibility) return;

        nextButton.setVisibility(visibility);
    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((AppBeeApplication) this.getApplication()).getComponent();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        getCurrentFragmentCommunicator().onNextButtonClick();
    }

    @OnPageChange(value = R.id.provision_viewpager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onSelectedPage(int position) {
        getFragmentCommunicator(position).onSelectedPage();
    }

    private FragmentCommunicator getFragmentCommunicator(int position) {
        Fragment currentFragment = ((ProvisioningPagerAdapter) viewPager.getAdapter()).getItem(position);
        if (currentFragment instanceof FragmentCommunicator) {
            return (FragmentCommunicator) currentFragment;
        } else {
            throw new IllegalArgumentException("Current Fragment didn't implement FragmentCommunicator!");
        }
    }

    private FragmentCommunicator getCurrentFragmentCommunicator() {
        return getFragmentCommunicator(viewPager.getCurrentItem());
    }

    public interface FragmentCommunicator {
        void onNextButtonClick();
        void onSelectedPage();
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
