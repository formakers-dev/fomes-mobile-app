package com.appbee.appbeemobile.adapter;

import android.content.Context;
import android.os.Build;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class RegisteredInterviewListAdapter extends RecyclerView.Adapter<RegisteredInterviewItemViewHolder> {

    private final List<Project> projectList;
    private final TimeHelper timeHelper;
    private final MyInterviewActivity.ActionListener listener;
    private Context context;
    private int skyBlueColorId;
    private int lightGrayColorId;

    @Inject
    public RegisteredInterviewListAdapter(List<Project> projectList, TimeHelper timeHelper, MyInterviewActivity.ActionListener listener) {
        this.projectList = projectList;
        this.timeHelper = timeHelper;
        this.listener = listener;
    }

    @Override
    public RegisteredInterviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        extractColorResId();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_interview, parent, false);
        return new RegisteredInterviewItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RegisteredInterviewItemViewHolder holder, int position) {
        Project project = projectList.get(position);
        final String interviewStatus = getInterviewStatus(project);

        bindInterviewSummary(holder, project);
        bindInterviewProgressFlow(holder, project, interviewStatus);
        bindLocationAndEmergencyPhone(holder, project);
        bindButtonListener(holder, project, interviewStatus);
    }

    private void bindInterviewSummary(RegisteredInterviewItemViewHolder holder, Project project) {
        holder.interviewNameTextView.setText(String.format(context.getString(R.string.registered_interview_formated_title), project.getName()));

        int dDay = DateUtil.calDateDiff(timeHelper.getCurrentTime(), project.getInterview().getInterviewDate().getTime());
        holder.interviewDDayTextView.setText(String.format(context.getString(R.string.d_day_text), dDay));
    }

    private void bindInterviewProgressFlow(RegisteredInterviewItemViewHolder holder, Project project, String interviewStatus) {
        if ("신청".equals(interviewStatus)) {
            holder.interviewOpenDateTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenOpenCloseDateView.setBackgroundColor(lightGrayColorId);
            holder.interviewCloseDateTextView.setTextColor(lightGrayColorId);
            holder.lineBetweenCloseInterviewDateView.setBackgroundColor(lightGrayColorId);
            holder.interviewDateTextView.setTextColor(lightGrayColorId);
        } else if ("확정".equals(interviewStatus)) {
            holder.interviewOpenDateTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenOpenCloseDateView.setBackgroundColor(skyBlueColorId);
            holder.interviewCloseDateTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenCloseInterviewDateView.setBackgroundColor(lightGrayColorId);
            holder.interviewDateTextView.setTextColor(lightGrayColorId);
        }

        holder.interviewDateLocationTextView.setText(String.format(context.getString(R.string.registered_interview_date_location), FormatUtil.toShortDateFormat(project.getInterview().getInterviewDate()), project.getInterview().getLocation(), Integer.parseInt(project.getInterview().getSelectedTimeSlot().substring(4))));
        holder.interviewOpenDateTextView.setText(String.format(context.getString(R.string.registered_interview_open_date), FormatUtil.toLongDateFormat(project.getInterview().getOpenDate())));
        holder.interviewCloseDateTextView.setText(String.format(context.getString(R.string.registered_interview_close_date), FormatUtil.toLongDateFormat(project.getInterview().getCloseDate())));
        holder.interviewDateTextView.setText(String.format(context.getString(R.string.registered_interview_complete_date), FormatUtil.toLongDateFormat(project.getInterview().getInterviewDate())));
    }

    private void bindLocationAndEmergencyPhone(RegisteredInterviewItemViewHolder holder, Project project) {
        holder.interviewLocation.setText(String.format(context.getString(R.string.registered_interview_location), project.getInterview().getLocation(), project.getInterview().getLocationDescription()));
        holder.emergencyPhone.setText(String.format(context.getString(R.string.registered_interview_emergency_phone), project.getInterview().getEmergencyPhone()));
    }

    private void bindButtonListener(RegisteredInterviewItemViewHolder holder, Project project, String interviewStatus) {
        holder.cancelInterviewButton.setOnClickListener(v -> listener.onRequestToCancelInterview(project.getProjectId(), project.getInterview().getSeq(), project.getInterview().getSelectedTimeSlot(), project.getName(), interviewStatus, project.getInterview().getInterviewDate(), project.getInterview().getLocation()));
        holder.showInterviewButton.setOnClickListener(v -> listener.onSelectProject(project.getProjectId()));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    private void extractColorResId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.skyBlueColorId = context.getResources().getColor(R.color.appbee_sky_blue, null);
            this.lightGrayColorId = context.getResources().getColor(R.color.appbee_light_gray, null);
        } else {
            this.skyBlueColorId = context.getResources().getColor(R.color.appbee_sky_blue);
            this.lightGrayColorId = context.getResources().getColor(R.color.appbee_light_gray);
        }
    }

    private String getInterviewStatus(Project project) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeHelper.getCurrentTime());
        Date currentDate = calendar.getTime();

        Date openDate = project.getInterview().getOpenDate();
        Date closeDate = project.getInterview().getCloseDate();

        calendar.setTimeInMillis(project.getInterview().getInterviewDate().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(project.getInterview().getSelectedTimeSlot().substring(4)));
        Date interviewDate = calendar.getTime();

        String interviewStatus = "";
        if (openDate.compareTo(currentDate) < 0 && currentDate.compareTo(closeDate) < 0) {
            interviewStatus = "신청";
        } else if (closeDate.compareTo(currentDate) < 0 && currentDate.compareTo(interviewDate) < 0) {
            interviewStatus = "확정";
        }
        return interviewStatus;
    }
}
