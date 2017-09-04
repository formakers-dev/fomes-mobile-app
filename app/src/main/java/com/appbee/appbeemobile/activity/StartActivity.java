package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.network.UserService;

import javax.inject.Inject;

import butterknife.OnClick;

public class StartActivity extends BaseActivity {

    @Inject
    UserService userService;

    @Inject
    TimeHelper timeHelper;

    @Inject
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_start);

    }

    @OnClick(R.id.start_analysis_button)
    void showPermissionAlertDialog() {
        if (appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
            startMainActivity();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_dialog_title)
                    .setMessage(R.string.permission_dialog_message)
                    .setPositiveButton(R.string.agree_button_text, (dialog, which) -> startMainActivity())
                    .setNegativeButton(R.string.cancel_button_text, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    void startMainActivity() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
