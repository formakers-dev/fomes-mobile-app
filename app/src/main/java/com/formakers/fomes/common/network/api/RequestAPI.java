package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.FeedbackRequest;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface RequestAPI {
    @GET("/requests")
    Observable<List<FeedbackRequest>> getRequests(@Header("x-access-token") String accessToken);
}
