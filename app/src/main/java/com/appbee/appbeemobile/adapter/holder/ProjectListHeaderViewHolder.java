package com.appbee.appbeemobile.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class ProjectListHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView titleTextView;
    TextView subtitleTextView;

    public ProjectListHeaderViewHolder(View view) {
        super(view);

        titleTextView = (TextView) view.findViewById(R.id.recommendation_apps_title);
        subtitleTextView = (TextView) view.findViewById(R.id.recommendation_apps_subtitle);
        titleTextView.setText(R.string.recommendation_apps_title);
        subtitleTextView.setText(R.string.recommendation_apps_subtitle);
    }
}
