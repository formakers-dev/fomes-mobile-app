package com.formakers.fomes.common.dagger;

import android.text.TextUtils;

import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.repository.dao.UserDAO;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Single;

@Module
public class DataModule {

    @Singleton
    @Provides
    @Named("userEmail")
    Single<String> userEmail(SharedPreferencesHelper sharedPreferencesHelper, UserDAO userDAO) {
        return Single.fromEmitter(singleEmitter -> {
            String email = sharedPreferencesHelper.getUserEmail();
            if (!TextUtils.isEmpty(email)) {
                singleEmitter.onSuccess(email);
            } else {
                userDAO.getUserInfo()
                        .map(User::getEmail)
                        .subscribe(singleEmitter::onSuccess, singleEmitter::onError);
            }
        });
    }

    @Singleton
    @Provides
    @Named("userNickName")
    Single<String> userNickName(SharedPreferencesHelper sharedPreferencesHelper, UserDAO userDAO) {
        return Single.fromEmitter(singleEmitter -> {
            String nickName = sharedPreferencesHelper.getUserNickName();
            if (!TextUtils.isEmpty(nickName)) {
                singleEmitter.onSuccess(nickName);
            } else {
                userDAO.getUserInfo()
                        .map(User::getNickName)
                        .subscribe(userNickName -> {
                            // TODO : [임시코드] 크리티컬 릴리즈의 다음 릴리즈 때 지우기
                            sharedPreferencesHelper.setUserNickName(userNickName);
                            singleEmitter.onSuccess(nickName);
                        }, singleEmitter::onError);
            }
        });
    }
}
