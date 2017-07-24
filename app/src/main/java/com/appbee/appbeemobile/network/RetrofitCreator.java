package com.appbee.appbeemobile.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCreator {
    public static Retrofit createRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        OkHttpClient okHttpClient = builder.build();
        return new Retrofit.Builder()
                .baseUrl("http://172.16.0.164:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)   // for logs
                .build();
    }
}
