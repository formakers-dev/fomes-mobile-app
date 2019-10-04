package com.formakers.fomes.wishList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.model.AppInfo;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements WishListAdapterContract.View, WishListAdapterContract.Model {

    private List<AppInfo> wishList = new ArrayList<>();

    private OnRecyclerItemClickListener wishedCheckedListener;
    private OnRecyclerItemClickListener downloadClickListener;

    private Context context;

    private WishListContract.Presenter presenter;

    public WishListAdapter(WishListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wish_list_app, parent, false);
        return new WishListAdapter.AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AppInfo wishApp = wishList.get(position);

        AppViewHolder viewHolder = (AppViewHolder) holder;

        this.presenter.getImageLoader().loadImage(viewHolder.iconImageView, wishApp.getIconUrl(),
                new RequestOptions().override(70, 70)
                        .centerCrop()
                        .transform(new RoundedCorners(10))
                , false);

        viewHolder.appNameTextView.setText(wishApp.getAppName());

        String genre = wishApp.getCategoryName();
        String developer = wishApp.getDeveloper();
        viewHolder.genreDeveloperTextView.setText(String.format("%s / %s", TextUtils.isEmpty(genre) ? "" : genre, TextUtils.isEmpty(developer) ? "" : developer));

        viewHolder.wishListToggleButton.setChecked(wishApp.isWished());

        viewHolder.wishListToggleButton.setOnCheckedChangeListener((v, isChecked) -> {
            if (!isChecked) {
                wishedCheckedListener.onItemClick(position);
            }
        });

        viewHolder.downloadButton.setOnClickListener(v -> downloadClickListener.onItemClick(position));
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
        ImageView iconImageView;
        ToggleButton wishListToggleButton;
        Button downloadButton;
        TextView appNameTextView;
        TextView genreDeveloperTextView;

        AppViewHolder(View itemView) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.item_app_icon_imageview);
            wishListToggleButton = itemView.findViewById(R.id.app_info_wishlist_button);
            downloadButton = itemView.findViewById(R.id.app_info_download_button);
            appNameTextView = itemView.findViewById(R.id.item_app_name_textview);
            genreDeveloperTextView = itemView.findViewById(R.id.item_app_genre_developer_textview);
        }
    }
}
