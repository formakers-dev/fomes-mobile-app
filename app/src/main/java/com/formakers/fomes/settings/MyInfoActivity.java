package com.formakers.fomes.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class MyInfoActivity extends FomesBaseActivity implements MyInfoContract.View {

    private static final String TAG = "MyInfoActivity";

    @BindView(R.id.my_info_life_game_content_edittext) EditText lifeGameEditText;
    @BindView(R.id.my_info_birth_spinner) Spinner birthSpinner;
    @BindView(R.id.my_info_job_spinner) Spinner jobSpinner;
    @BindView(R.id.my_info_gender_radiogroup) RadioGroup genderRadioGroup;
    @BindView(R.id.my_info_male_radiobutton) RadioButton maleRadioButton;
    @BindView(R.id.my_info_female_radiobutton) RadioButton femaleRadioButton;
    @BindView(R.id.my_info_submit_button) Button submitButton;

    @Inject MyInfoContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerMyInfoDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new MyInfoDagger.Module(this))
                .build()
                .inject(this);

        getSupportActionBar().setTitle(R.string.main_menu_my_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setContentView(R.layout.activity_my_info);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // TODO : 스피너 커스텀 예정
//        SpinnerSimpleTextAdapter birthSpinnerAdapter = new SpinnerSimpleTextAdapter(this, R.layout.item_provision_spinner, Arrays.asList(this.getResources().getStringArray(R.array.birth_items)));
//        birthSpinnerAdapter.setHint(R.string.birth_spinner_hint);
//        birthSpinner.setAdapter(birthSpinnerAdapter);
//        birthSpinner.setSelection(birthSpinnerAdapter.getHintPosition());

        ArrayList<String> items = new ArrayList<>();
        items.add(getResources().getString(R.string.job_spinner_hint));
        for (User.JobCategory job : User.JobCategory.values()) {
            if (job.getSelectable())
                items.add(job.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        jobSpinner.setAdapter(adapter);

        submitButton.setVisibility(View.GONE);
        this.presenter.loadUserInfo();
    }

    @Override
    public void bind(User userInfo) {
        lifeGameEditText.setText(userInfo.getLifeApps() != null && userInfo.getLifeApps().size() > 0 ? userInfo.getLifeApps().get(0) : "");
        birthSpinner.setSelection(Arrays.asList(this.getResources().getStringArray(R.array.birth_items)).indexOf(String.valueOf(userInfo.getBirthday())));
        jobSpinner.setSelection(((ArrayAdapter<String>) jobSpinner.getAdapter()).getPosition(User.JobCategory.get(userInfo.getJob()).getName()));

        if (User.GENDER_MALE.equals(userInfo.getGender())) {
            maleRadioButton.toggle();
        } else {
            femaleRadioButton.toggle();
        }
    }

    @Override
    public void setPresenter(MyInfoContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @OnClick(R.id.my_info_submit_button)
    public void onSubmitButtonClicked() {
        String lifeApp = lifeGameEditText.getText().toString();
        int birth = Integer.parseInt(birthSpinner.getSelectedItem().toString());
        User.JobCategory jobCategory = User.JobCategory.get(jobSpinner.getSelectedItem().toString());
        int job = jobCategory != null ? jobCategory.getCode() : 0;
        String gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.my_info_male_radiobutton ? User.GENDER_MALE : User.GENDER_FEMALE;

        this.presenter.updateUserInfo(birth, job, gender, lifeApp);
    }

    @OnTextChanged(value = R.id.my_info_life_game_content_edittext, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onLifeGameTextChanged(CharSequence text, int start, int before, int count) {
        Log.v(TAG, "onLifeGameTextChanged) " + text + " start=" + start + ", before=" + before + ", count=" + count);
        emitFilledUpEvent();
    }

    @OnItemSelected(R.id.my_info_birth_spinner)
    public void onBirthSpinnerItemSelected(Spinner spinner, int position) {
        Log.v(TAG, "onBirthSpinnerItemSelected) " + spinner.getItemAtPosition(position));
        emitFilledUpEvent();
    }

    @OnItemSelected(R.id.my_info_job_spinner)
    public void onJobSpinnerItemSeletected(Spinner spinner, int position) {
        Log.v(TAG, "onJobSpinnerItemSeletected) " + spinner.getItemAtPosition(position));
        emitFilledUpEvent();
    }

    @OnCheckedChanged({R.id.my_info_male_radiobutton, R.id.my_info_female_radiobutton})
    void onGenderRadioSelected(CompoundButton radioButton, boolean checked) {
        String gender = radioButton.getId() == R.id.my_info_male_radiobutton ? User.GENDER_MALE : User.GENDER_FEMALE;
        Log.v(TAG, "onGenderRadioSelected) " + gender + " checked=" + checked + " test=" + radioButton.isChecked());
        if (checked) {
            emitFilledUpEvent(radioButton.getId());
        }
    }

    @SuppressLint("ResourceType")
    private void emitFilledUpEvent() {
        this.emitFilledUpEvent(genderRadioGroup.getCheckedRadioButtonId());
    }

    @SuppressLint("ResourceType")
    private void emitFilledUpEvent(@IdRes int checkedRadioButtonId) {
        if (this.presenter == null) {
            return;
        }

        if (this.verifyEnableToSubmit(checkedRadioButtonId)) {
            submitButton.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.GONE);
        }
    }

    private boolean verifyEnableToSubmit(@IdRes int checkedRadioButtonId) {
        String lifeApp = lifeGameEditText.getText().toString();

        if (lifeApp.length() > 0
                && birthSpinner.getSelectedItemPosition() > 0
                && jobSpinner.getSelectedItemPosition() > 0
                && (maleRadioButton.isChecked() || femaleRadioButton.isChecked())) {

            int birth = Integer.parseInt(birthSpinner.getSelectedItem().toString());
            User.JobCategory jobCategory = User.JobCategory.get(jobSpinner.getSelectedItem().toString());
            int job = jobCategory != null ? jobCategory.getCode() : 0;
            String gender = checkedRadioButtonId == R.id.my_info_male_radiobutton ? User.GENDER_MALE : User.GENDER_FEMALE;
            Log.d(TAG, "gender=" + gender);

            if (this.presenter.isUpdated(birth, job, gender, lifeApp)) {
                return true;
            }
        }

        return false;
    }
}