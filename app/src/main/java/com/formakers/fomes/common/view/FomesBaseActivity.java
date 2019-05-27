package com.formakers.fomes.common.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.provisioning.view.ProvisioningNickNameFragment;
import com.formakers.fomes.provisioning.view.ProvisioningPermissionFragment;

import javax.inject.Inject;


public class FomesBaseActivity extends BaseActivity {
    @Inject SharedPreferencesHelper sharedPreferencesHelper;
    @Inject AndroidNativeHelper androidNativeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FomesApplication) getApplication()).getComponent().inject(this);

        if (isNeedProvisioning()) {
            finish();
            return;
        }

        // 마이그레이션 팝업이 필요해지면 FomesConstants.MIGRATION_VERSION 를 +1 하고 이 주석을 풀면 된다! => 나중에 리모트컨피그로 관리 할 수 있는 포인트 일 것 같다!
//        if (sharedPreferencesHelper.getOldLatestMigrationVersion() < FomesConstants.MIGRATION_VERSION) {
//            if (!(this instanceof RecentAnalysisReportActivity)) {
//                Intent intent = new Intent(this, NoticeMigrationActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (!androidNativeHelper.hasUsageStatsPermission()) {
            Intent intent = new Intent(this, ProvisioningActivity.class);
            intent.putExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME, ProvisioningPermissionFragment.TAG);
            startActivity(intent);
            finish();
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
                intent.putExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME, ProvisioningNickNameFragment.TAG);
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
