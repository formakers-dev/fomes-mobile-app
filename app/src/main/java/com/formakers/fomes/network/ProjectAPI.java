package com.formakers.fomes.network;

import com.formakers.fomes.model.Project;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface ProjectAPI {
    @GET("/projects")
    Observable<List<Project>> getAllProjects(@Header("x-access-token") String accessToken);

    @GET("/projects/{id}")
    Observable<Project> getProject(@Header("x-access-token") String accessToken, @Path("id") String projectId);

    @GET("/projects/match/interviews")
    Observable<List<Project>> getAllInterviews(@Header("x-access-token") String accessToken);

    @GET("/projects/registered/interviews")
    Observable<List<Project>> getRegisteredInterviews(@Header("x-access-token") String accessToken);

    @GET("/projects/{id}/interviews/{seq}")
    Observable<Project> getInterview(@Header("x-access-token") String accessToken, @Path("id") String projectId, @Path("seq") long seq);

    @POST("/projects/{id}/interviews/{seq}/participate/{slotId}")
    Observable<Void> postParticipate(@Header("x-access-token") String accessToken, @Path("id") String projectId, @Path("seq") long seq, @Path("slotId") String slotId);

    @POST("/projects/{id}/interviews/{seq}/cancel/{slotId}")
    Observable<Void> postCancelParticipate(@Header("x-access-token") String accessToken, @Path("id") String projectId, @Path("seq") long seq, @Path("slotId") String slotId);
}