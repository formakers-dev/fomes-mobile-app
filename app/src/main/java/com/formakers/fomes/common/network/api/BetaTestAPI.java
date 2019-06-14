package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface BetaTestAPI {
    @GET("/beta-tests")
    Observable<List<BetaTest>> getBetaTests(@Header("x-access-token") String accessToken);

    @GET("/beta-tests/finished")
    Observable<List<BetaTest>> getFinishedBetaTests(@Header("x-access-token") String accessToken);
}
