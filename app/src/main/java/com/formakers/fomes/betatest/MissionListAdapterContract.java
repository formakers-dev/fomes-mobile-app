package com.formakers.fomes.betatest;

import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;

import java.util.List;

public interface MissionListAdapterContract {
    interface View {
        void setPresenter(BetaTestDetailContract.Presenter presenter);

        void setOnItemClickListener(OnRecyclerItemClickListener listener);

        void clickRefreshButton(String missionId);

        void notifyDataSetChanged();
        void notifyItemChanged(int position);
        void notifyItemBelowAllChanged(int position);
    }

    interface Model {
        void setLoading(String missionId, boolean isLoading);

        int getPositionById(String missionId);

        int getItemCount();
        Mission getItem(int position);
        Mission getItemById(String missionId);
        List<Mission> getAllItems();
        void add(Mission item);
        void addAll(List<Mission> items);
        void clear();
    }
}
