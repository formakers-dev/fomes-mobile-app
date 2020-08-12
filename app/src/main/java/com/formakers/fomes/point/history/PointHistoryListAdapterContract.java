package com.formakers.fomes.point.history;

import com.formakers.fomes.common.model.FomesPoint;

import java.util.List;

public interface PointHistoryListAdapterContract {
    interface View {
        void refresh();
        void refresh(int position);
    }

    interface Model {
        int getItemCount();
        FomesPoint getItem(int position);
        List<FomesPoint> getAllItems();
        void add(FomesPoint item);
        void addAll(List<FomesPoint> items);
        void remove(int positon);
        void clear();
    }
}
