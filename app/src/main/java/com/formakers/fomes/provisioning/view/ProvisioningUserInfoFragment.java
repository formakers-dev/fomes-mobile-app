package com.formakers.fomes.provisioning.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import butterknife.BindView;

public class ProvisioningUserInfoFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    @BindView(R.id.provision_user_info_birth_spinner)       Spinner birthSpinner;
    @BindView(R.id.provision_user_info_job_spinner)         Spinner jobSpinner;
    @BindView(R.id.provision_user_info_gender_radiogroup)   RadioGroup genderRadioGroup;

    ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provision_user_info, container, false);
    }

    public ProvisioningUserInfoFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public void onNextButtonClick() {
        View view = this.getView();
        if (view == null) {
            return;
        }

        int birth = Integer.parseInt(birthSpinner.getSelectedItem().toString());
        String job = jobSpinner.getSelectedItem().toString();
        String gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.provision_user_info_male_radiobutton ? "male" : "female";

        this.presenter.updateDemographicsToUser(birth, job, gender);
        this.presenter.emitNextPageEvent();
    }
}
