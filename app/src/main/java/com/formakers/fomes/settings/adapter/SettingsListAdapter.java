package com.formakers.fomes.settings.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.settings.model.SettingsItem;

import java.util.List;

public class SettingsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<SettingsItem> settingsList;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(SettingsItem item);
    }

    public SettingsListAdapter(List<SettingsItem> list, OnItemClickListener onItemClickListener) {
        this.settingsList = list;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false);
        return new SettingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SettingsViewHolder) holder).bind(settingsList.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public SettingsItem getItem(int position) {
        return settingsList.get(position);
    }

    class SettingsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView sideInfoTextView;

        public SettingsViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title);
            sideInfoTextView = itemView.findViewById(R.id.side_info);
        }

        public void bind(SettingsItem item, OnItemClickListener listener) {
            titleTextView.setText(item.getTitle());
            sideInfoTextView.setText(item.getSideInfo());
            itemView.setOnClickListener(v -> listener.onItemClick(item));
            itemView.setClickable(item.isClickable());
            itemView.setTag(item.getId());
        }
    }
}