package com.appbee.appbeemobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.holder.DetailPlansHolder;

import java.util.List;
import java.util.Locale;


public class DetailPlansAdapter extends RecyclerView.Adapter<DetailPlansHolder> {

    private final List<String> timeSlots;

    private String selectedTimeSlot;

    public DetailPlansAdapter(List<String> timeSlots) {
        this.timeSlots = timeSlots;
    }

    @Override
    public DetailPlansHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_button, parent, false);
        return new DetailPlansHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DetailPlansHolder holder, int position) {
        String timeSlot = timeSlots.get(position);
        holder.button.setText(String.format(Locale.KOREA, "%02d:00", Integer.parseInt(timeSlot.substring(4))));
        holder.button.setOnClickListener(v -> {
            selectedTimeSlot = timeSlot;
            notifyItemRangeChanged(0, position);
            notifyItemRangeChanged(position + 1, timeSlots.size() - position - 1);
        });

        holder.button.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public String getItem(int position) {
        return timeSlots.get(position);
    }

    public void setSelectedTimeSlot(int postion) {
        this.selectedTimeSlot = timeSlots.get(postion);
    }

    public String getSelectedTimeSlot() {
        return selectedTimeSlot;
    }
}
