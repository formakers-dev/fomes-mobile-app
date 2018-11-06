package com.formakers.fomes.common.dagger;

import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.job.SendDataJobService;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.common.network.ProjectService;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.presenter.MainPresenter;
import com.formakers.fomes.provisioning.presenter.ProvisioningPresenter;
import com.formakers.fomes.service.MessagingTokenService;
import com.formakers.fomes.wishList.WishListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { NetworkModule.class, GlideModule.class })
public interface ApplicationComponent {
    AppStatService appStatService();
    UserService userService();
    ProjectService projectService();
    ConfigService configService();
    RecommendService recommendService();

    GoogleSignInAPIHelper googleSignInAPIHelper();
    SharedPreferencesHelper sharedPreferencesHelper();

    UserDAO userDAO();

    void inject(MessagingTokenService messagingTokenService);
    void inject(SendDataJobService sendDataJobService);

    // fomes
    void inject(ProvisioningPresenter provisioningPresenter);
    void inject(RecentAnalysisReportPresenter reportPresenter);
    void inject(MainPresenter presenter);

    void inject(FomesBaseActivity activity);
    void inject(BaseActivity activity);
    void inject(WishListActivity activity);
}
