package com.appbee.appbeemobile.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public static final int HEADER_TYPE_RECOMMENDATION = 0;
    public static final int HEADER_TYPE_CLAB = 1;

    TextView titleTextView;
    TextView subtitleTextView;

    public HeaderViewHolder(View view, int headerType) {
        super(view);

        titleTextView = (TextView) view.findViewById(R.id.recommendation_apps_title);
        subtitleTextView = (TextView) view.findViewById(R.id.recommendation_apps_subtitle);

        bind(headerType);
    }

    private void bind(int headerType) {
        if (headerType == 0) {
            titleTextView.setText(R.string.recommendation_apps_title);
            subtitleTextView.setText(R.string.recommendation_apps_subtitle);
        } else {
            titleTextView.setText(R.string.clab_title);
            subtitleTextView.setText(R.string.clab_subtitle);
        }
    }
}
