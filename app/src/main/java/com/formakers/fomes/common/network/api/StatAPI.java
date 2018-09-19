package com.formakers.fomes.common.network.api;

import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.model.ShortTermStat;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface StatAPI {
    interface PeopleGroupFilter {
        String GENDER_AND_AGE = "GENDER_AND_AGE";
        String JOB = "JOB";
    }

    @POST("/stats/short")
    Observable<Void> sendShortTermStats(@Header("x-access-token") String accessToken, @Body List<ShortTermStat> shortTermStats);

    @POST("/stats/usages/app")
    Observable<Void> postUsages(@Header("x-access-token") String accessToken, @Body List<AppUsage> appUsages);

    @GET("/stats/usages/app/category/{categoryId}")
    Observable<List<AppUsage>> getAppUsageByCategory(@Header("x-access-token") String accessToken, @Path("categoryId") String categoryId);

    @GET("/stats/usages/category")
    Observable<List<CategoryUsage>> getCategoryUsage(@Header("x-access-token") String accessToken);

    @GET("/stats/usages/category")
    Observable<List<CategoryUsage>> getCategoryUsage(@Header("x-access-token") String accessToken, @Query("peopleGroup") String peopleGroup);
}