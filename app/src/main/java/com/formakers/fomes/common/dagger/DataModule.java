package com.formakers.fomes.common.dagger;

import android.text.TextUtils;

import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;

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
}
