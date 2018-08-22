package com.formakers.fomes.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.AppUsageAnalysisFragment;

public class MyAppUsageActivity extends BaseActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_app_usage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, createAppUsageAnalysisFragment(), AppUsageAnalysisFragment.TAG);
        fragmentTransaction.commit();
    }

    @NonNull
    private AppUsageAnalysisFragment createAppUsageAnalysisFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(AppUsageAnalysisFragment.EXTRA_DESCRIPTION_RES_ID, R.string.my_analysis_description);

        AppUsageAnalysisFragment appUsageAnalysisFragment = new AppUsageAnalysisFragment();
        appUsageAnalysisFragment.setArguments(bundle);
        return appUsageAnalysisFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
