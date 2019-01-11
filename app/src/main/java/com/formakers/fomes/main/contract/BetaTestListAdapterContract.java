package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.network.vo.BetaTestRequest;

import java.util.List;

public interface BetaTestListAdapterContract {
    interface View {
        void setPresenter(BetaTestContract.Presenter presenter);
        void notifyDataSetChanged();
    }

    interface Model {
        int getItemCount();
        Object getItem(int position);
        List<BetaTestRequest> getAllItems();
        void add(BetaTestRequest item);
        void addAll(List<BetaTestRequest> items);
        void clear();
    }
}
