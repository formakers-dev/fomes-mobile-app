package com.formakers.fomes.common.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.formakers.fomes.activity.BaseActivity;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.util.FomesConstants;

import javax.inject.Inject;


public abstract class FomesBaseActivity extends BaseActivity {
    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        int status = sharedPreferencesHelper.getProvisioningProgressStatus();

        switch (status) {
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.NOT_LOGIN:
                moveActivityTo(LoginActivity.class);
                return;
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO:
                moveActivityTo(ProvisioningActivity.class);
                return;
            case FomesConstants.PROVISIONING.PROGRESS_STATUS.NO_PERMISSION:
                moveActivityTo(ProvisioningActivity.class);
                return;
        }
    }
}
