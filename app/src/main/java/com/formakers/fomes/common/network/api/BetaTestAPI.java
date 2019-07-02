package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface BetaTestAPI {
    @GET("/beta-tests")
    Observable<List<BetaTest>> getBetaTests(@Header("x-access-token") String accessToken);

    @GET("/beta-tests/finished")
    Observable<List<BetaTest>> getFinishedBetaTests(@Header("x-access-token") String accessToken);

    @GET("/beta-tests/detail/{id}")
    Observable<BetaTest> getDetailBetaTest(@Header("x-access-token") String accessToken, @Path("id") String id);

    @POST("/beta-tests/{id}/complete")
    Observable<Void> postCompleteBetaTest(@Header("x-access-token") String accessToken, @Path("id") String id);
}
