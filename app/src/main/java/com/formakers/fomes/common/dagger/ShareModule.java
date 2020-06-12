package com.formakers.fomes.common.dagger;

import android.content.Context;

import com.kakao.kakaolink.v2.KakaoLinkService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

// TODO : 나중엔 ShareManager 와 같은 클래스에 연결해서 하나의 공통 VO로 여러 공유클래스를 관리하면 될 듯... 지금은 카카오만 있으니까 사용처에서 이 모듈을 직접 접근하여 사용하는 것으로! 나중엔 필요한 컴포넌트에서만 이 모듈을 연결해서 사용하기
@Module
public class ShareModule {

    @Singleton
    @Provides
    public KakaoLinkService kakaoLinkService(Context context) {
        return KakaoLinkService.getInstance();
    }
}
