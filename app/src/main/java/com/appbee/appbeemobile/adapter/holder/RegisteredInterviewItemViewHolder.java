package com.appbee.appbeemobile.adapter.holder;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class RegisteredInterviewItemViewHolder extends RecyclerView.ViewHolder {
    public TextView interviewNameTextView;
    public TextView interviewDDayTextView;
    public TextView interviewDateLocationTextView;
    public TextView interviewOpenDateTextView;
    public TextView interviewCloseDateTextView;
    public TextView interviewDateTextView;
    public TextView interviewOpenTitleTextView;
    public TextView interviewCloseTitleTextView;
    public TextView interviewDateTitleTextView;
    public TextView interviewLocation;
    public TextView emergencyPhone;
    public TextView cancelInterviewTextView;
    public Button showInterviewButton;
    public View lineBetweenOpenCloseDateView;
    public View lineBetweenCloseInterviewDateView;

    public RegisteredInterviewItemViewHolder(View view) {
        super(view);
        this.interviewNameTextView = (TextView) view.findViewById(R.id.interview_name);
        this.interviewDDayTextView = (TextView) view.findViewById(R.id.interview_d_day);
        this.interviewDateLocationTextView = (TextView) view.findViewById(R.id.interview_date_location);
        this.interviewOpenDateTextView = (TextView) view.findViewById(R.id.interview_progress_open_date);
        this.interviewCloseDateTextView = (TextView) view.findViewById(R.id.interview_progress_close_date);
        this.interviewDateTextView = (TextView) view.findViewById(R.id.interview_progress_date);
        this.interviewOpenTitleTextView = (TextView) view.findViewById(R.id.interview_progress_open_title);
        this.interviewCloseTitleTextView = (TextView) view.findViewById(R.id.interview_progress_close_title);
        this.interviewDateTitleTextView = (TextView) view.findViewById(R.id.interview_progress_date_title);
        this.interviewLocation = (TextView) view.findViewById(R.id.interview_location);
        this.emergencyPhone = (TextView) view.findViewById(R.id.emergency_phone);
        this.showInterviewButton = ((Button) view.findViewById(R.id.show_interview_button));
        this.cancelInterviewTextView = ((TextView) view.findViewById(R.id.interview_cancel));
        this.cancelInterviewTextView.setPaintFlags(this.cancelInterviewTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.lineBetweenOpenCloseDateView = view.findViewById(R.id.line_between_open_close_date);
        this.lineBetweenCloseInterviewDateView = view.findViewById(R.id.line_between_close_interview_date);
    }
}
