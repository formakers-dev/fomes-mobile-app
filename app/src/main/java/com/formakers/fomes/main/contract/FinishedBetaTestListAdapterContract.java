package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.view.adapter.listener.OnRecyclerItemClickListener;

import java.util.List;

public interface FinishedBetaTestListAdapterContract {
    interface View {
        void setPresenter(FinishedBetaTestContract.Presenter presenter);

        void setOnItemClickListener(OnRecyclerItemClickListener listener);

        void notifyDataSetChanged();
    }

    interface Model {
        int getItemCount();
        Object getItem(int position);
        List<BetaTest> getAllItems();
        void add(BetaTest item);
        void addAll(List<BetaTest> items);
        void clear();
    }
}
