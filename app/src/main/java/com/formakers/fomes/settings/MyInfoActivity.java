package com.formakers.fomes.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.Group;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.custom.MultiSelectionSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class MyInfoActivity extends FomesBaseActivity implements MyInfoContract.View {

    private static final String TAG = "MyInfoActivity";

    @BindView(R.id.my_info_nickname_content_group) Group nickNameContentGroup;
    @BindView(R.id.my_info_nickname_content_edittext) EditText nickNameEditText;
    @BindView(R.id.my_info_nickname_format_warning_textview) TextView nickNameWarningTextView;
    @BindView(R.id.my_info_life_game_content_edittext) EditText lifeGameEditText;
    @BindView(R.id.my_info_birth_spinner) Spinner birthSpinner;
    @BindView(R.id.my_info_job_spinner) Spinner jobSpinner;
    @BindView(R.id.my_info_gender_radiogroup) RadioGroup genderRadioGroup;
    @BindView(R.id.my_info_male_radiobutton) RadioButton maleRadioButton;
    @BindView(R.id.my_info_female_radiobutton) RadioButton femaleRadioButton;
    @BindView(R.id.my_info_monthly_payment_content_edittext) EditText monthlyPaymentEditText;
    @BindView(R.id.my_info_favorite_genre_spinner) MultiSelectionSpinner favoriteGenreSpinner;
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

        ArrayList<String> jobItems = new ArrayList<>();
        jobItems.add(getResources().getString(R.string.job_spinner_hint));
        for (User.JobCategory job : User.JobCategory.values()) {
            if (job.getSelectable())
                jobItems.add(job.getName());
        }
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, jobItems);
        jobSpinner.setAdapter(jobAdapter);

        ArrayList<String> genreItems = new ArrayList<>();
        for (User.GenreCategory genre : User.GenreCategory.values()) {
            genreItems.add(genre.getName());
        }
        favoriteGenreSpinner.setItems(genreItems);

        submitButton.setEnabled(false);
        this.presenter.loadUserInfo();
    }

    @Override
    public void bind(User userInfo) {
        nickNameEditText.setText(userInfo.getNickName());
        nickNameContentGroup.setVisibility((TextUtils.isEmpty(userInfo.getNickName()))? View.VISIBLE : View.GONE);

        lifeGameEditText.setText(userInfo.getLifeApps() != null && userInfo.getLifeApps().size() > 0 ? userInfo.getLifeApps().get(0) : "");
        birthSpinner.setSelection(Arrays.asList(this.getResources().getStringArray(R.array.birth_items)).indexOf(String.valueOf(userInfo.getBirthday())));
        monthlyPaymentEditText.setText(userInfo.getMonthlyPayment());

        if (userInfo.getJob() != null) {
            jobSpinner.setSelection(((ArrayAdapter<String>) jobSpinner.getAdapter()).getPosition(User.JobCategory.get(userInfo.getJob()).getName()));
        }

        if (User.GENDER_MALE.equals(userInfo.getGender())) {
            maleRadioButton.toggle();
        } else {
            femaleRadioButton.toggle();
        }

        if (userInfo.getFavoriteGenres() != null && userInfo.getFavoriteGenres().size() > 0) {
            List<String> favoriteGenreNames = Stream.of(userInfo.getFavoriteGenres())
                    .map((genreCode) -> User.GenreCategory.get(genreCode).getName())
                    .collect(Collectors.toList());
            favoriteGenreSpinner.setSelections(favoriteGenreNames);
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(MyInfoContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @OnClick(R.id.my_info_submit_button)
    public void onSubmitButtonClicked() {
        String nickName = nickNameEditText.getText().toString();
        String lifeApp = lifeGameEditText.getText().toString();
        int birth = Integer.parseInt(birthSpinner.getSelectedItem().toString());
        User.JobCategory jobCategory = User.JobCategory.get(jobSpinner.getSelectedItem().toString());
        int job = jobCategory != null ? jobCategory.getCode() : 0;
        String gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.my_info_male_radiobutton ? User.GENDER_MALE : User.GENDER_FEMALE;
        String monthlyPayment = monthlyPaymentEditText.getText().toString();

        List<String> favoriteGenres = Stream.of(favoriteGenreSpinner.getSelectedItems())
                .map((genreName) -> User.GenreCategory.getByName(genreName).getCode())
                .collect(Collectors.toList());

        User filledUserInfo = new User().setNickName(nickName)
                .setBirthday(birth)
                .setJob(job)
                .setGender(gender)
                .setLifeApps(Collections.singletonList(lifeApp))
                .setMonthlyPayment(monthlyPayment)
                .setFavoriteGenres(favoriteGenres);

        this.presenter.updateUserInfo(filledUserInfo);
    }

    @OnTextChanged(value = R.id.my_info_nickname_content_edittext, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onNickNameTextChanged(CharSequence text, int start, int before, int count) {
        Log.v(TAG, "onNickNameTextChanged) " + text + " start=" + start + ", before=" + before + ", count=" + count);

        boolean isWrong = Pattern.matches(FomesConstants.PROVISIONING.NICK_NAME_REGEX.WRONG, text);

        if (TextUtils.isEmpty(text)) {
            setVisibilityNickNameWarningView(false, R.string.provision_nickname_format_warning);
        } else if (isWrong) {
            setVisibilityNickNameWarningView(true, R.string.provision_nickname_format_special_string_warning);
        } else {
            boolean isMatched = Pattern.matches(FomesConstants.PROVISIONING.NICK_NAME_REGEX.CORRECT, text);
            setVisibilityNickNameWarningView(!isMatched, R.string.provision_nickname_format_warning);
        }

        emitFilledUpEvent();
    }

    private void setVisibilityNickNameWarningView(boolean isVisible, @StringRes int stringResId) {
        if (isVisible) {
            nickNameWarningTextView.setText(getString(stringResId));
            nickNameWarningTextView.setVisibility(View.VISIBLE);
        } else {
            nickNameWarningTextView.setVisibility(View.GONE);
        }
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

    @OnTextChanged(value = R.id.my_info_monthly_payment_content_edittext, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onMonthlyPaymentTextChanged(CharSequence text, int start, int before, int count) {
        Log.v(TAG, "onLifeGameTextChanged) " + text + " start=" + start + ", before=" + before + ", count=" + count);
        emitFilledUpEvent();
    }

    @OnItemSelected(R.id.my_info_favorite_genre_spinner)
    public void onFavoriteGenreSpinnerItemSelected(Spinner spinner, int position) {
        Log.v(TAG, "onFavoriteGenreSpinnerItemSelected) " + spinner.getItemAtPosition(position));
        emitFilledUpEvent();
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
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void showDuplicatedNickNameWarning() {
        setVisibilityNickNameWarningView(true, R.string.provision_nickname_already_exist_warning);
    }

    private boolean verifyEnableToSubmit(@IdRes int checkedRadioButtonId) {
        String nickName = nickNameEditText.getText().toString();
        String lifeApp = lifeGameEditText.getText().toString();
        String monthlyPayment = monthlyPaymentEditText.getText().toString();
        List<String> selectedFavoriteGenres = favoriteGenreSpinner.getSelectedItems();

        if (nickName.length() > 0
                && nickNameWarningTextView.getVisibility() != View.VISIBLE
                && lifeApp.length() > 0
                && birthSpinner.getSelectedItemPosition() > 0
                && jobSpinner.getSelectedItemPosition() > 0
                && (maleRadioButton.isChecked() || femaleRadioButton.isChecked())
                && monthlyPayment.length() > 0
                && selectedFavoriteGenres.size() > 0) {

            int birth = Integer.parseInt(birthSpinner.getSelectedItem().toString());
            User.JobCategory jobCategory = User.JobCategory.get(jobSpinner.getSelectedItem().toString());
            int job = jobCategory != null ? jobCategory.getCode() : 0;
            String gender = checkedRadioButtonId == R.id.my_info_male_radiobutton ? User.GENDER_MALE : User.GENDER_FEMALE;

            List<String> favoriteGenres = Stream.of(selectedFavoriteGenres)
                    .map((genreName) -> User.GenreCategory.getByName(genreName).getCode())
                    .collect(Collectors.toList());

            User filledUserInfo = new User().setNickName(nickName)
                    .setBirthday(birth)
                    .setJob(job)
                    .setGender(gender)
                    .setLifeApps(Collections.singletonList(lifeApp))
                    .setMonthlyPayment(monthlyPayment)
                    .setFavoriteGenres(favoriteGenres);

            if (this.presenter.isUpdated(filledUserInfo)) {
                return true;
            }
        }

        return false;
    }
}
