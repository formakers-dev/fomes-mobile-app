package com.formakers.fomes.common.network.api;

import com.formakers.fomes.common.network.vo.Post;

import java.util.List;

import retrofit2.http.GET;
import rx.Single;

public interface PostAPI {
    @GET("/posts")
    Single<List<Post>> getPromotions();
}
