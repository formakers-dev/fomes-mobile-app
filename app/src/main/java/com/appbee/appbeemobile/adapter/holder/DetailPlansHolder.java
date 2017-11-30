package com.appbee.appbeemobile.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;

import com.appbee.appbeemobile.R;

public class DetailPlansHolder extends RecyclerView.ViewHolder {
    public RadioButton button;

    public DetailPlansHolder(View view) {
        super(view);

        button = (RadioButton) view.findViewById(R.id.time_slot_button);
    }
}