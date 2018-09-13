package com.formakers.fomes.analysis.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;

public class CurrentAnalysisReportActivity extends FomesBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppBeeApplication) this.getApplication()).getComponent().inject(this);
        this.setContentView(R.layout.activity_current_analysis_report);
    }
}
