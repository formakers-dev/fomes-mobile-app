package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.common.model.AppUsage;
import com.formakers.fomes.common.model.ShortTermStat;
import com.formakers.fomes.common.model.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface StatAPI {

    @POST("/stats/short")
    Observable<Void> postShortTermStats(@Header("x-access-token") String accessToken, @Body List<ShortTermStat> shortTermStats);

    @POST("/stats/usages/app")
    Observable<Void> postUsages(@Header("x-access-token") String accessToken, @Body List<AppUsage> appUsages);

    /**
     *
     * @param accessToken user's appbee token for auth
     * @param categoryId app category Id
     * @param user (mandatory) user.job, user.birthday, user.gender
     * @return the report of user's recent usages
     */
    @POST("/stats/report/category/{categoryId}/recent")
    Observable<RecentReport> getRecentReport(@Header("x-access-token") String accessToken, @Path("categoryId") String categoryId, @Body User user);
}