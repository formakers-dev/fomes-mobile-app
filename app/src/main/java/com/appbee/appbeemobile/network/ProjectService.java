package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class ProjectService extends AbstractAppBeeService {
    private static final String TAG = ProjectService.class.getSimpleName();
    private final ProjectAPI projectAPI;
    private final LocalStorageHelper localStorageHelper;

    @Inject
    public ProjectService(ProjectAPI projectAPI, LocalStorageHelper localStorageHelper) {
        this.projectAPI = projectAPI;
        this.localStorageHelper = localStorageHelper;
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
        return projectAPI.getProject(localStorageHelper.getAccessToken(), projectId)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
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

    public Observable<Boolean> postParticipate(String projectId, long seq, String slotId) {
        return projectAPI.postParticipate(localStorageHelper.getAccessToken(), projectId, seq, slotId)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> postCancelParticipate(String project, long seq, String slotId) {
        return projectAPI.postCancleParticipate(localStorageHelper.getAccessToken(), project, seq, slotId)
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io());
    }
}