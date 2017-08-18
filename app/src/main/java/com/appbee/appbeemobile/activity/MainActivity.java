package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    AppStatService appStatService;

    @Inject
    AppService appService;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            callAppServiceGetInfoAPI();
        } else {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION);
        }
    }

    private void callAppServiceGetInfoAPI() {
        appService.getInfos(appStatService.getUsedPackageNameList(), appInfosServiceCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION) {
            if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
                callAppServiceGetInfoAPI();
            } else {
                finish();
            }
        }
    }

    private void moveToAnalysisResultActivity() {
        startActivity(new Intent(MainActivity.this, AnalysisResultActivity.class));
        finish();
    }

    AppService.AppInfosServiceCallback appInfosServiceCallback = new AppService.AppInfosServiceCallback() {
        @Override
        public void onSuccess(List<AppInfo> result) {
            moveToAnalysisResultActivity();
        }

        @Override
        public void onFail(String resultCode) {
            MainActivity.this.runOnUiThread(() ->
                Toast.makeText(MainActivity.this, R.string.app_service_get_info_api_fail, Toast.LENGTH_SHORT).show());
        }
    };
}
