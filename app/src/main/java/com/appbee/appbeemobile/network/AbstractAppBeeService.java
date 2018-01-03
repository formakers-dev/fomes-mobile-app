package com.appbee.appbeemobile.network;

import android.util.Log;

import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

abstract class AbstractAppBeeService {

    @Inject
    GoogleSignInAPIHelper googleSignInAPIHelper;

    @Inject
    UserAPI userAPI;

    @Inject
    LocalStorageHelper localStorageHelper;

    protected abstract String getTag();

    public void logError(Throwable error) {
        if (error instanceof HttpException) {
            Log.e(getTag(), String.valueOf(((HttpException) error).code()));
        } else {
            Log.e(getTag(), error.getMessage());
        }
    }

    //TODO : 토큰 재발행 로직 점검 필요 - 현재는 userService그대로 쓰고 있음
    <T> Observable.Transformer<T, T> refreshExpiredToken() {
        return observable -> observable.retryWhen(errors -> {
                    return errors.take(2)
                            .filter(error -> error instanceof HttpException)
                            .filter(error -> ((HttpException) error).code() == 401 || ((HttpException) error).code() == 403)
                            .flatMap(error -> googleSignInAPIHelper.requestSilentSignInResult())
                            .filter(googleSignInResult -> googleSignInResult != null && googleSignInResult.isSuccess() && googleSignInResult.getSignInAccount() != null)
                            .flatMap(googleSignInResult -> Observable.just(googleSignInResult.getSignInAccount()))
                            .filter(account -> account != null)
                            .flatMap(account -> {
                                User user = new User();
                                user.setUserId(account.getId());
                                return userAPI.signIn(account.getIdToken(), user);
                            })
                            .filter(accessToken -> accessToken != null && !accessToken.isEmpty())
                            .flatMap(accessToken -> {
                                localStorageHelper.setAccessToken(accessToken);
                                return Observable.timer(1, TimeUnit.SECONDS);
                            });
                }
        );
    }
}
