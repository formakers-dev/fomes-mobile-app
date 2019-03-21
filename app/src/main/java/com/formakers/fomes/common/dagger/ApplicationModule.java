package com.formakers.fomes.common.dagger;

import android.content.Context;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final FomesApplication application;

    public ApplicationModule(FomesApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context context() {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    FirebaseRemoteConfig firebaseRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        remoteConfig.setConfigSettings(configSettings);
        remoteConfig.setDefaults(R.xml.remote_config_default);

        // 앱을 완전히 종료했다가 켜야지만 업데이트 된다... 지금 당장 생각나는 대안으로는
        // - 리모트 컨피그 업데이트용 알림을 보낸다 (notification은 발생시키지 않고 이벤트 성향으로)
        //    - 단점 : 운영페이지처럼 단순하게 텍스트만 변경하면 되는 것이 아니라 알림을 보내야해서 죠큼... 불편할 수 있음
        // 기본 캐쉬 만료시간 : 12시간
        remoteConfig.fetch(0).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                remoteConfig.activateFetched();
            }
        });

        return remoteConfig;
    }
}
