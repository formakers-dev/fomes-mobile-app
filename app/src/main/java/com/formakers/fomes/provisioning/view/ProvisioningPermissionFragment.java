package com.formakers.fomes.provisioning.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.formakers.fomes.R;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

public class ProvisioningPermissionFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    public static final String TAG = ProvisioningPermissionFragment.class.getSimpleName();
    static final int REQUEST_CODE_USAGE_STATS_PERMISSION = 1001;

    private Context context;

    private ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.presenter != null && this.presenter.isSelected(this)) {
            onSelectedPage();
        }

        return inflater.inflate(R.layout.fragment_provision_permission, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    public ProvisioningPermissionFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public void onNextButtonClick() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_USAGE_STATS_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_USAGE_STATS_PERMISSION) {
            onSelectedPage();
        }
    }

    @Override
    public void onSelectedPage() {
        this.presenter.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION);
        this.presenter.getAnalytics().setCurrentScreen(this);

        if (this.presenter.hasUsageStatsPermission()) {
            moveToNextPage();
        } else {
            this.presenter.emitNeedToGrantEvent();
        }
    }

    private void moveToNextPage() {
        if (!this.presenter.isProvisiongProgress()) {
            startActivity(new Intent(context, MainActivity.class));
            getActivity().finish();
        } else {
            this.presenter.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);
            this.presenter.emitStartActivityAndFinishEvent(RecentAnalysisReportActivity.class);
        }
    }
}
