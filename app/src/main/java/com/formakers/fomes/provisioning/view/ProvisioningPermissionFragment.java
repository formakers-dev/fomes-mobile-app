package com.formakers.fomes.provisioning.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

public class ProvisioningPermissionFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    public static final String TAG = ProvisioningPermissionFragment.class.getSimpleName();
    static final int REQUEST_CODE_USAGE_STATS_PERMISSION = 1001;

    private ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provision_permission, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            this.presenter.emitGrantedEvent(true);
        }
    }

    @Override
    public void onSelectedPage() {
        this.presenter.emitAlmostCompletedEvent(true);
        this.presenter.emitGrantedEvent(this.presenter.hasUsageStatsPermission());
    }
}
