package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.RecommendApp;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RecommendAPI {

    @GET("/recommend/apps/{categoryId}")
    Observable<List<RecommendApp>> getRecommendApps(@Header("x-access-token") String accessToken, @Path("categoryId") String categoryId, @Query("page") int page, @Query("limit") int limit);

}
