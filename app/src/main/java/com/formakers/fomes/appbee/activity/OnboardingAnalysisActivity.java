package com.formakers.fomes.appbee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;

import com.formakers.fomes.R;
import com.formakers.fomes.appbee.fragment.AppUsageAnalysisFragment;
import com.formakers.fomes.appbee.fragment.OnboardingRewardsFragment;
import com.formakers.fomes.common.view.BaseActivity;

import butterknife.OnClick;

@Deprecated
public class OnboardingAnalysisActivity extends BaseActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboardin_analysis);

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, createAppUsageAnalysisFragment(), AppUsageAnalysisFragment.TAG);
        fragmentTransaction.commit();
    }

    @NonNull
    private AppUsageAnalysisFragment createAppUsageAnalysisFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(AppUsageAnalysisFragment.EXTRA_DESCRIPTION_RES_ID, R.string.analysis_description);
        AppUsageAnalysisFragment appUsageAnalysisFragment = new AppUsageAnalysisFragment();
        appUsageAnalysisFragment.setArguments(bundle);
        return appUsageAnalysisFragment;
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        if (fragmentManager.findFragmentByTag(AppUsageAnalysisFragment.TAG) != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new OnboardingRewardsFragment(), OnboardingRewardsFragment.TAG);
            fragmentTransaction.commit();

            Button nextButton = (Button) findViewById(R.id.next_button);
            nextButton.setText(R.string.start_button);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        }
    }
}
