package com.formakers.fomes.common.dagger;

import android.content.Context;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.AppService;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.RequestService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.api.AppAPI;
import com.formakers.fomes.common.network.api.ConfigAPI;
import com.formakers.fomes.common.network.api.RecommendAPI;
import com.formakers.fomes.common.network.api.RequestAPI;
import com.formakers.fomes.common.network.api.StatAPI;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.repository.helper.AppRepositoryHelper;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.helper.ResourceHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.helper.TimeHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestApplicationModule {
    private final TestFomesApplication application;

    public TestApplicationModule(TestFomesApplication application) {
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
    ConfigAPI configAPI() {
        return mock(ConfigAPI.class);
    }

    @Singleton
    @Provides
    RecommendAPI recommendAPI() {
        return mock(RecommendAPI.class);
    }

    @Singleton
    @Provides
    AppAPI appAPI() {
        return mock(AppAPI.class);
    }

    @Singleton
    @Provides
    RequestAPI requestAPI() {
        return mock(RequestAPI.class);
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
    ConfigService configService() {
        return mock(ConfigService.class);
    }

    @Singleton
    @Provides
    RecommendService recommendService() {
        return mock(RecommendService.class);
    }

    @Singleton
    @Provides
    AppService appService() {
        return mock(AppService.class);
    }

    @Singleton
    @Provides
    RequestService requestService() {
        return mock(RequestService.class);
    }

    /**
     * Helper
     */

    @Singleton
    @Provides
    SharedPreferencesHelper SharedPreferencesHelper() {
        return mock(SharedPreferencesHelper.class);
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
    AndroidNativeHelper appBeeAndroidNativeHelper() {
        return mock(AndroidNativeHelper.class);
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

    /**
     * ImageLoaders
     */

    @Singleton
    @Provides
    RequestManager glideRequestManager() {
        return mock(RequestManager.class);
    }

    /**
     * Andorid Job
     */

    @Singleton
    @Provides
    JobManager jobManager() {
        return mock(JobManager.class);
    }

    /**
     * Database
     */

    @Singleton
    @Provides
    UserDAO userDAO() {
        return mock(UserDAO.class);
    }
}
