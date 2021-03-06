package com.formakers.fomes.common.dagger;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.common.LocalBroadcastReceiver;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.helper.ShareHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.job.SendDataJobService;
import com.formakers.fomes.common.network.AppService;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.noti.MessagingService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import rx.Single;

@Singleton
@Component(modules = { NetworkModule.class, GlideModule.class, AnalyticsModule.class, DataModule.class, ShareModule.class })
public interface ApplicationComponent {
    AppStatService appStatService();
    UserService userService();
    ConfigService configService();
    RecommendService recommendService();
    AppService appService();
    BetaTestService requestService();
    EventLogService eventLogService();
    PostService postService();
    PointService pointService();
    FomesUrlHelper fomesUrlHelper();

    GoogleSignInAPIHelper googleSignInAPIHelper();
    SharedPreferencesHelper sharedPreferencesHelper();
    AndroidNativeHelper androidNativeHelper();
    AppUsageDataHelper appUsageDataHelper();
    ShareHelper shareHelper();

    UserDAO userDAO();
    RequestManager requestManager();
    JobManager jobManager();
    ChannelManager channelManager();
    FirebaseRemoteConfig firebaseRemoteConfig();

    AnalyticsModule.Analytics analytics();
    ImageLoader imageLoader();
    @Named("userEmail") Single<String> userEmail();
    @Named("userNickName") Single<String> userNickName();

    void inject(MessagingService messagingService);
    void inject(SendDataJobService sendDataJobService);
    void inject(LocalBroadcastReceiver localBroadcastReceiver);

    // fomes
    void inject(FomesNoticeDialog fomesNoticeDialog);

    void inject(FomesBaseActivity activity);
    void inject(BaseActivity activity);
}
