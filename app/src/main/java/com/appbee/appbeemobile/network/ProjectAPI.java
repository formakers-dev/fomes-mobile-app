package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.Project;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

public interface ProjectAPI {
    @GET("/project")
    Observable<List<Project>> getAllProjects(@Header("x-access-token") String accessToken);

    @GET("/project")
    Observable<Project> getProject(@Header("x-access-token") String accessToken, @Query("projectId") String projectId);
}