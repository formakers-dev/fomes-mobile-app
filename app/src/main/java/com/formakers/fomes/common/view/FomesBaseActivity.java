package com.formakers.fomes.common.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.appbee.custom.AppBeeAlertDialog;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.provisioning.view.ProvisioningPermissionFragment;
import com.formakers.fomes.provisioning.view.ProvisioningUserInfoFragment;
import com.formakers.fomes.util.FomesConstants;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;


public class FomesBaseActivity extends BaseActivity {
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject AppBeeAndroidNativeHelper appBeeAndroidNativeHelper;
    @Inject ConfigService configService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        checkUpdatePopup();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (isNeedProvisioning()) {
            finish();
            return;
        }

        if (!appBeeAndroidNativeHelper.hasUsageStatsPermission()) {
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

    private void checkUpdatePopup() {
        configService.getAppVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(version -> {
                    if (BuildConfig.VERSION_CODE < version) {
                        displayVersionUpdateDialog();
                    }
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    private void displayVersionUpdateDialog() {
        AppBeeAlertDialog appBeeAlertDialog = new AppBeeAlertDialog(this, getString(R.string.update_dialog_title),
                getString(R.string.update_dialog_message), (dialog, which) -> {
            moveToPlayStore();
            dialog.dismiss();
        });
        appBeeAlertDialog.setOnCancelListener(dialog -> {
//            finishAffinity();
            dialog.dismiss();
        });
        appBeeAlertDialog.show();
    }

    private void moveToPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.formakers.fomes"));
        startActivity(intent);
//        finishAffinity();
    }

}
