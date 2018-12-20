package com.formakers.fomes.wishList.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.adapter.listener.OnRecyclerItemClickListener;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.wishList.contract.WishListAdapterContract;

import java.util.ArrayList;
import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements WishListAdapterContract.View, WishListAdapterContract.Model {

    private List<AppInfo> wishList = new ArrayList<>();

    private OnRecyclerItemClickListener wishedCheckedListener;
    private OnRecyclerItemClickListener downloadClickListener;

    private Context context;

    public WishListAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wish_list_app, parent, false);
        return new WishListAdapter.AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AppInfo wishListApp = wishList.get(position);

        AppViewHolder viewHolder = (AppViewHolder) holder;
        viewHolder.wishListAppItemView.bindAppInfo(wishListApp);
        viewHolder.wishListAppItemView.setOnWishListCheckedChangeListener((v, isChecked) -> {
            if (!isChecked) {
                wishedCheckedListener.onItemClick(position);
            }
        });
        viewHolder.wishListAppItemView.setOnDownloadButtonClickListener(v -> downloadClickListener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return wishList.get(position);
    }

    @Override
    public List<AppInfo> getAllItems() {
        return wishList;
    }

    @Override
    public void add(AppInfo item) {
        wishList.add(item);
    }

    @Override
    public void addAll(List<AppInfo> items) {
        wishList.addAll(items);
    }

    @Override
    public void remove(int position) {
        wishList.remove(position);
    }

    @Override
    public void clear() {
        wishList.clear();
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void refresh(int position) {
        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, getItemCount() - position);
        notifyDataSetChanged();
    }

    @Override
    public void setOnWishCheckedChangeListener(OnRecyclerItemClickListener listener) {
        this.wishedCheckedListener = listener;
    }

    @Override
    public void setOnDownloadButtonClickListener(OnRecyclerItemClickListener listener) {
        this.downloadClickListener = listener;
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        RecommendAppItemView wishListAppItemView;

        AppViewHolder(View itemView) {
            super(itemView);
            wishListAppItemView = itemView.findViewById(R.id.wish_list_app_item_view);
        }
    }
}
