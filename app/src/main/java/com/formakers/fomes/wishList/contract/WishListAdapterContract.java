package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.network.vo.RecommendApp;

import java.util.List;

public interface RecommendListAdapterContract {
    interface View {
        void setPresenter(RecommendContract.Presenter presenter);

        void notifyItemChanged(String pacakgeName);
        void notifyDataSetChanged();
    }

    interface Model {
        int getItemCount();
        Object getItem(int position);
        List<RecommendApp> getAllItems();
        void add(RecommendApp item);
        void addAll(List<RecommendApp> items);
        void clear();

        // RecommendListAdapter
        void updateWishedStatus(String packgeName, boolean wishedByMe);
    }
}
