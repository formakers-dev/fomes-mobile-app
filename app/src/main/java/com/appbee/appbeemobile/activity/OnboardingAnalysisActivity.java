package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.OnboardingAnalysisFragment;
import com.appbee.appbeemobile.fragment.OnboardingRewardsFragment;

public class OnboardingAnalysisActivity extends BaseActivity implements IFragmentManager {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboardin_analysis);
        replaceFragment(OnboardingAnalysisFragment.TAG);
    }

    // TODO : replaceFragment, startMainActivityAndFinish 메서드 테스트 필요
    @Override
    public void replaceFragment(@NonNull String fragmentTag) {
        Fragment fragment = null;

        if (OnboardingAnalysisFragment.TAG.equals(fragmentTag)) {
            fragment = new OnboardingAnalysisFragment().setFragmentManager(this);
        } else if (OnboardingRewardsFragment.TAG.equals(fragmentTag)) {
            fragment = new OnboardingRewardsFragment().setFragmentManager(this);
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void startMainActivityAndFinish() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}
