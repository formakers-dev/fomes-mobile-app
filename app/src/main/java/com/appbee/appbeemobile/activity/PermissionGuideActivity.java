package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.service.PowerConnectedService;

import javax.inject.Inject;

import butterknife.OnClick;

public class PermissionGuideActivity extends BaseActivity {

    static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_permission_guide);

        // TODO : 초기상태 - 인증완료 상태 패턴 적용하여 관리하는 방안 고려
        if (TextUtils.isEmpty(localStorageHelper.getInvitationCode())) {
            moveActivityTo(CodeVerificationActivity.class);
            return;
        }

        if (TextUtils.isEmpty(localStorageHelper.getEmail())) {
            moveActivityTo(LoginActivity.class);
            return;
        }

        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            startPowerConnectedService();
            moveActivityTo(MainActivity.class);
        }
    }

    @OnClick(R.id.permission_button)
    void onPermissionButtonClick() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION);
    }

    private void startPowerConnectedService() {
        startService(new Intent(this, PowerConnectedService.class));
    }

    private void moveActivityTo(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION
                && appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            startPowerConnectedService();
            moveActivityTo(LoadingActivity.class);
        }
    }
}
