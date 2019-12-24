package com.formakers.fomes.common.view.webview;

import android.net.Uri;

import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.vo.Post;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import rx.Single;
import rx.subscriptions.CompositeSubscription;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class WebViewPresenterTest {

    WebViewPresenter subject;
    @Mock WebViewConstract.View mockView;
    @Mock FomesUrlHelper mockFomesUrlHelper;
    @Mock PostService mockPostService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockView.getCompositeSubscription()).thenReturn(new CompositeSubscription());

        subject = new WebViewPresenter(mockView, mockFomesUrlHelper, mockPostService);
    }

    @Test
    public void isFromDeeplink_호출시__인앱웹뷰_딥링크여부를_체크한다() {
        assertThat(subject.isFromDeeplink(Uri.parse("fomes://web/internal?url=http://www.naver.com"))).isTrue();
        assertThat(subject.isFromDeeplink(Uri.parse("fomes://abcd"))).isTrue();
        assertThat(subject.isFromDeeplink(Uri.parse("otehrs://main"))).isFalse();
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
    public void interpreteDeepLink_호출시__포스팅_딥링크면__포스팅을_로드한다() {
        when(mockPostService.getPromotion("postId")).thenReturn(Single.just(new Post().setTitle("타이틀").setContents("http://www.naver.com")));

        subject.interpreteDeepLink(Uri.parse("fomes://posts/detail?id=postId"));

        verify(mockPostService).getPromotion("postId");
        verify(mockView).initialize("타이틀", "http://www.naver.com");
    }

    @Test
    public void interpreteDeepLink_호출시__포스팅_딥링크이고_해당_포스트의_컨텐츠가_딥링크면__딥링크로_로드한다() {
        when(mockPostService.getPromotion("postId")).thenReturn(Single.just(new Post().setTitle("타이틀").setDeeplink("fomes://web/external?url=http://www.naver.com?email={email}").setContents("it_should_be_skipped")));

        subject.interpreteDeepLink(Uri.parse("fomes://posts/detail?id=postId"));

        verify(mockPostService).getPromotion("postId");
        ArgumentCaptor<Uri> argumentCaptor = ArgumentCaptor.forClass(Uri.class);
        verify(mockView).throwDeepLink(argumentCaptor.capture());
        String actualDeepLink = String.valueOf(argumentCaptor.getValue());
        assertThat(actualDeepLink).isEqualTo("fomes://web/external?url=http://www.naver.com?email={email}");
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