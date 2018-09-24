package com.formakers.fomes.appbee.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.R;

public class ContentsListHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView titleTextView;
    TextView subtitleTextView;

    public ContentsListHeaderViewHolder(View view) {
        super(view);

        titleTextView = (TextView) view.findViewById(R.id.item_header_title);
        subtitleTextView = (TextView) view.findViewById(R.id.item_header_subtitle);
    }
}
