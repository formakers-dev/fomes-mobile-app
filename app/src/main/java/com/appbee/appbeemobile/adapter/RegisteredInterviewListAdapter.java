package com.appbee.appbeemobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;
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

    @Inject
    public RegisteredInterviewListAdapter(List<Project> projectList, TimeHelper timeHelper) {
        this.projectList = projectList;
        this.timeHelper = timeHelper;
    }

    @Override
    public RegisteredInterviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.interviewNameTextView.setText(String.format("%s 유저 인터뷰", project.getName()));

        int dDay = DateUtil.calDateDiff(timeHelper.getCurrentTime(), project.getInterview().getInterviewDate().getTime());
        holder.interviewDDayTextView.setText(String.format("D-%d", dDay));
    }

    private void bindInterviewProgressFlow(RegisteredInterviewItemViewHolder holder, Project project) {
        String interviewDateString = FormatUtil.toShortDateFormat(project.getInterview().getInterviewDate());
        String interviewTimeString = project.getInterview().getSelectedTimeSlot().substring(4);
        if(interviewTimeString.length() == 1) {
            interviewTimeString = "0" + interviewTimeString;
        }

        holder.interviewDateLocationTextView.setText(String.format("%s %s %s:00", interviewDateString, project.getInterview().getLocation(), interviewTimeString));
        holder.interviewOpenDateTextView.setText(String.format("신청\n%s", FormatUtil.toLongDateFormat(project.getInterview().getOpenDate())));
        holder.interviewCloseDateTextView.setText(String.format("확정\n%s", FormatUtil.toLongDateFormat(project.getInterview().getCloseDate())));
        holder.interviewDateTextView.setText(String.format("완료\n%s", FormatUtil.toLongDateFormat(project.getInterview().getInterviewDate())));
    }

    private void bindLocationAndEmergencyPhone(RegisteredInterviewItemViewHolder holder, Project project) {
        holder.interviewLocation.setText(String.format("* 인터뷰 위치 : %s %s", project.getInterview().getLocation(), project.getInterview().getLocationDescription()));
        holder.emergencyPhone.setText(String.format("* 비상연락처 : %s", project.getInterview().getEmergencyPhone()));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}
