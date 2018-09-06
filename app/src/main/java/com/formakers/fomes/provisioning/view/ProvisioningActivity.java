package com.formakers.fomes.provisioning.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.activity.BaseActivity;

public class ProvisioningActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_provisioning);
    }
}
