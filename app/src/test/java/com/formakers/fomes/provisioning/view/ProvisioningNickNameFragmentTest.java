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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProvisioningNickNameFragmentTest {

    @Mock ProvisioningContract.Presenter mockPresenter;

    ProvisioningNickNameFragment subject;
    SupportFragmentController<ProvisioningNickNameFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new ProvisioningNickNameFragment();
        subject.setPresenter(mockPresenter);
        controller = SupportFragmentController.of(subject);

        controller.create().start().resume();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void ProvisioningNickNameFragment_시작시__프로비저닝_인생게임_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.provision_nickname_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_subtitle_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_content_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_content_edittext).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.next_button).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 다음버튼_클릭시__입력된_닉네임을_유저정보에_업데이트한다() {
        EditText nickNameEditText = subject.getView().findViewById(R.id.provision_nickname_content_edittext);
        nickNameEditText.setText("새로운닉네임");

        subject.getView().findViewById(R.id.next_button).performClick();

        verify(mockPresenter).updateNickNameToUser(eq("새로운닉네임"));
    }
}