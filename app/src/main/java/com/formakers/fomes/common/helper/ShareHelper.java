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
                "๐ฐ๋ฌธ์ ๋ํ! ๊ฐ์ด๋ ๊ฒ์ ํ์คํธ ํํ ์ด๋?",
                "๐ฎํต๊ฟ์ผ! ์ ๋ฐํ ๊ฒ์ ํ์คํธ ํํ ์ด๋?",
                "๐ค๋ด ์์ผ๋ก ๊ฒ์ ์ฌํ์์! ๋ฉ์ง ์กฐ๋ ฅ์๊ฐ ๋์ด๋ด!"
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
                        "๊ฒ์ ํ์คํธ ํ๋ฌ๊ฐ๊ธฐ",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build()))
                .build();

        Log.v(TAG, activityContext + "\n" + context);
        this.kakaoLinkService.sendDefault(activityContext, feedTemplate, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e(TAG, "์นดํก ๊ณต์ ํ๊ธฐ ์๋ฌ ๋ฐ์!\n" + errorResult);
                Toast.makeText(context, "์นด์นด์คํก ๊ณต์ ํ๊ธฐ์ ์คํจํ์์ต๋๋ค...๐ข", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                Log.v(TAG, "์นดํก ๊ณต์ ํ๊ธฐ ์ฑ๊ณต!\n" + result);
                Toast.makeText(context, "๊ฟ์ผ ํ์คํธ๋ฅผ ์นดํก ์น๊ตฌ์๊ฒ ๊ณต์ ํด๋ณด์ธ์!๐", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
