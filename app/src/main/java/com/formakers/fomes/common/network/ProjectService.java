package com.formakers.fomes.common.network;

import com.formakers.fomes.helper.APIHelper;
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
public class ProjectService extends AbstractService {
    private static final String TAG = "ProjectService";
    private final ProjectAPI projectAPI;
    private final SharedPreferencesHelper SharedPreferencesHelper;
    private final APIHelper APIHelper;

    @Inject
    public ProjectService(ProjectAPI projectAPI, SharedPreferencesHelper SharedPreferencesHelper, APIHelper APIHelper) {
        this.projectAPI = projectAPI;
        this.SharedPreferencesHelper = SharedPreferencesHelper;
        this.APIHelper = APIHelper;
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
                .compose(APIHelper.refreshExpiredToken());
    }

    public Observable<Project> getProject(String projectId) {
        return Observable.defer(() -> projectAPI.getProject(SharedPreferencesHelper.getAccessToken(), projectId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken());
    }

    public Observable<List<Project>> getAllInterviews() {
        return Observable.defer(() -> projectAPI.getAllInterviews(SharedPreferencesHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken());
    }

    public Observable<List<Project>> getRegisteredInterviews() {
        return Observable.defer(() -> projectAPI.getRegisteredInterviews(SharedPreferencesHelper.getAccessToken()))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken());
    }

    public Observable<Project> getInterview(String projectId, long seq) {
        return Observable.defer(() -> projectAPI.getInterview(SharedPreferencesHelper.getAccessToken(), projectId, seq))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken());
    }

    public Completable postParticipate(String projectId, long seq, String slotId) {
        return Observable.defer(() -> projectAPI.postParticipate(SharedPreferencesHelper.getAccessToken(), projectId, seq, slotId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken())
                .toCompletable();
    }

    public Completable postCancelParticipate(String projectId, long seq, String slotId) {
        return Observable.defer(() -> projectAPI.postCancelParticipate(SharedPreferencesHelper.getAccessToken(), projectId, seq, slotId))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(APIHelper.refreshExpiredToken())
                .toCompletable();
    }
}