package com.formakers.fomes.provisioning.view;

import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

public class ProvisioningLifeGameFragment extends BaseFragment {
    ProvisioningContract.Presenter presenter;

    public ProvisioningLifeGameFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }
}
