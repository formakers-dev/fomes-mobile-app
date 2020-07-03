package com.formakers.fomes.common.helper;

import android.content.Context;
import android.widget.Toast;

import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.Log;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShareHelper {
    private static final String TAG = "ShareHelper";

    private Context context;
    private KakaoLinkService kakaoLinkService;

    @Inject
    public ShareHelper(Context context,
                       KakaoLinkService kakaoLinkService) {
        this.context = context;
        this.kakaoLinkService = kakaoLinkService;
    }

    public void sendBetaTestToKaKao(Context activityContext, BetaTest betaTest) {
        List<String> descriptions = Arrays.asList(
                "💰문상 득템! 개이득 게임 테스트 한판 어때?",
                "🎮핵꿀잼! 신박한 게임 테스트 한판 어때?",
                "🤝내 손으로 게임 심폐소생! 멋진 조력자가 되어봐!"
        );
        int descriptionIndex = new Random().nextInt(descriptions.size());
        String description = descriptions.get(descriptionIndex);

        FeedTemplate feedTemplate = FeedTemplate.newBuilder(ContentObject.newBuilder(
                betaTest.getTitle(),
                betaTest.getCoverImageUrl(),
                LinkObject.newBuilder()
                        .setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .build())
                .setDescrption(description)
                .build())
                .addButton(new ButtonObject(
                        "게임 테스트 하러가기",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build()))
                .build();

        Log.v(TAG, activityContext + "\n" + context);
        this.kakaoLinkService.sendDefault(activityContext, feedTemplate, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e(TAG, "카톡 공유하기 에러 발생!\n" + errorResult);
                Toast.makeText(context, "카카오톡 공유하기에 실패하였습니다...😢", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                Log.v(TAG, "카톡 공유하기 성공!\n" + result);
                Toast.makeText(context, "꿀잼 테스트를 카톡 친구에게 공유해보세요!😆", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
