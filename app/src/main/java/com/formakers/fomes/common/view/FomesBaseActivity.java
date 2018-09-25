package com.formakers.fomes.common.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.provisioning.view.ProvisioningPermissionFragment;
import com.formakers.fomes.provisioning.view.ProvisioningUserInfoFragment;
import com.formakers.fomes.util.FomesConstants;

import javax.inject.Inject;


public class FomesBaseActivity extends BaseActivity {
    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        int status = sharedPreferencesHelper.getProvisioningProgressStatus();

        switch (status) {
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.NOT_LOGIN:
                moveActivityTo(LoginActivity.class);
                return;
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO: {
                Intent intent = new Intent(this, ProvisioningActivity.class);
                intent.putExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME, ProvisioningUserInfoFragment.TAG);
                startActivity(intent);
                finish();
                return;
            }
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.NO_PERMISSION: {
                Intent intent = new Intent(this, ProvisioningActivity.class);
                intent.putExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME, ProvisioningPermissionFragment.TAG);
                startActivity(intent);
                finish();
                return;
            }
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
}
