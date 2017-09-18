package com.appbee.appbeemobile.helper;

import android.content.Context;

import com.appbee.appbeemobile.util.AppBeeConstants;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.appbee.appbeemobile.BuildConfig.SERVER_BASE_URL;

@Singleton
public class ShareSnsHelper {

    private Context context;
    private LocalStorageHelper localStorageHelper;

    @Inject
    public ShareSnsHelper(Context context, LocalStorageHelper localStorageHelper) {
        this.context = context;
        this.localStorageHelper = localStorageHelper;
    }

    public void shareKakao(AppBeeConstants.CharacterType characterType) {
        try {
            String imageUrl = context.getString(characterType.shareImageUrl);
            String downloadUrl = SERVER_BASE_URL + "download?referer=" + localStorageHelper.getUserId();

            FeedTemplate params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder("모바일로 나의 벌 타입 알아보러 Go!", imageUrl,
                            LinkObject.newBuilder().setWebUrl(downloadUrl).setMobileWebUrl(downloadUrl).build())
                            .setImageWidth(334).setImageHeight(294)
                            .build())
                    .addButton(new ButtonObject("다운로드하기!", LinkObject.newBuilder().setWebUrl(downloadUrl).setMobileWebUrl(downloadUrl).build()))
                    .build();

            KakaoLinkService.getInstance().sendDefault(context, params, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Logger.e(errorResult.toString());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
