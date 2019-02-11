package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.PostAPI;
import com.formakers.fomes.common.network.vo.Post;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class PostService {

    private PostAPI postAPI;

    @Inject
    public PostService(PostAPI postAPI) {
        this.postAPI = postAPI;
    }

    public Single<List<Post>> getPromotions() {
        return postAPI.getPromotions()
                .subscribeOn(Schedulers.io());
    }
}
