package com.formakers.fomes.provisioning.view;


import android.view.View;
import android.widget.EditText;

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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProvisioningLifeGameFragmentTest {

    @Mock ProvisioningContract.Presenter mockPresenter;

    ProvisioningLifeGameFragment subject;
    SupportFragmentController<ProvisioningLifeGameFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new ProvisioningLifeGameFragment();
        subject.setPresenter(mockPresenter);
        controller = SupportFragmentController.of(subject);

        controller.create().start().resume().visible();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void ProvisioningLifeGameFragment_시작시__프로비저닝_인생게임_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.provision_life_game_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_life_game_subtitle_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_life_game_content_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_life_game_content_edittext).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void ProvisioningLifeGameFragment_가_보여질시__입력완료여부_이벤트를_보낸다() {
        subject.onSelectedPage();
        verify(this.mockPresenter).emitFilledUpEvent(eq(subject), anyBoolean());
    }

    @Test
    public void 다음버튼_클릭시__입력된_닉네임을_유저정보에_업데이트하고_다음페이지로_넘어가는_이벤트를_보낸다() {
        EditText lifegameEditText = subject.getView().findViewById(R.id.provision_life_game_content_edittext);
        lifegameEditText.setText("마비노기");

        subject.onNextButtonClick();

        verify(mockPresenter).updateLifeGameToUser(eq("마비노기"));
        verify(mockPresenter).emitNextPageEvent();
    }

    @Test
    public void 인생게임_입력시__입력완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_life_game_content_edittext)).setText("마비노기");

        verify(mockPresenter).emitFilledUpEvent(subject, true);
    }

    @Test
    public void 인생게임_미입력시__입력미완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_life_game_content_edittext)).setText("");

        verify(mockPresenter).emitFilledUpEvent(subject, false);
    }
}