package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.network.AppAPI;
import com.appbee.appbeemobile.network.ConfigAPI;
import com.appbee.appbeemobile.network.ProjectAPI;
import com.appbee.appbeemobile.network.StatAPI;
import com.appbee.appbeemobile.network.UserAPI;

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
    private final static long NETWORK_TIMEOUT = 30L;

    @Singleton
    @Provides
    OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        builder.connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(30L, TimeUnit.SECONDS);
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
    AppAPI appAPI(Retrofit retrofit) {
        return retrofit.create(AppAPI.class);
    }

    @Singleton
    @Provides
    ProjectAPI projectAPI(Retrofit retrofit) {
        return retrofit.create(ProjectAPI.class);
    }

    @Singleton
    @Provides
    ConfigAPI configAPI(Retrofit retrofit) {
        return retrofit.create(ConfigAPI.class);
    }
}