package com.formakers.fomes.common.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.provisioning.view.ProvisioningPermissionFragment;
import com.formakers.fomes.provisioning.view.ProvisioningUserInfoFragment;
import com.formakers.fomes.util.FomesConstants;

import javax.inject.Inject;


public class FomesBaseActivity extends BaseActivity {
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject
    AndroidNativeHelper androidNativeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FomesApplication) getApplication()).getComponent().inject(this);

        if (isNeedProvisioning()) {
            finish();
            return;
        }

        if (!androidNativeHelper.hasUsageStatsPermission()) {
            Intent intent = new Intent(this, ProvisioningActivity.class);
            intent.putExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME, ProvisioningPermissionFragment.TAG);
            startActivity(intent);
            return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean isNeedProvisioning() {
        int status = sharedPreferencesHelper.getProvisioningProgressStatus();

        if (status < 0) {
            return false;
        }

        switch (status) {
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN: {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO: {
                Intent intent = new Intent(this, ProvisioningActivity.class);
                intent.putExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME, ProvisioningUserInfoFragment.TAG);
                startActivity(intent);
                break;
            }
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION: {
                Intent intent = new Intent(this, ProvisioningActivity.class);
                intent.putExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME, ProvisioningPermissionFragment.TAG);
                startActivity(intent);
                break;
            }
        }

        return true;
    }
}
