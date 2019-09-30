package com.formakers.fomes.provisioning;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.custom.SwipeViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class ProvisioningActivity extends BaseActivity implements ProvisioningContract.View {

    private static final String TAG = ProvisioningActivity.class.getSimpleName();

    @BindView(R.id.provision_viewpager) SwipeViewPager viewPager;
    @BindView(R.id.next_button) Button nextButton;

    @Inject ProvisioningContract.Presenter presenter;
    private HashMap<String, BaseFragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerProvisioningDagger_Component.builder()
            .applicationComponent(FomesApplication.get(this).getComponent())
            .module(new ProvisioningDagger.Module(this))
            .build()
            .inject(this);

        this.setContentView(R.layout.activity_provisioning);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        fragmentMap.put(ProvisioningUserInfoFragment.TAG, new ProvisioningUserInfoFragment().setPresenter(this.presenter));
        fragmentMap.put(ProvisioningNickNameFragment.TAG, new ProvisioningNickNameFragment().setPresenter(this.presenter));
        fragmentMap.put(ProvisioningPermissionFragment.TAG, new ProvisioningPermissionFragment().setPresenter(this.presenter));

        ProvisioningPagerAdapter provisioningPagerAdapter = new ProvisioningPagerAdapter(getSupportFragmentManager());
        provisioningPagerAdapter.addFragment(fragmentMap.get(ProvisioningNickNameFragment.TAG));
        provisioningPagerAdapter.addFragment(fragmentMap.get(ProvisioningUserInfoFragment.TAG));
        provisioningPagerAdapter.addFragment(fragmentMap.get(ProvisioningPermissionFragment.TAG));

        viewPager.setAdapter(provisioningPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setEnableSwipe(false);

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
    public void setNextButtonVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        if (nextButton.getVisibility() == visibility) return;

        nextButton.setEnabled(isVisible);
        nextButton.setVisibility(visibility);
    }

    @Override
    public void setNextButtonText(@StringRes int stringResId) {
        nextButton.setText(stringResId);
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
        setNextButtonVisibility(false);
        getCurrentFragmentCommunicator().onNextButtonClick();
    }

    @OnPageChange(value = R.id.provision_viewpager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onSelectedPage(int position) {
        getFragmentCommunicator(position).onSelectedPage();
    }

    @OnPageChange(value = R.id.provision_viewpager, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
    public void onPageScrolledStateChanged(int state) {
        Log.i(TAG, "state=" + state);
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                this.viewPager.endFakeDrag();
                break;
            default:
                this.viewPager.beginFakeDrag();
                break;
        }
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
