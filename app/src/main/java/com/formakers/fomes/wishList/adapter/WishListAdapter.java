package com.formakers.fomes.wishList.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.wishList.contract.WishListContract;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppInfo> wishList;

    private WishListContract.Presenter presenter;
    private Context context;

    public WishListAdapter(List<AppInfo> wishList) {
        this.wishList = wishList;
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
            // 이 경우 wish button이 삭제 버튼의 역할을 하기 떄문
            this.presenter.requestRemoveFromWishList(wishListApp.getPackageName());
        });
        viewHolder.wishListAppItemView.setOnDownloadButtonClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + wishListApp.getPackageName()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public void setPresenter(WishListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void removeApp(String packageName) {
        for (int position = 0; position < getItemCount(); position++) {
            if (packageName.equals(wishList.get(position).getPackageName())) {
                wishList.remove(position);
                notifyDataSetChanged();
                break;
            }
        }

        if(getItemCount() == 0) {
            presenter.emitShowEmptyList();
        }
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        RecommendAppItemView wishListAppItemView;

        AppViewHolder(View itemView) {
            super(itemView);
            wishListAppItemView = itemView.findViewById(R.id.wish_list_app_item_view);
        }
    }
}
