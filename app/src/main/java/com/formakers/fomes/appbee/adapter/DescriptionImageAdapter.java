package com.formakers.fomes.appbee.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.formakers.fomes.R;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.model.Project;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

@Deprecated
public class DescriptionImageAdapter extends RecyclerView.Adapter<DescriptionImageAdapter.DescriptionImageHolder> {

    private List<Project.ImageObject> imageList;
    private ImageLoader imageLoader;

    public DescriptionImageAdapter(List<Project.ImageObject> imageList, ImageLoader imageLoader) {
        this.imageList = imageList;
        this.imageLoader = imageLoader;
    }

    @Override
    public DescriptionImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_description_image, parent, false);
        return new DescriptionImageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DescriptionImageHolder holder, int position) {

        ImageView imageView = holder.imageView;
        imageLoader.loadImage(imageView, imageList.get(position).getUrl(), new RequestOptions().override(1300, 1000));
        imageView.setTag(R.string.tag_key_image_url, imageList.get(position).getUrl());

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class DescriptionImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        DescriptionImageHolder(View itemView) {
            super(itemView);
            imageView = ((ImageView) itemView.findViewById(R.id.description_image));
        }
    }
}
