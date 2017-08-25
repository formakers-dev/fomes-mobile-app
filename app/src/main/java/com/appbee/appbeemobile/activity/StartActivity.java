package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.R;

import butterknife.OnClick;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
    }

    @OnClick(R.id.start_analysis_button)
    void showPermissionAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_dialog_title)
                .setMessage(R.string.permission_dialog_message)
                .setPositiveButton(R.string.agree_button_text, (dialog, which) -> startMainActivity())
                .setNegativeButton(R.string.cancel_button_text, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    void startMainActivity() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
