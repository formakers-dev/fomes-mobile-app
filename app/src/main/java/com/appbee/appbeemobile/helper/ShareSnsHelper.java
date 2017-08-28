package com.appbee.appbeemobile.helper;

import android.content.Context;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShareSnsHelper {

    private Context context;

    @Inject
    public ShareSnsHelper(Context context) {
        this.context = context;
    }

    public void shareKakao() {
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(context);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            kakaoTalkLinkMessageBuilder.addText("AppBee 다운로드");

            String url = "https://firebasestorage.googleapis.com/v0/b/appbeemobile.appspot.com/o/appbee-logo.png?alt=media&token=70a97b40-4bf9-4fe5-ba8a-5ab96507831b";
            kakaoTalkLinkMessageBuilder.addImage(url, 160, 160);
            kakaoTalkLinkMessageBuilder.addWebButton("앱비 다운로드", "http://appbeepkg.s3-website.ap-northeast-no_brain_background.amazonaws.com/test/app-release.apk");
            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, context);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
