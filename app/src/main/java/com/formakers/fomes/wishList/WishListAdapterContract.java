package com.formakers.fomes.wishList;

import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;
import com.formakers.fomes.common.model.AppInfo;

import java.util.List;

public interface WishListAdapterContract {
    interface View {
        void refresh();
        void refresh(int position);

        void setOnWishCheckedChangeListener(OnRecyclerItemClickListener listener);
        void setOnDownloadButtonClickListener(OnRecyclerItemClickListener listener);
    }

    interface Model {
        int getItemCount();
        AppInfo getItem(int position);
        List<AppInfo> getAllItems();
        void add(AppInfo item);
        void addAll(List<AppInfo> items);
        void remove(int positon);
        void clear();
    }
}
