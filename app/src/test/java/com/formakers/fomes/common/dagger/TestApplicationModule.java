package com.formakers.fomes.common.dagger;

import android.content.Context;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.AppService;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.api.AppAPI;
import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.network.api.ConfigAPI;
import com.formakers.fomes.common.network.api.EventLogAPI;
import com.formakers.fomes.common.network.api.PostAPI;
import com.formakers.fomes.common.network.api.RecommendAPI;
import com.formakers.fomes.common.network.api.StatAPI;
import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.helper.ResourceHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.helper.TimeHelper;

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
    BetaTestAPI requestAPI() {
        return mock(BetaTestAPI.class);
    }

    @Provides
    @Singleton
    EventLogAPI eventLogAPI() {
        return mock(EventLogAPI.class);
    }

    @Provides
    @Singleton
    PostAPI postAPI() {
        return mock(PostAPI.class);
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
    BetaTestService requestService() {
        return mock(BetaTestService.class);
    }

    @Singleton
    @Provides
    EventLogService eventLogService() {
        return mock(EventLogService.class);
    }

    @Singleton
    @Provides
    PostService postService() {
        return mock(PostService.class);
    }

    /**
     * Helper
     */

    @Singleton
    @Provides
    SharedPreferencesHelper SharedPreferencesHelper() {
        return mock(SharedPreferencesHelper.class);
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

    @Singleton
    @Provides
    FomesUrlHelper fomesUrlHelper() {
        return mock(FomesUrlHelper.class);
    }

    @Singleton
    @Provides
    ChannelManager channelManager() {
        return mock(ChannelManager.class);
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

    @Singleton
    @Provides
    AnalyticsModule.Analytics analytics() {
        return mock(AnalyticsModule.Analytics.class);
    }
}
