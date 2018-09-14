package com.formakers.fomes.main.contract;

import com.formakers.fomes.model.AppInfo;

import java.util.List;

public interface RecommendListAdapterContract {
    interface View {
        void setPresenter(RecommendContract.Presenter presenter);
    }

    interface Model {
        int getItemCount();
        Object getItem(int position);
        void add(AppInfo appInfo);
        void addAll(List<AppInfo> appInfos);
        void clear();
    }
}
