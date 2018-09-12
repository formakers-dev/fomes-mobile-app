package com.formakers.fomes.provisioning.view;

import android.view.View;
import android.widget.EditText;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;
import com.formakers.fomes.util.FomesConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import rx.Completable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        controller.create().start().resume().visible();
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
    }

    @Test
    public void ProvisioningNickNameFragment_가_보여질시__입력완료여부_이벤트를_보낸다() {
        subject.onSelectedPage();
        verify(this.mockPresenter).emitFilledUpEvent(anyBoolean());
    }

    @Test
    public void 다음버튼_클릭시__입력된_닉네임을_유저정보에_업데이트하고_서버에_업데이트_요청을_한다() {
        EditText nickNameEditText = subject.getView().findViewById(R.id.provision_nickname_content_edittext);
        nickNameEditText.setText("새로운닉네임");

        when(mockPresenter.requestUpdateUser()).thenReturn(Completable.complete());

        subject.onNextButtonClick();

        verify(mockPresenter).updateNickNameToUser(eq("새로운닉네임"));
        verify(mockPresenter).requestUpdateUser();
    }

    @Test
    public void 서버에_업데이트_요청_성공시__프로비저닝_플로우를_업데이트한_후_다음페이지로_넘어가는_이벤트를_보낸다() {
        when(mockPresenter.requestUpdateUser()).thenReturn(Completable.complete());

        subject.onNextButtonClick();

        verify(mockPresenter).requestUpdateUser();
        verify(mockPresenter).setProvisioningProgressStatus(eq(FomesConstants.PROVISIONING.PROGRESS_STATUS.NO_PERMISSION));
        verify(mockPresenter).emitNextPageEvent();
    }

    @Test
    public void 다음버튼_클릭시__서버에_업데이트_요청을_하고_실패시_실패문구를_띄운다() {
        when(mockPresenter.requestUpdateUser()).thenReturn(Completable.error(new Throwable()));

        subject.onNextButtonClick();

        verify(mockPresenter).requestUpdateUser();
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("유저 정보 업데이트를 실패하였습니다.");
    }


    @Test
    public void 닉네임_입력시__입력완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_nickname_content_edittext)).setText("예나르");

        verify(mockPresenter).emitFilledUpEvent(true);
    }

    @Test
    public void 닉네임_미입력시__입력미완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_nickname_content_edittext)).setText("");

        verify(mockPresenter).emitFilledUpEvent(false);
    }
}