package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.AppStatServiceCallback;

import javax.inject.Inject;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    AppStatService appStatService;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            sendData();
        } else {
            //TODO: 테스트 필요
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: 테스트 필요
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION) {
            if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
                sendData();
            } else {
                finish();
            }
        }
    }

    public void sendData() {
        appStatService.sendAppList(appStatServiceCallback);
        appStatService.sendLongTermStats(appStatServiceCallback);
        appStatService.sendEventStats(appStatServiceCallback);
        appStatService.sendShortTermStats(appStatServiceCallback);
    }

    AppStatServiceCallback appStatServiceCallback = new AppStatServiceCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "api call success");
        }

        @Override
        public void onFail(String resultCode) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };
}
