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
    private static final String TAG = "ProjectService";
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
        return Observable.defer(() -> projectAPI.getAllProjects(localStorageHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<Project> getProject(String projectId) {
        return Observable.defer(() -> projectAPI.getProject(localStorageHelper.getAccessToken(), projectId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<Project>> getAllInterviews() {
        return Observable.defer(() -> projectAPI.getAllInterviews(localStorageHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<Project>> getRegisteredInterviews() {
        return Observable.defer(() -> projectAPI.getRegisteredInterviews(localStorageHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<Project> getInterview(String projectId, long seq) {
        return Observable.defer(() -> projectAPI.getInterview(localStorageHelper.getAccessToken(), projectId, seq))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Completable postParticipate(String projectId, long seq, String slotId) {
        return Observable.defer(() -> projectAPI.postParticipate(localStorageHelper.getAccessToken(), projectId, seq, slotId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable postCancelParticipate(String projectId, long seq, String slotId) {
        return Observable.defer(() -> projectAPI.postCancelParticipate(localStorageHelper.getAccessToken(), projectId, seq, slotId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }
}