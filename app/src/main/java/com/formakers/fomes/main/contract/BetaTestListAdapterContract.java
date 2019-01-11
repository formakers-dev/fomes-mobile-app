package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.network.vo.BetaTestRequest;
import com.formakers.fomes.common.view.adapter.listener.OnRecyclerItemClickListener;

import java.util.List;

public interface BetaTestListAdapterContract {
    interface View {
        void setPresenter(BetaTestContract.Presenter presenter);

        void setOnItemClickListener(OnRecyclerItemClickListener listener);

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
