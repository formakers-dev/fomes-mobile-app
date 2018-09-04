package com.formakers.fomes.helper;

import com.formakers.fomes.model.User;
import com.formakers.fomes.network.api.UserAPI;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class AppBeeAPIHelper {

    private final LocalStorageHelper localStorageHelper;
    private final GoogleSignInAPIHelper googleSignInAPIHelper;
    private final UserAPI userAPI;

    @Inject
    public AppBeeAPIHelper(LocalStorageHelper localStorageHelper, GoogleSignInAPIHelper googleSignInAPIHelper, UserAPI userAPI) {
        this.localStorageHelper = localStorageHelper;
        this.googleSignInAPIHelper = googleSignInAPIHelper;
        this.userAPI = userAPI;
    }

    //TODO : 토큰 재발행 로직 점검 필요 - 현재는 userService그대로 쓰고 있음
    public <T> Observable.Transformer<T, T> refreshExpiredToken() {
        return observable -> observable.retryWhen(errors -> {
                    return errors.observeOn(Schedulers.io())
                            .take(2)
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
