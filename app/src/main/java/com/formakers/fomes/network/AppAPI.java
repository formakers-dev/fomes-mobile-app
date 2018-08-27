package com.formakers.fomes.network;

import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.CategoryUsage;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface AppAPI {
    @POST("/apps/usages")
    Observable<Void> postUsages(@Header("x-access-token") String accessToken, @Body List<AppUsage> appUsages);

    @GET("/apps/usages/category/{categoryId}")
    Observable<List<AppUsage>> getAppUsageByCategory(@Header("x-access-token") String accessToken, @Path("categoryId") String categoryId);

    @GET("/apps/usages/rank/category")
    Observable<List<CategoryUsage>> getCategoryUsage(@Header("x-access-token") String accessToken);
}