package com.formakers.fomes.common.network;

import com.formakers.fomes.helper.AppBeeAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.common.network.api.ProjectAPI;

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
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final AppBeeAPIHelper appBeeAPIHelper;

    @Inject
    public ProjectService(ProjectAPI projectAPI, SharedPreferencesHelper SharedPreferencesHelper, AppBeeAPIHelper appBeeAPIHelper) {
        this.projectAPI = projectAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.appBeeAPIHelper = appBeeAPIHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Observable<List<Project>> getAllProjects() {
        return Observable.defer(() -> projectAPI.getAllProjects(SharedPreferencesHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<Project> getProject(String projectId) {
        return Observable.defer(() -> projectAPI.getProject(SharedPreferencesHelper.getAccessToken(), projectId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<Project>> getAllInterviews() {
        return Observable.defer(() -> projectAPI.getAllInterviews(SharedPreferencesHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<List<Project>> getRegisteredInterviews() {
        return Observable.defer(() -> projectAPI.getRegisteredInterviews(SharedPreferencesHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Observable<Project> getInterview(String projectId, long seq) {
        return Observable.defer(() -> projectAPI.getInterview(SharedPreferencesHelper.getAccessToken(), projectId, seq))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken());
    }

    public Completable postParticipate(String projectId, long seq, String slotId) {
        return Observable.defer(() -> projectAPI.postParticipate(SharedPreferencesHelper.getAccessToken(), projectId, seq, slotId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable postCancelParticipate(String projectId, long seq, String slotId) {
        return Observable.defer(() -> projectAPI.postCancelParticipate(SharedPreferencesHelper.getAccessToken(), projectId, seq, slotId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(appBeeAPIHelper.refreshExpiredToken())
                .toCompletable();
    }
}