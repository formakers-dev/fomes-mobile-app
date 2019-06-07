package com.formakers.fomes.provisioning.view;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import com.formakers.fomes.R;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ProvisioningNickNameFragmentTest {

    @Mock ProvisioningContract.Presenter mockPresenter;

    ProvisioningNickNameFragment subject;
    FragmentScenario<ProvisioningNickNameFragment> scenario;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockPresenter.getAnalytics()).thenReturn(mock(AnalyticsModule.Analytics.class));

        scenario = FragmentScenario.launchInContainer(ProvisioningNickNameFragment.class);
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
    public void ProvisioningNickNameFragment_시작시__프로비저닝_인생게임_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.provision_nickname_content_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_content_edittext).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void ProvisioningNickNameFragment_가_보여질시__입력완료여부_이벤트를_보낸다() {
        subject.onSelectedPage();
        verify(this.mockPresenter).emitFilledUpEvent(eq(subject), anyBoolean());
    }

    @Test
    public void 다음버튼_클릭시__입력된_닉네임_검사_API를_호출한다() {
        EditText nickNameEditText = subject.getView().findViewById(R.id.provision_nickname_content_edittext);
        nickNameEditText.setText("새로운닉네임");

        when(mockPresenter.requestVerifyUserNickName(anyString())).thenReturn(Completable.complete());

        subject.onNextButtonClick();

        verify(mockPresenter).requestVerifyUserNickName(eq("새로운닉네임"));
    }

    @Test
    public void 다음버튼_클릭시__닉네임_검사에_성공하면__업데이트하고_다음화면으로_넘어간다() {
        when(mockPresenter.requestVerifyUserNickName(anyString())).thenReturn(Completable.complete());

        subject.onNextButtonClick();

        verify(mockPresenter).updateNickNameToUser(anyString());
        verify(mockPresenter).emitNextPageEvent();
    }

    @Test
    public void 다음버튼_클릭시__닉네임_검사에_실패하면__중복경고문구를_보여준다() {
        when(mockPresenter.requestVerifyUserNickName(anyString())).
                thenReturn(Completable.error(new HttpException(Response.error(409, ResponseBody.create(null, "")))));

        subject.onNextButtonClick();

        verify(mockPresenter).emitFilledUpEvent(subject, false);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_format_warning_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(((TextView) subject.getView().findViewById(R.id.provision_nickname_format_warning_textview)).getText())
                .isEqualTo("* 이미 사용된 닉네임 입니다. 다른 닉네임을 입력해주세요.");
    }

    @Test
    public void 닉네임_입력시__입력완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_nickname_content_edittext)).setText("예나르");

        verify(mockPresenter).emitFilledUpEvent(subject, true);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_format_warning_textview).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void 닉네임_미입력시__입력미완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_nickname_content_edittext)).setText("");

        verify(mockPresenter).emitFilledUpEvent(subject, false);
    }

    @Test
    public void 닉네임_특수문자_입력시__경고문구를_보여주고_입력미완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_nickname_content_edittext)).setText("%");

        verify(mockPresenter).emitFilledUpEvent(subject, false);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_format_warning_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(((TextView) subject.getView().findViewById(R.id.provision_nickname_format_warning_textview)).getText())
                .isEqualTo("* 닉네임은 2글자에서 10글자 사이로, 한글/영어/숫자로만 작성 가능합니다.");
    }

    @Test
    public void 닉네임_글자수가_제한길이보다_클경우__경고문구를_보여주고_입력미완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_nickname_content_edittext)).setText("일이삼사오육칠팔구십십십십");

        verify(mockPresenter).emitFilledUpEvent(subject, false);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_format_warning_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(((TextView) subject.getView().findViewById(R.id.provision_nickname_format_warning_textview)).getText())
                .isEqualTo("* 닉네임은 2글자에서 10글자 사이로, 한글/영어/숫자로만 작성 가능합니다.");
    }

    @Test
    public void 닉네임_글자수가_제한길이보다_작을경우__경고문구를_보여주고_입력미완료_이벤트를_보낸다() {
        ((EditText) subject.getView().findViewById(R.id.provision_nickname_content_edittext)).setText("일");

        verify(mockPresenter).emitFilledUpEvent(subject, false);
        assertThat(subject.getView().findViewById(R.id.provision_nickname_format_warning_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(((TextView) subject.getView().findViewById(R.id.provision_nickname_format_warning_textview)).getText())
                .isEqualTo("* 닉네임은 2글자에서 10글자 사이로, 한글/영어/숫자로만 작성 가능합니다.");
    }
}