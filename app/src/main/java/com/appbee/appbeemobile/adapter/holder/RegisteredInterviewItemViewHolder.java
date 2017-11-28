package com.appbee.appbeemobile.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class RegisteredInterviewItemViewHolder extends RecyclerView.ViewHolder {
    public TextView interviewNameTextView;
    public TextView interviewDDayTextView;
    public TextView interviewDateLocationTextView;
    public TextView interviewOpenDateTextView;
    public TextView interviewCloseDateTextView;
    public TextView interviewDateTextView;
    public TextView interviewLocation;
    public TextView emergencyPhone;

    public RegisteredInterviewItemViewHolder(View view) {
        super(view);
        this.interviewNameTextView = (TextView) view.findViewById(R.id.interview_name);
        this.interviewDDayTextView = (TextView) view.findViewById(R.id.interview_d_day);
        this.interviewDateLocationTextView = (TextView) view.findViewById(R.id.interview_date_location);
        this.interviewOpenDateTextView = (TextView) view.findViewById(R.id.interview_open_date);
        this.interviewCloseDateTextView = (TextView) view.findViewById(R.id.interview_close_date);
        this.interviewDateTextView = (TextView) view.findViewById(R.id.interview_date);
        this.interviewLocation = (TextView) view.findViewById(R.id.interview_location);
        this.emergencyPhone = (TextView) view.findViewById(R.id.emergency_phone);
    }
}
