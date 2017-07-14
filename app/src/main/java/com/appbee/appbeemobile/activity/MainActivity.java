package com.appbee.appbeemobile.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.manager.StatManager;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    StatManager mStatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        confirmAuth();
        init();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void confirmAuth() {
        boolean granted;
        AppOpsManager appOps = (AppOpsManager) getSystemService(APP_OPS_SERVICE);

        if(appOps != null) {
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
            if(mode == AppOpsManager.MODE_DEFAULT) {
                granted = checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS)== PackageManager.PERMISSION_GRANTED;
            }else {
                granted = (mode == AppOpsManager.MODE_ALLOWED);
            }
            if(!granted) {
                // 마시멜로 이상 버전에서는, 사용정보 접근 허용창에서 해당 어플리케이션의 시스템 접근을 허용해야 함
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 0);
            }
        }
    }

    public void init() {
        // TODO: 일주일동안 사용정보 가져오기
        UsageEvents usageEvents = mStatManager.getUserAppUsageInDetail();

        // TODO: 일년간 통계정보 가져오기
//        List<UsageStats> usageStatsList = mStatManager.getUserAppUsageStats();

    }

}
