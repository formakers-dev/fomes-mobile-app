package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appbee.appbeemobile.R;

import butterknife.OnClick;

public class CancelInterviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cancel_interview);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @OnClick(R.id.back_button)
    void onBackButtonClick() {
        super.onBackPressed();
    }
}