package com.formakers.fomes.provisioning.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.model.User;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.util.FomesConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnItemSelected;

public class ProvisioningUserInfoFragment extends BaseFragment implements ProvisioningActivity.FragmentCommunicator {

    public static final String TAG = ProvisioningUserInfoFragment.class.getSimpleName();

    @BindView(R.id.provision_user_info_birth_spinner)       Spinner birthSpinner;
    @BindView(R.id.provision_user_info_job_spinner)         Spinner jobSpinner;
    @BindView(R.id.provision_user_info_gender_radiogroup)   RadioGroup genderRadioGroup;
    @BindView(R.id.provision_user_info_male_radiobutton)    RadioButton maleRadioButton;
    @BindView(R.id.provision_user_info_female_radiobutton)  RadioButton femaleRadioButton;

    ProvisioningContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.presenter.setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO);
        return inflater.inflate(R.layout.fragment_provision_user_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> items = new ArrayList<>();
        items.add(getResources().getString(R.string.common_spinner_hint));
        for (User.JobCategory job : User.JobCategory.values()) {
            if (job.getSelectable())
                items.add(job.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, items);
        jobSpinner.setAdapter(adapter);
    }

    public ProvisioningUserInfoFragment setPresenter(ProvisioningContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public void onSelectedPage() {
        Log.v(TAG, "onSelectedPage");
        if (getView() != null && this.isVisible()) {
            emitFilledUpEvent();
        }
    }

    @Override
    public void onNextButtonClick() {
        View view = this.getView();
        if (view == null) {
            return;
        }

        int birth = Integer.parseInt(birthSpinner.getSelectedItem().toString());
        User.JobCategory jobCategory = User.JobCategory.get(jobSpinner.getSelectedItem().toString());
        int job = jobCategory != null ? jobCategory.getCode() : 0;
        String gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.provision_user_info_male_radiobutton ? "male" : "female";

        this.presenter.updateDemographicsToUser(birth, job, gender);
        this.presenter.emitNextPageEvent();
    }

    @OnItemSelected(R.id.provision_user_info_birth_spinner)
    public void onBirthSpinnerItemSelected(Spinner spinner, int position) {
        Log.v(TAG, "onBirthSpinnerItemSelected) " + spinner.getItemAtPosition(position));
        emitFilledUpEvent();
    }

    @OnItemSelected(R.id.provision_user_info_job_spinner)
    public void onJobSpinnerItemSeletected(Spinner spinner, int position) {
        Log.v(TAG, "onJobSpinnerItemSeletected) " + spinner.getItemAtPosition(position));
        emitFilledUpEvent();
    }

    @OnCheckedChanged({R.id.provision_user_info_male_radiobutton, R.id.provision_user_info_female_radiobutton})
    void onGenderRadioSelected(CompoundButton radioButton, boolean checked) {
        String gender = radioButton.getId() == R.id.provision_user_info_male_radiobutton ? "male" : "female";
        Log.v(TAG, "onGenderRadioSelected) " + gender + " checked=" + checked + " test=" + radioButton.isChecked());
        emitFilledUpEvent();
    }

    @SuppressLint("ResourceType")
    private void emitFilledUpEvent() {
        if (birthSpinner.getSelectedItemPosition() > 0
                && jobSpinner.getSelectedItemPosition() > 0
                && (maleRadioButton.isChecked() || femaleRadioButton.isChecked())) {
            this.presenter.emitFilledUpEvent(this, true);
        } else {
            this.presenter.emitFilledUpEvent(this, false);
        }
    }
}
