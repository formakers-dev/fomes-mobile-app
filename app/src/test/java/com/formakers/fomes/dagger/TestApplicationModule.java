package com.formakers.fomes.dagger;

import android.content.Context;

import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.helper.MessagingHelper;
import com.formakers.fomes.helper.ResourceHelper;
import com.formakers.fomes.helper.TimeHelper;
import com.formakers.fomes.network.AppStatService;
import com.formakers.fomes.network.ConfigAPI;
import com.formakers.fomes.network.ConfigService;
import com.formakers.fomes.network.ProjectAPI;
import com.formakers.fomes.network.ProjectService;
import com.formakers.fomes.network.StatAPI;
import com.formakers.fomes.network.UserAPI;
import com.formakers.fomes.network.UserService;
import com.formakers.fomes.repository.helper.AppRepositoryHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestApplicationModule {
    private final TestAppBeeApplication application;

    public TestApplicationModule(TestAppBeeApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context context() {
        return application.getApplicationContext();
    }

    /**
     * API interfaces
     */

    @Provides
    @Singleton
    StatAPI statAPI() {
        return mock(StatAPI.class);
    }

    @Singleton
    @Provides
    UserAPI userAPI() {
        return mock(UserAPI.class);
    }

    @Singleton
    @Provides
    ProjectAPI projectAPI() {
        return mock(ProjectAPI.class);
    }

    @Singleton
    @Provides
    ConfigAPI configAPI() {
        return mock(ConfigAPI.class);
    }

    /**
     * API Service
     */
    @Provides
    @Singleton
    AppStatService appStatService() {
        return mock(AppStatService.class);
    }

    @Singleton
    @Provides
    UserService userService() {
        return mock(UserService.class);
    }

    @Singleton
    @Provides
    ProjectService projectService() {
        return mock(ProjectService.class);
    }

    @Singleton
    @Provides
    ConfigService configService() {
        return mock(ConfigService.class);
    }

    /**
     * Helper
     */

    @Singleton
    @Provides
    LocalStorageHelper localStorageHelper() {
        return mock(LocalStorageHelper.class);
    }

    @Singleton
    @Provides
    AppRepositoryHelper appRepositoryHelper() {
        return mock(AppRepositoryHelper.class);
    }

    @Provides
    @Singleton
    AppUsageDataHelper appUsageDataHelper() {
        return mock(AppUsageDataHelper.class);
    }

    @Provides
    @Singleton
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper() {
        return mock(AppBeeAndroidNativeHelper.class);
    }

    @Singleton
    @Provides
    TimeHelper timeHelper() {
        return mock(TimeHelper.class);
    }

    @Singleton
    @Provides
    ResourceHelper resourceHelper() {
        return mock(ResourceHelper.class);
    }

    @Singleton
    @Provides
    ImageLoader imageLoader() {
        return mock(ImageLoader.class);
    }

    @Singleton
    @Provides
    GoogleSignInAPIHelper googleSignInAPIHelper() {
        return mock(GoogleSignInAPIHelper.class);
    }

    @Singleton
    @Provides
    MessagingHelper messagingHelper() {
        return mock(MessagingHelper.class);
    }

}
