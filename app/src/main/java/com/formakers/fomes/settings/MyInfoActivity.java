package com.formakers.fomes.settings;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.view.FomesBaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;

public class MyInfoActivity extends FomesBaseActivity implements MyInfoContract.View {

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
}
