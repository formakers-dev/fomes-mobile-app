package com.formakers.fomes.common.view.webview;

import android.net.Uri;

import com.formakers.fomes.common.helper.FomesUrlHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class WebViewPresenterTest {

    WebViewPresenter subject;
    @Mock WebViewConstract.View mockView;
    @Mock FomesUrlHelper mockFomesUrlHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new WebViewPresenter(mockView, mockFomesUrlHelper);
    }

    @Test
    public void isFromDeeplink_호출시__인앱웹뷰_딥링크여부를_체크한다() {
        assertThat(subject.isFromDeeplink(Uri.parse("fomes://web/internal?url=http://www.naver.com"))).isTrue();
        assertThat(subject.isFromDeeplink(Uri.parse("fomes://abcd"))).isFalse();
        assertThat(subject.isFromDeeplink(Uri.parse("fomes://main"))).isFalse();
    }

    @Test
    public void interpreteDeepLink_호출시__인앱웹뷰_딥링크이면__url로드를_준비한다() {
        subject.interpreteDeepLink(Uri.parse("fomes://web/internal?title=제목&url=http://www.google.com?email={email}"));

        verify(mockView).initialize(eq("제목"), eq("http://www.google.com?email={email}"));
    }

    @Test
    public void interpreteDeepLink_호출시__외부브라우저_딥링크면__url을_외부로_던진다() {
        when(mockFomesUrlHelper.interpretUrlParams("http://www.google.com?email={email}")).thenReturn("http://www.google.com?email=test@gmail.com");
        subject.interpreteDeepLink(Uri.parse("fomes://web/external?title=제목&url=http://www.google.com?email={email}"));

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.google.com?email={email}"));

        ArgumentCaptor<Uri> argumentCaptor = ArgumentCaptor.forClass(Uri.class);
        verify(mockView).throwDeepLink(argumentCaptor.capture());
        String actualDeepLink = String.valueOf(argumentCaptor.getValue());
        assertThat(actualDeepLink).isEqualTo("http://www.google.com?email=test@gmail.com");
    }

    @Test
    public void loadContents_호출시__url이면__해석후_로드한다() {
        when(mockFomesUrlHelper.interpretUrlParams("http://www.google.com?email={email}")).thenReturn("http://www.google.com?email=test@gmail.com");

        subject.loadContents("http://www.google.com?email={email}");

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.google.com?email={email}"));
        verify(mockView).loadUrl("http://www.google.com?email=test@gmail.com");
    }

    @Test
    public void loadContents_호출시__html이면__로드한다() {
        subject.loadContents("<html></html>");

        verify(mockView).loadHtml("<html></html>");
    }
}