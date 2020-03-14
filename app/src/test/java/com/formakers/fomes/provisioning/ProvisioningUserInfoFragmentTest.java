package com.formakers.fomes.provisioning;


import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;

import rx.Completable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ProvisioningUserInfoFragmentTest {

    @Mock ProvisioningContract.Presenter mockPresenter;

    ProvisioningUserInfoFragment subject;
    FragmentScenario<ProvisioningUserInfoFragment> scenario;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockPresenter.getAnalytics()).thenReturn(mock(AnalyticsModule.Analytics.class));

        scenario = FragmentScenario.launchInContainer(ProvisioningUserInfoFragment.class);
        scenario.onFragment(fragment -> {
            subject = fragment;
            fragment.setPresenter(mockPresenter);
        });

        scenario.moveToState(Lifecycle.State.CREATED)
                .moveToState(Lifecycle.State.STARTED)
                .moveToState(Lifecycle.State.RESUMED);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void ProvisioningUserInfoFragmentTest_시작시__프로비저닝_데모그래픽정보입력_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.provision_life_game_content_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_life_game_content_edittext).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_birth_spinner).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_content_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_birth_spinner).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_job_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_job_spinner).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_gender_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_gender_radiogroup).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_male_radiobutton).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_user_info_female_radiobutton).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void ProvisioningUserInfoFragment_시작시__프로비저닝_진행상황을_업데이트한다() {
        subject.onCreateView(subject.getLayoutInflater(), null, null);
        verify(mockPresenter).setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO);
    }

    @Test
    public void ProvisioningUserInfoFragment_가_보여질시__입력완료여부_이벤트를_보낸다() {
        subject.onSelectedPage();
        verify(this.mockPresenter, atLeast(1)).emitFilledUpEvent(eq(subject), anyBoolean());
    }

    @Test
    public void 다음버튼_클릭시__입력된_데모그래픽_정보를_유저정보에_업데이트하고_서버에_업데이트_요청을_한다() {
        setValuesToView("인생게임", 1, 3, R.id.provision_user_info_female_radiobutton);

        when(mockPresenter.requestToUpdateUserInfo()).thenReturn(Completable.complete());

        subject.onNextButtonClick();

        verify(mockPresenter).setUserInfo(eq("인생게임"), eq(2015), eq(2001), eq(User.GENDER_FEMALE));
        verify(mockPresenter).requestToUpdateUserInfo();
    }

    @Test
    public void 서버에_업데이트_요청_성공시__프로비저닝_플로우를_업데이트한_후_다음페이지로_넘어가는_이벤트를_보낸다() {
        setValuesToView("인생게임", 1, 3, R.id.provision_user_info_female_radiobutton);

        when(mockPresenter.requestToUpdateUserInfo()).thenReturn(Completable.complete());

        subject.onNextButtonClick();

        verify(mockPresenter).requestToUpdateUserInfo();
        verify(mockPresenter).emitNextPageEvent();
    }

    @Test
    public void 다음버튼_클릭시__서버에_업데이트_요청을_하고_실패시__실패문구를_띄운다() {
        setValuesToView("인생게임", 1, 3, R.id.provision_user_info_female_radiobutton);

        when(mockPresenter.requestToUpdateUserInfo()).thenReturn(Completable.error(new Throwable()));

        subject.onNextButtonClick();

        verify(mockPresenter).requestToUpdateUserInfo();
        assertThat(ShadowToast.getTextOfLatestToast()).contains("재시도");
    }

    @Test
    public void 항목_전체_입력시__입력완료_이벤트를_보낸다() {
        setValuesToView("인생게임", 1, 3, R.id.provision_user_info_female_radiobutton);

        verify(mockPresenter).emitFilledUpEvent(subject, true);
    }

    @Test
    public void 항목_미입력시__입력미완료_이벤트를_보낸다() {
        // 전체 미임력시
        setValuesToView("", 0, 0, 0);
        verify(mockPresenter, atLeast(3)).emitFilledUpEvent(subject, false);

        // 일부 미임력시
        setValuesToView("", 1, 0, 0);
        verify(mockPresenter, atLeast(3)).emitFilledUpEvent(subject, false);

        setValuesToView("", 0, 1, 0);
        verify(mockPresenter, atLeast(3)).emitFilledUpEvent(subject, false);

        setValuesToView("", 0, 0, R.id.provision_user_info_female_radiobutton);
        verify(mockPresenter, atLeast(4)).emitFilledUpEvent(subject, false);

        setValuesToView("", 0, 0, 0);
        verify(mockPresenter, atLeast(3)).emitFilledUpEvent(subject, false);
    }

    private void setValuesToView(String lifeGame, int birthPosition, int jobPosition, int genderRadioButtonId) {
        EditText lifeGameEditText = subject.getView().findViewById(R.id.provision_life_game_content_edittext);
        Spinner birthSpinner = subject.getView().findViewById(R.id.provision_user_info_birth_spinner);
        Spinner jobSpinner = subject.getView().findViewById(R.id.provision_user_info_job_spinner);
        RadioGroup genderRadioGroup = subject.getView().findViewById(R.id.provision_user_info_gender_radiogroup);

        lifeGameEditText.setText("인생게임");
        birthSpinner.setSelection(1);
        jobSpinner.setSelection(3);

        if (genderRadioButtonId != 0) {
            genderRadioGroup.check(R.id.provision_user_info_female_radiobutton);
        } else {
            genderRadioGroup.clearCheck();
        }
    }
}