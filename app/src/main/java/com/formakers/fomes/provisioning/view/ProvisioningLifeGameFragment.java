package com.formakers.fomes.provisioning.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

public class ProvisioningLifeGameFragment extends BaseFragment {
    ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provision_life_game, container, false);
    }

    public ProvisioningLifeGameFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }
}
