package com.formakers.fomes.provisioning.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.provisioning.presenter.ProvisioningPresenter;
import com.formakers.fomes.util.FomesConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class ProvisioningActivity extends BaseActivity implements ProvisioningContract.View {

    private static final String TAG = ProvisioningActivity.class.getSimpleName();

    @BindView(R.id.provision_icon_imageview) ImageView iconImageView;
    @BindView(R.id.provision_viewpager) ViewPager viewPager;
    @BindView(R.id.next_button) Button nextButton;

    private ProvisioningContract.Presenter presenter;
    private HashMap<String, BaseFragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPresenter(new ProvisioningPresenter(this));
        this.setContentView(R.layout.activity_provisioning);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        fragmentMap.put(ProvisioningUserInfoFragment.TAG, new ProvisioningUserInfoFragment().setPresenter(this.presenter));
        fragmentMap.put(ProvisioningLifeGameFragment.TAG, new ProvisioningLifeGameFragment().setPresenter(this.presenter));
        fragmentMap.put(ProvisioningNickNameFragment.TAG, new ProvisioningNickNameFragment().setPresenter(this.presenter));
        fragmentMap.put(ProvisioningPermissionFragment.TAG, new ProvisioningPermissionFragment().setPresenter(this.presenter));

        ProvisioningPagerAdapter provisioningPagerAdapter = new ProvisioningPagerAdapter(getSupportFragmentManager());
        provisioningPagerAdapter.addFragment(fragmentMap.get(ProvisioningUserInfoFragment.TAG));
        provisioningPagerAdapter.addFragment(fragmentMap.get(ProvisioningLifeGameFragment.TAG));
        provisioningPagerAdapter.addFragment(fragmentMap.get(ProvisioningNickNameFragment.TAG));
        provisioningPagerAdapter.addFragment(fragmentMap.get(ProvisioningPermissionFragment.TAG));

        viewPager.setAdapter(provisioningPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.beginFakeDrag();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            String selectedFragment = bundle.getString(FomesConstants.EXTRA.START_FRAGMENT_NAME);

            if (!TextUtils.isEmpty(selectedFragment)) {
                if (ProvisioningPermissionFragment.TAG.equals(selectedFragment)) {
                    provisioningPagerAdapter.clear();
                    provisioningPagerAdapter.addFragment(fragmentMap.get(selectedFragment));
                    provisioningPagerAdapter.notifyDataSetChanged();
                    return;
                }
                viewPager.setCurrentItem(provisioningPagerAdapter.getItemPosition(fragmentMap.get(selectedFragment)));
                Log.d(TAG, "viewPager current item = " + viewPager.getCurrentItem());
            }
        }
    }

    @Override
    public void onBackPressed() {
        int currentPosition = viewPager.getCurrentItem();
        if (currentPosition <= 0 || currentPosition >= viewPager.getAdapter().getItemPosition(fragmentMap.get(ProvisioningPermissionFragment.TAG))) {
            this.finishAffinity();
        } else {
            viewPager.setCurrentItem(currentPosition - 1);
        }
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
    public void setIconImage(@DrawableRes int drawableResId) {
        iconImageView.setImageResource(drawableResId);
    }

    @Override
    public void setNextButtonVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        if (nextButton.getVisibility() == visibility) return;

        nextButton.setVisibility(visibility);
    }

    @Override
    public void setNextButtonText(@StringRes int stringResId) {
        nextButton.setText(stringResId);
    }

    @Override
    public ApplicationComponent getApplicationComponent() {
        return ((FomesApplication) this.getApplication()).getComponent();
    }

    @Override
    public void startActivityAndFinish(Class<?> destActivity) {
        Intent intent = new Intent(this, destActivity);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean isSelectedFragement(BaseFragment fragment) {
        return this.viewPager.getCurrentItem() == this.viewPager.getAdapter().getItemPosition(fragment);
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

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof Fragment) {
                return fragmentList.indexOf(object);
            } else {
                throw new IllegalArgumentException("it is not a Fragment!");
            }
        }

        public void clear() {
            fragmentList.clear();
        }

        public List<Fragment> getFragmentList() {
            return fragmentList;
        }
    }
}
