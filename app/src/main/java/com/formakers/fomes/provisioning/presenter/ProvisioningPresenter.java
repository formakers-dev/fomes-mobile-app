package com.formakers.fomes.provisioning.presenter;

import com.formakers.fomes.provisioning.contract.ProvisioningContract;

public class ProvisioningPresenter implements ProvisioningContract.Presenter {

    private ProvisioningContract.View view;

    public ProvisioningPresenter(ProvisioningContract.View view) {
        this.view = view;
    }

    @Override
    public void onNextPageEvent() {
        this.view.nextPage();
    }
}
