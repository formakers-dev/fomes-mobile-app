package com.formakers.fomes.provisioning.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import butterknife.BindView;

public class ProvisioningLifeGameFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    @BindView(R.id.provision_life_game_content_edittext) EditText lifeGameEditText;

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

    @Override
    public void onNextButtonClick() {
        this.presenter.updateLifeGameToUser(lifeGameEditText.getText().toString());
        this.presenter.emitNextPageEvent();
    }
}
