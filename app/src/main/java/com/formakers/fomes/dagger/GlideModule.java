package com.formakers.fomes.dagger;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ApplicationModule.class, NetworkModule.class})
public class GlideModule {

    @Singleton
    @Provides
    public RequestManager glideRequestManager(Context context) {
        return Glide.with(context);
    }
}
