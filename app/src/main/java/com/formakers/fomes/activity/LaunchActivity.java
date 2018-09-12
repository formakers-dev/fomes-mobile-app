package com.formakers.fomes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.provisioning.view.CurrentAnalysisReportActivity;

import butterknife.OnClick;

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);
    }

    @OnClick(R.id.launch_appbee_button)
    public void onAppBeeClick() {
        Intent intent = new Intent(this, PermissionGuideActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.launch_fomes_button)
    public void onFomesClick() {
        Intent intent = new Intent(this, CurrentAnalysisReportActivity.class);
        startActivity(intent);
    }
}
