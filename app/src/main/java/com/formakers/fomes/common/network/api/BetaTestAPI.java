package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface BetaTestAPI {
    @GET("/beta-tests")
    Observable<List<BetaTest>> getBetaTests(@Header("x-access-token") String accessToken);

    class BetaTestProgressResponseVO {
        public boolean isAttended;
        public boolean isCompleted;
    }

    @GET("/beta-tests/{id}/progress")
    Observable<BetaTestProgressResponseVO> getBetaTestProgress(@Header("x-access-token") String accessToken, @Path("id") String betaTestId, @Query("verbose") Boolean isVerbose);

    @GET("/beta-tests/finished")
    Observable<List<BetaTest>> getFinishedBetaTests(@Header("x-access-token") String accessToken, @Query("verbose") Boolean isVerbose);

    @GET("/beta-tests/detail/{id}")
    Observable<BetaTest> getDetailBetaTest(@Header("x-access-token") String accessToken, @Path("id") String betaTestId);

    @GET("/beta-tests/{id}/missions/{missionId}/progress")
    Observable<Mission> getMissionProgress(@Header("x-access-token") String accessToken, @Path("id") String betaTestId, @Path("missionId") String missionId);

    @POST("/beta-tests/{id}/missions/{missionId}/complete")
    Observable<Void> postCompleteMission(@Header("x-access-token") String accessToken, @Path("id") String betaTestId, @Path("missionId") String missionId);

    @POST("/beta-tests/{id}/complete")
    Observable<Void> postCompleteBetaTest(@Header("x-access-token") String accessToken, @Path("id") String id);

    @POST("/beta-tests/{id}/attend")
    Observable<Void> postAttend(@Header("x-access-token") String accessToken, @Path("id") String betaTestId);
}
