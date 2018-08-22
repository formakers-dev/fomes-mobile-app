package com.formakers.fomes.network;

import com.formakers.fomes.model.ShortTermStat;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface StatAPI {
    @POST("/stats/short")
    Observable<Void> sendShortTermStats(@Header("x-access-token") String accessToken, @Body List<ShortTermStat> shortTermStats);
}