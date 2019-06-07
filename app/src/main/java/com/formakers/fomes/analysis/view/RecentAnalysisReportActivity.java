package com.formakers.fomes.analysis.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class RecentAnalysisReportActivity extends FomesBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_current_analysis_report);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.report_container, new RecentAnalysisReportFragment());
        ft.commit();
    }
}
