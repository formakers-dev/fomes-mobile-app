package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;
import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    AppStatService appStatService;

    @Inject
    AppService appService;

    @Inject
    AppRepositoryHelper appRepositoryHelper;

    @Inject
    AppUsageDataHelper appUsageDataHelper;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    private boolean isServiceAPIFailAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Glide.with(this).asGif().load(R.drawable.automated_loading_bowl).into((ImageView) findViewById(R.id.loadingImage));

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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
        public void onSuccess(List<AppInfo> appInfos) {
            MainActivity.this.runOnUiThread(() -> {
                appRepositoryHelper.insertUsedApps(appInfos);
                appRepositoryHelper.updateTotalUsedTime(appUsageDataHelper.getLongTermStatsSummary());
                moveToAnalysisResultActivity();
            });
        }

        @Override
        public void onFail(String resultCode) {
            if (!isServiceAPIFailAlready) {
                isServiceAPIFailAlready = true;
                callAppServiceGetInfoAPI();
            } else {
                MainActivity.this.runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, R.string.app_service_get_info_api_fail, Toast.LENGTH_SHORT).show();
                });
            }
        }
    };
}
