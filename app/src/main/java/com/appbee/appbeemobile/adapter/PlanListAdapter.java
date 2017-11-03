package com.appbee.appbeemobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.Project;

import java.util.List;

public class PlanListAdapter extends BaseAdapter {
    private List<Project.InterviewPlan> plans;

    public PlanListAdapter(List<Project.InterviewPlan> plans) {
        this.plans = plans;
    }

    @Override
    public int getCount() {
        return plans.size();
    }

    @Override
    public Project.InterviewPlan getItem(int position) {
        return plans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_list_item, null);

        bind(view, position);

        return view;
    }

    private void bind(View view, int position) {
        Project.InterviewPlan interviewPlan = getItem(position);
        TextView minuteTextView = (TextView) view.findViewById(R.id.minute);
        TextView planTextView = (TextView) view.findViewById(R.id.plan);

        minuteTextView.setText(String.format(view.getContext().getString(R.string.minute), interviewPlan.getMinute()));
        planTextView.setText(interviewPlan.getPlan());
    }
}
