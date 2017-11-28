package com.appbee.appbeemobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.MyInterviewActivity;
import com.appbee.appbeemobile.adapter.holder.RegisteredInterviewItemViewHolder;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.util.DateUtil;
import com.appbee.appbeemobile.util.FormatUtil;

import java.util.List;

import javax.inject.Inject;

public class RegisteredInterviewListAdapter extends RecyclerView.Adapter<RegisteredInterviewItemViewHolder> {

    private final List<Project> projectList;
    private final TimeHelper timeHelper;
    private final MyInterviewActivity.ActionListener listener;
    private Context context;

    @Inject
    public RegisteredInterviewListAdapter(List<Project> projectList, TimeHelper timeHelper, MyInterviewActivity.ActionListener listener) {
        this.projectList = projectList;
        this.timeHelper = timeHelper;
        this.listener = listener;
    }

    @Override
    public RegisteredInterviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_interview, parent, false);
        return new RegisteredInterviewItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RegisteredInterviewItemViewHolder holder, int position) {
        Project project = projectList.get(position);

        bindInterviewSummary(holder, project);
        bindInterviewProgressFlow(holder, project);
        bindLocationAndEmergencyPhone(holder, project);
    }

    private void bindInterviewSummary(RegisteredInterviewItemViewHolder holder, Project project) {
        holder.interviewNameTextView.setText(String.format(context.getString(R.string.registered_interview_formated_title), project.getName()));

        int dDay = DateUtil.calDateDiff(timeHelper.getCurrentTime(), project.getInterview().getInterviewDate().getTime());
        holder.interviewDDayTextView.setText(String.format(context.getString(R.string.registered_interview_d_day), dDay));
    }

    private void bindInterviewProgressFlow(RegisteredInterviewItemViewHolder holder, Project project) {
        String interviewDateString = FormatUtil.toShortDateFormat(project.getInterview().getInterviewDate());
        String interviewTimeString = project.getInterview().getSelectedTimeSlot().substring(4);
        if(interviewTimeString.length() == 1) {
            interviewTimeString = "0" + interviewTimeString;
        }

        holder.interviewDateLocationTextView.setText(String.format(context.getString(R.string.registered_interview_date_location), interviewDateString, project.getInterview().getLocation(), interviewTimeString));
        holder.interviewOpenDateTextView.setText(String.format(context.getString(R.string.registered_interview_open_date), FormatUtil.toLongDateFormat(project.getInterview().getOpenDate())));
        holder.interviewCloseDateTextView.setText(String.format(context.getString(R.string.registered_interview_close_date), FormatUtil.toLongDateFormat(project.getInterview().getCloseDate())));
        holder.interviewDateTextView.setText(String.format(context.getString(R.string.registered_interview_complete_date), FormatUtil.toLongDateFormat(project.getInterview().getInterviewDate())));
    }

    private void bindLocationAndEmergencyPhone(RegisteredInterviewItemViewHolder holder, Project project) {
        holder.interviewLocation.setText(String.format(context.getString(R.string.registered_interview_location), project.getInterview().getLocation(), project.getInterview().getLocationDescription()));
        holder.emergencyPhone.setText(String.format(context.getString(R.string.registered_interview_emergency_phone), project.getInterview().getEmergencyPhone()));
        holder.showInterviewButton.setOnClickListener(v -> listener.onSelectProject(project.getProjectId()));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}
