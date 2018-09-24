package com.formakers.fomes.appbee.activity;

import android.content.Intent;
import android.os.Bundle;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseActivity;

import butterknife.OnClick;

@Deprecated
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
