package com.appbee.appbeemobile.dagger;

import android.content.Context;

import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.ImageLoader;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.ResourceHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.network.AppAPI;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.ConfigAPI;
import com.appbee.appbeemobile.network.ConfigService;
import com.appbee.appbeemobile.network.ProjectAPI;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.network.StatAPI;
import com.appbee.appbeemobile.network.UserAPI;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

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
    AppAPI appAPI() {
        return mock(AppAPI.class);
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

    @Singleton
    @Provides
    AppService appService() {
        return mock(AppService.class);
    }

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

}
