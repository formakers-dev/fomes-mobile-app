package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.Project;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface ProjectAPI {
    @GET("/projects")
    Observable<List<Project>> getAllProjects(@Header("x-access-token") String accessToken);

    @GET("/projects/{id}")
    Observable<Project> getProject(@Header("x-access-token") String accessToken, @Path("id") String projectId);
}