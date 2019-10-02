package com.formakers.fomes.main;

import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.main.MainContract;

import java.util.List;

public interface EventPagerAdapterContract {
    interface View {
        void setPresenter(MainContract.Presenter presenter);
        void notifyDataSetChanged();
    }

    interface Model {
        void addAll(List<Post> promotions);
        int getCount();
    }
}