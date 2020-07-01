package com.formakers.fomes.common.network.api;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface PointAPI {
//    @GET("/points")
//    Observable<List<BetaTest>> getBetaTests(@Header("x-access-token") String accessToken);

    class PointResponseVO {
        public long point;

        public PointResponseVO(long point) {
            this.point = point;
        }
    }

    @GET("/points/available")
    Observable<PointResponseVO> getAvailablePoint(@Header("x-access-token") String accessToken);
}
