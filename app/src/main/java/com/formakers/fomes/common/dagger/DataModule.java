package com.formakers.fomes.common.dagger;

import android.text.TextUtils;

import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;

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
        return userDAO.getUserInfo().map(user -> {
            String email = sharedPreferencesHelper.getUserEmail();
            if (!TextUtils.isEmpty(email)) {
                return email;
            } else {
                return user.getEmail();
            }
        });
    }

    @Singleton
    @Provides
    @Named("userNickName")
    Single<String> userNickName(SharedPreferencesHelper sharedPreferencesHelper, UserDAO userDAO) {
        return userDAO.getUserInfo().map(user -> {
            String nickName = sharedPreferencesHelper.getUserNickName();
            if (!TextUtils.isEmpty(nickName)) {
                return nickName;
            } else {
                return user.getNickName();
            }
        });
    }
}
