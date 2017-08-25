package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.appbee.appbeemobile.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class StartActivity extends AppCompatActivity {

    private Unbinder binder;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        binder = ButterKnife.bind(this);
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

    @Override
    protected void onDestroy() {
        binder.unbind();
        super.onDestroy();
    }
}
