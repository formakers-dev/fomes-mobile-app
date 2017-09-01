package com.appbee.appbeemobile.helper;

import android.content.Context;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.appbee.appbeemobile.BuildConfig.SERVER_BASE_URL;

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
            String url = "https://firebasestorage.googleapis.com/v0/b/appbeemobile.appspot.com/o/share_card.jpg?alt=media&token=3ea4f582-e3af-4a90-bc9e-bb60cef0ba58";
            kakaoTalkLinkMessageBuilder.addImage(url, 160, 86);
            String msg = "[해커톤] 나는 어떤 꿀벌일까나?\n숨겨왔던 너의~모바일 성향 모두~알려~줄게에이예에";
            kakaoTalkLinkMessageBuilder.addText(msg);
            kakaoTalkLinkMessageBuilder.addWebButton("다운로드하러가기", SERVER_BASE_URL + "download?abc=def");
            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
