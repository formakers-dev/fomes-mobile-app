package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.model.ShortTermStat;
import com.formakers.fomes.model.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface StatAPI {

    @POST("/stats/short")
    Observable<Void> sendShortTermStats(@Header("x-access-token") String accessToken, @Body List<ShortTermStat> shortTermStats);

    @POST("/stats/usages/app")
    Observable<Void> postUsages(@Header("x-access-token") String accessToken, @Body List<AppUsage> appUsages);

    @GET("/stats/usages/app/category/{categoryId}")
    Observable<List<AppUsage>> getAppUsageByCategory(@Header("x-access-token") String accessToken, @Path("categoryId") String categoryId);

    @GET("/stats/usages/category")
    Observable<List<CategoryUsage>> getCategoryUsage(@Header("x-access-token") String accessToken);

    @GET("/stats/usages/category")
    Observable<List<CategoryUsage>> getCategoryUsage(@Header("x-access-token") String accessToken, @Query("peopleGroup") int peopleGroup);

    @POST("/stats/report/category/{categoryId}/recent")
    Observable<RecentReport> getRecentReport(@Header("x-access-token") String accessToken, @Path("categoryId") String categoryId, @Body User user);
}