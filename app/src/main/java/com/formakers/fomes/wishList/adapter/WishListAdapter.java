package com.formakers.fomes.wishList.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.model.AppInfo;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppInfo> wishList;

    public WishListAdapter(List<AppInfo> wishList) {
        this.wishList = wishList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wish_list_app, parent, false);
        return new WishListAdapter.AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AppInfo wishListApp = wishList.get(position);

        AppViewHolder viewHolder = (AppViewHolder) holder;
        viewHolder.wishListAppItemView.bindAppInfo(wishListApp);
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        RecommendAppItemView wishListAppItemView;

        AppViewHolder(View itemView) {
            super(itemView);
            wishListAppItemView = itemView.findViewById(R.id.wish_list_app_item_view);
        }
    }
}
