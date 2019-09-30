package com.formakers.fomes.common.view.webview;

import android.net.Uri;
import android.os.Bundle;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.FomesUrlHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    public void getInterpretedDeeplinkBundle_호출시__링크를_해석하고__결과_데이터를_리턴한다() {
        when(mockFomesUrlHelper.interpretUrlParams("http://www.google.com?email={email}")).thenReturn("http://www.google.com?email=test@gmail.com");
        Bundle bundle = subject.getInterpretedDeeplinkBundle(Uri.parse("fomes://web/internal?title=제목&url=http://www.google.com?email={email}"));

        String title = bundle.getString(FomesConstants.WebView.EXTRA_TITLE);
        String contents = bundle.getString(FomesConstants.WebView.EXTRA_CONTENTS);

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.google.com?email={email}"));
        assertThat(title).isEqualTo("제목");
        assertThat(contents).isEqualTo("http://www.google.com?email=test@gmail.com");
    }
}