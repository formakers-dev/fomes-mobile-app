package com.formakers.fomes.common.dagger;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.common.LocalBroadcastReceiver;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.job.SendDataJobService;
import com.formakers.fomes.common.network.AppService;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.noti.MessagingService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.NoticeMigrationActivity;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { NetworkModule.class, GlideModule.class, AnalyticsModule.class, DataModule.class })
public interface ApplicationComponent {
    AppStatService appStatService();
    UserService userService();
    ConfigService configService();
    RecommendService recommendService();
    AppService appService();
    BetaTestService requestService();
    EventLogService eventLogService();
    PostService postService();
    FomesUrlHelper fomesUrlHelper();

    GoogleSignInAPIHelper googleSignInAPIHelper();
    SharedPreferencesHelper sharedPreferencesHelper();
    AndroidNativeHelper androidNativeHelper();
    AppUsageDataHelper appUsageDataHelper();

    UserDAO userDAO();
    RequestManager requestManager();
    JobManager jobManager();
    ChannelManager channelManager();

    AnalyticsModule.Analytics analytics();
    ImageLoader imageLoader();

    void inject(MessagingService messagingService);
    void inject(SendDataJobService sendDataJobService);
    void inject(LocalBroadcastReceiver localBroadcastReceiver);

    // fomes
    void inject(NoticeMigrationActivity activity);

    void inject(FomesBaseActivity activity);
    void inject(BaseActivity activity);
}
