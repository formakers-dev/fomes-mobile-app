package com.formakers.fomes.betatest;

import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;

import java.util.List;

public interface BetaTestListAdapterContract {
    interface View {
        void setPresenter(BetaTestContract.Presenter presenter);

        void setOnItemClickListener(OnRecyclerItemClickListener listener);

        void notifyDataSetChanged();
        void notifyItemChanged(int position);
    }

    interface Model {
        int getPositionById(String id);

        int getItemCount();
        Object getItem(int position);
        List<BetaTest> getAllItems();
        void add(BetaTest item);
        void addAll(List<BetaTest> items);
        void clear();
    }
}
