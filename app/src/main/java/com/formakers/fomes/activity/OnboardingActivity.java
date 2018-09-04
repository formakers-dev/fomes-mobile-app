package com.formakers.fomes.activity;

import android.content.Intent;
import android.os.Bundle;

import com.formakers.fomes.R;

import butterknife.OnClick;

public class OnboardingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
    }

    @OnClick(R.id.next_button)
    void onNextButtonClick() {
        Intent intent = new Intent(this, PermissionGuideActivity.class);
        startActivity(intent);
        finish();
    }
}