package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            startActivity(new Intent(this, AnalysisResultActivity.class));
            finish();
        } else {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION) {
            if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
                startActivity(new Intent(this, AnalysisResultActivity.class));
            }
            finish();
        }
    }
}
