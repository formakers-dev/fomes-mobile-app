package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.model.FomesPoint;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import rx.Observable;

public interface PointAPI {
    @GET("/points")
    Observable<List<FomesPoint>> getPointHistory(@Header("x-access-token") String accessToken);

    class PointResponseVO {
        public long point;

        public PointResponseVO(long point) {
            this.point = point;
        }
    }

    @GET("/points/available")
    Observable<PointResponseVO> getAvailablePoint(@Header("x-access-token") String accessToken);

    @PUT("/points/exchange")
    Observable<Void> putPointExchange(@Header("x-access-token") String accessToken, @Body FomesPoint point);

    @GET("/points/exchange/requested")
    Observable<PointResponseVO> getRequestedExchangePoint(@Header("x-access-token") String accessToken);
}
