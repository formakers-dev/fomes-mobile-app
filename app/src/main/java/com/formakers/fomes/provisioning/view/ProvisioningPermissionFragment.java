package com.formakers.fomes.provisioning.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.common.FomesConstants;

public class ProvisioningPermissionFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    public static final String TAG = ProvisioningPermissionFragment.class.getSimpleName();
    static final int REQUEST_CODE_USAGE_STATS_PERMISSION = 1001;

    private Context context;

    private ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provision_permission, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.presenter != null && this.presenter.isSelected(this)) {
            onSelectedPage();
        }
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

        if (requestCode == REQUEST_CODE_USAGE_STATS_PERMISSION
                && this.presenter.hasUsageStatsPermission()) {
            moveToNextPage();
        }
    }

    @Override
    public void onSelectedPage() {
        this.presenter.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION);

        if (this.presenter.hasUsageStatsPermission()) {
            moveToNextPage();
        } else {
            this.presenter.emitNeedToGrantEvent();
        }
    }

    private void moveToNextPage() {
        Log.d(TAG, "job registered result=" + this.presenter.registerSendDataJob());

        if (!this.presenter.isProvisiongProgress()) {
            getActivity().finish();
            return;
        }

        this.presenter.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);
        this.presenter.emitStartActivityAndFinishEvent(RecentAnalysisReportActivity.class);
    }
}
