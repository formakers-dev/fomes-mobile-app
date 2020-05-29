package com.formakers.fomes.common.view.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.R;
import com.formakers.fomes.common.helper.ImageLoader;

import java.util.List;

public class NetworkImageViewPagerAdapter extends RecyclerView.Adapter<NetworkImageViewPagerAdapter.ViewHolder> {

    ImageLoader imageLoader;
    List<String> imageUrlList;

    public NetworkImageViewPagerAdapter(ImageLoader imageLoader, List<String> imageUrlList) {
        this.imageLoader = imageLoader;
        this.imageUrlList = imageUrlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        this.imageLoader.loadImage(holder.imageView, imageUrlList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView;
        }
    }
}