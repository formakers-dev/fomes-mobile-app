package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.AppBeeAPIHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class ProjectService extends AbstractAppBeeService {
    private static final String TAG = ProjectService.class.getSimpleName();
    private final ProjectAPI projectAPI;
    private final LocalStorageHelper localStorageHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public ProjectService(ProjectAPI projectAPI, LocalStorageHelper localStorageHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.projectAPI = projectAPI;
        this.localStorageHelper = localStorageHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Observable<List<Project>> getAllProjects() {
        return projectAPI.getAllProjects(localStorageHelper.getAccessToken())
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Project> getProject(String projectId) {
        return Observable.defer(() -> projectAPI.getProject(localStorageHelper.getAccessToken(), projectId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<Project>> getAllInterviews() {
        return projectAPI.getAllInterviews(localStorageHelper.getAccessToken())
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Project>> getRegisteredInterviews() {
        return projectAPI.getRegisteredInterviews(localStorageHelper.getAccessToken())
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Project> getInterview(String projectId, long seq) {
        return projectAPI.getInterview(localStorageHelper.getAccessToken(), projectId, seq)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Completable postParticipate(String projectId, long seq, String slotId) {
        return projectAPI.postParticipate(localStorageHelper.getAccessToken(), projectId, seq, slotId)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }

    public Completable postCancelParticipate(String project, long seq, String slotId) {
        return projectAPI.postCancleParticipate(localStorageHelper.getAccessToken(), project, seq, slotId)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }
}