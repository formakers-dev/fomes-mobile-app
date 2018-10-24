package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.RecommendApp;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

public interface RecommendAPI {

    @GET("/recommend/similar/demographic")
    Observable<List<RecommendApp>> getSimilarAppsByDemographic(@Header("x-access-token") String accessToken, @Query("page") int page, @Query("limit") int limit);

}
