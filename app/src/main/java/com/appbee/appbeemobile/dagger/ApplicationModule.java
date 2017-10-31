package com.appbee.appbeemobile.dagger;

import android.content.Context;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.adapter.ClabAppsAdapter;
import com.appbee.appbeemobile.adapter.RecommendationAppsAdapter;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.ProjectAPI;
import com.appbee.appbeemobile.network.ProjectService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final AppBeeApplication application;

    public ApplicationModule(AppBeeApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context context() {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    ProjectService projectService(ProjectAPI projectAPI, LocalStorageHelper localStorageHelper) {
        return new ProjectService(projectAPI, localStorageHelper);
    }

    @Singleton
    @Provides
    ClabAppsAdapter clabAppsAdapter(Context context) {
        return new ClabAppsAdapter(context);
    }

    @Singleton
    @Provides
    RecommendationAppsAdapter recommendationAppsAdapter(Context context) {
        return new RecommendationAppsAdapter(context);
    }
}
