package com.formakers.fomes.common.dagger;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.common.network.api.AppAPI;
import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.network.api.ConfigAPI;
import com.formakers.fomes.common.network.api.EventLogAPI;
import com.formakers.fomes.common.network.api.PostAPI;
import com.formakers.fomes.common.network.api.RecommendAPI;
import com.formakers.fomes.common.network.api.StatAPI;
import com.formakers.fomes.common.network.api.UserAPI;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    private final static long NETWORK_TIMEOUT = 60L;

    @Singleton
    @Provides
    OkHttpClient okHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);

        return builder.build();
    }

    @Singleton
    @Provides
    Retrofit retrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)   // for logs
                .build();
    }

    @Singleton
    @Provides
    StatAPI statAPI(Retrofit retrofit) {
        return retrofit.create(StatAPI.class);
    }

    @Singleton
    @Provides
    UserAPI userAPI(Retrofit retrofit) {
        return retrofit.create(UserAPI.class);
    }

    @Singleton
    @Provides
    ConfigAPI configAPI(Retrofit retrofit) {
        return retrofit.create(ConfigAPI.class);
    }

    @Singleton
    @Provides
    RecommendAPI recommendAPI(Retrofit retrofit) {
        return retrofit.create(RecommendAPI.class);
    }

    @Singleton
    @Provides
    AppAPI appAPI(Retrofit retrofit) {
        return retrofit.create(AppAPI.class);
    }

    @Singleton
    @Provides
    BetaTestAPI requestAPI(Retrofit retrofit) {
        return retrofit.create(BetaTestAPI.class);
    }

    @Singleton
    @Provides
    EventLogAPI eventLogAPI(Retrofit retrofit) {
        return retrofit.create(EventLogAPI.class);
    }

    @Singleton
    @Provides
    PostAPI postAPI(Retrofit retrofit) {
        return retrofit.create(PostAPI.class);
    }
}