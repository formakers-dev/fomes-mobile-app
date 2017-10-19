package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.network.UserService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class PermissionGuideActivity extends BaseActivity {

    static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    UserService userService;

    @Inject
    TimeHelper timeHelper;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @BindView(R.id.permission_button)
    Button permissionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_permission_guide);

    }

    @OnClick(R.id.permission_button)
    void showPermissionAlertDialog() {
        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            startLoadingActivity();
        } else {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION);
        }
    }

    void startLoadingActivity() {
        Intent intent = new Intent(PermissionGuideActivity.this, LoadingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION) {
            if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
                startLoadingActivity();
            } else {
                // TODO : UX 검토
                finish();
            }
        }
    }

}
