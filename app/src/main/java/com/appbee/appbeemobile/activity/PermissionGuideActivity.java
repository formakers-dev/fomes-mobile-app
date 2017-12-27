package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.custom.AppBeeAlertDialog;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.ConfigService;
import com.appbee.appbeemobile.util.AppBeeConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class PermissionGuideActivity extends BaseActivity {

    private static final String TAG = PermissionGuideActivity.class.getSimpleName();
    static final int REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION = 1001;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    ConfigService configService;

    @BindView(R.id.permission_button)
    Button permissionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_permission_guide);

        if (BuildConfig.VERSION_CODE < configService.getAppVersion()) {
            displayVersionUpdateDialog();
            return;
        }

        // TODO : 초기상태 - 인증완료 상태 패턴 적용하여 관리하는 방안 고려
        if (TextUtils.isEmpty(localStorageHelper.getInvitationCode())) {
            moveActivityTo(CodeVerificationActivity.class);
            return;
        }

        if (!localStorageHelper.isLoggedIn()) {
            moveActivityTo(LoginActivity.class);
            return;
        }

        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            String notification = getIntent().getStringExtra(AppBeeConstants.EXTRA.NOTIFICATION_TYPE);
            if ("확정".equals(notification)) {
                moveActivityTo(MyInterviewActivity.class);
            } else {
                moveActivityTo(MainActivity.class);
            }
        }
    }

    private void displayVersionUpdateDialog() {
        AppBeeAlertDialog appBeeAlertDialog = new AppBeeAlertDialog(this, getString(R.string.app_update_dialog_title), getString(R.string.app_update_dialog_message), (dialog, which) -> moveToPlayStore());
        appBeeAlertDialog.setOnCancelListener(dialog -> finishAffinity());
        appBeeAlertDialog.show();
    }

    private void moveToPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.appbee.appbeemobile"));
        startActivity(intent);
        finishAffinity();
    }

    @OnClick(R.id.permission_button)
    void onPermissionButtonClick() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS_PERMISSION
                && appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            moveActivityTo(LoadingActivity.class);
        }
    }
}
