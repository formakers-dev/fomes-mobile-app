package com.formakers.fomes.provisioning.view;


import android.view.View;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProvisioningUserInfoFragmentTest {

    @Mock ProvisioningContract.Presenter mockPresenter;

    ProvisioningUserInfoFragment subject;
    SupportFragmentController<ProvisioningUserInfoFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new ProvisioningUserInfoFragment();
        subject.setPresenter(mockPresenter);
        controller = SupportFragmentController.of(subject);

        controller.create().start().resume();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void ProvisioningUserInfoFragmentTest_시작시__프로비저닝_데모그래픽정보입력_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.provision_user_info_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_subtitle_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_content_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_birth_spinner).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_job_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_job_spinner).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_gender_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_gender_radiogroup).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_male_radiobutton).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_female_radiobutton).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.next_button).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 다음버튼_클릭시__입력된_데모그래픽_정보를_유저정보에_업데이트한다() {
        Spinner birthSpinner = subject.getView().findViewById(R.id.provision_user_info_birth_spinner);
        Spinner jobSpinner = subject.getView().findViewById(R.id.provision_user_info_job_spinner);
        RadioGroup genderRadioGroup = subject.getView().findViewById(R.id.provision_user_info_gender_radiogroup);

        birthSpinner.setSelection(1);
        jobSpinner.setSelection(1);
        genderRadioGroup.check(R.id.provision_user_info_female_radiobutton);

        subject.getView().findViewById(R.id.next_button).performClick();

        verify(mockPresenter).updateDemographicsToUser(eq(1900), eq("중고등학생"), eq("female"));
    }
}