package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.fragment.OnboardingAnalysisFragment;
import com.appbee.appbeemobile.fragment.OnboardingRewardsFragment;

import butterknife.OnClick;

public class OnboardingAnalysisActivity extends BaseActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboardin_analysis);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new OnboardingAnalysisFragment(), OnboardingAnalysisFragment.TAG);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        if (fragmentManager.findFragmentByTag(OnboardingAnalysisFragment.TAG) != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new OnboardingRewardsFragment(), OnboardingRewardsFragment.TAG);
            fragmentTransaction.commit();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        }
    }
}
