package com.formakers.fomes.common.helper;

import android.content.Context;

import com.formakers.fomes.common.network.vo.BetaTest;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.FeedTemplate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ShareHelperTest {

    @Mock private Context mockContext;
    @Mock private KakaoLinkService mockKakaoLinkService;

    ShareHelper subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new ShareHelper(mockContext, mockKakaoLinkService);
    }

    @Test
    public void sendBetaTestToKaKao_호출시__베타테스트_정보를_카카오톡에_공유한다() {
        String[] descriptions = {
                "💰문상 득템! 개이득 게임 테스트 한판 어때?",
                "🎮핵꿀잼! 신박한 게임 테스트 한판 어때?",
                "🤝내 손으로 게임 심폐소생! 멋진 조력자가 되어봐!"
        };
        BetaTest betaTest = new BetaTest().setTitle("제목").setCoverImageUrl("coverImageLink").setDescription("설명");

        subject.sendBetaTestToKaKao(betaTest);

        ArgumentCaptor<FeedTemplate> argumentCaptor = ArgumentCaptor.forClass(FeedTemplate.class);
        verify(mockKakaoLinkService).sendDefault(eq(mockContext), argumentCaptor.capture(), any());

        FeedTemplate actual = argumentCaptor.getValue();

        assertThat(actual.getContentObject().getTitle()).isEqualTo("제목");
        assertThat(actual.getContentObject().getImageUrl()).isEqualTo("coverImageLink");
        assertThat(descriptions).contains(actual.getContentObject().getDescription());
    }
}