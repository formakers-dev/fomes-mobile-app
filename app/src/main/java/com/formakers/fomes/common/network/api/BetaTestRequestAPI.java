package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.BetaTestRequest;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface BetaTestRequestAPI {
    @GET("/requests")
    Observable<List<BetaTestRequest>> getRequests(@Header("x-access-token") String accessToken);
}
