package com.formakers.fomes.common.view.custom.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.R;

import java.util.List;

public class MenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @LayoutRes private int itemLayoutResId;

    List<MenuItem> menuList;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public MenuListAdapter(List<MenuItem> list, @LayoutRes int itemLayoutResId, OnItemClickListener onItemClickListener) {
        this.menuList = list;
        this.onItemClickListener = onItemClickListener;
        this.itemLayoutResId = itemLayoutResId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(itemLayoutResId, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(menuList.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public MenuItem getItem(int position) {
        return menuList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView sideInfoTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.icon);
            titleTextView = itemView.findViewById(R.id.title);
            sideInfoTextView = itemView.findViewById(R.id.side_info);
        }

        public void bind(MenuItem item, OnItemClickListener listener) {
            if (item.getIconImageDrawable() != null) {
                iconImageView.setImageDrawable(item.getIconImageDrawable());
                iconImageView.setVisibility(View.VISIBLE);
            }
            titleTextView.setText(item.getTitle());
            sideInfoTextView.setText(item.getSideInfo());
            itemView.setOnClickListener(v -> listener.onItemClick(item));
            itemView.setClickable(item.isClickable());
            itemView.setTag(item.getId());
        }
    }

    public static class MenuItem {
        int id;
        Drawable iconImageDrawable;
        String title;
        String sideInfo;
        boolean isClickable = true;

        public MenuItem(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Drawable getIconImageDrawable() {
            return iconImageDrawable;
        }

        public MenuItem setIconImageDrawable(Drawable iconImageDrawable) {
            this.iconImageDrawable = iconImageDrawable;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public MenuItem setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getSideInfo() {
            return sideInfo;
        }

        public MenuItem setSideInfo(String sideInfo) {
            this.sideInfo = sideInfo;
            return this;
        }

        public boolean isClickable() {
            return isClickable;
        }

        public MenuItem setClickable(boolean clickable) {
            isClickable = clickable;
            return this;
        }

        @Override
        public String toString() {
            return "MenuItem{" +
                    "id='" + id + '\'' +
                    ", iconImageDrawable=" + iconImageDrawable +
                    ", title='" + title + '\'' +
                    ", sideInfo='" + sideInfo + '\'' +
                    ", isClickable=" + isClickable +
                    '}';
        }
    }
}