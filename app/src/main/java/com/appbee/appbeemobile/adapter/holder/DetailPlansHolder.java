package com.appbee.appbeemobile.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class DetailPlansHolder extends RecyclerView.ViewHolder {
    public TextView button;

    public DetailPlansHolder(View view) {
        super(view);

        button = (TextView) view.findViewById(R.id.time_slot_button);
    }
}