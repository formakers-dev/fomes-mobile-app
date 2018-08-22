package com.formakers.fomes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.activity.MyInterviewActivity;
import com.formakers.fomes.adapter.holder.RegisteredInterviewItemViewHolder;
import com.formakers.fomes.helper.ResourceHelper;
import com.formakers.fomes.helper.TimeHelper;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.util.DateUtil;
import com.formakers.fomes.util.FormatUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class RegisteredInterviewListAdapter extends RecyclerView.Adapter<RegisteredInterviewItemViewHolder> {

    private final List<Project> projectList;
    private final TimeHelper timeHelper;
    private final MyInterviewActivity.OnItemClickListener listener;
    private final ResourceHelper resourceHelper;
    private Context context;

    @Inject
    public RegisteredInterviewListAdapter(List<Project> projectList, TimeHelper timeHelper, MyInterviewActivity.OnItemClickListener listener, ResourceHelper resourceHelper) {
        this.projectList = projectList;
        this.timeHelper = timeHelper;
        this.listener = listener;
        this.resourceHelper = resourceHelper;
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
        int skyBlueColorId = resourceHelper.getColorValue(R.color.appbee_sky_blue);
        int lightGrayColorId = resourceHelper.getColorValue(R.color.appbee_light_gray);

        if ("신청".equals(interviewStatus)) {
            holder.interviewOpenTitleTextView.setTextColor(skyBlueColorId);
            holder.interviewOpenDateTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenOpenCloseDateView.setBackgroundColor(lightGrayColorId);
            holder.interviewCloseDateTextView.setTextColor(lightGrayColorId);
            holder.interviewCloseTitleTextView.setTextColor(lightGrayColorId);
            holder.lineBetweenCloseInterviewDateView.setBackgroundColor(lightGrayColorId);
            holder.interviewDateTextView.setTextColor(lightGrayColorId);
            holder.interviewDateTitleTextView.setTextColor(lightGrayColorId);
        } else if ("확정".equals(interviewStatus)) {
            holder.interviewOpenDateTextView.setTextColor(skyBlueColorId);
            holder.interviewOpenTitleTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenOpenCloseDateView.setBackgroundColor(skyBlueColorId);
            holder.interviewCloseDateTextView.setTextColor(skyBlueColorId);
            holder.interviewCloseTitleTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenCloseInterviewDateView.setBackgroundColor(lightGrayColorId);
            holder.interviewDateTextView.setTextColor(lightGrayColorId);
            holder.interviewDateTitleTextView.setTextColor(lightGrayColorId);
        } else {
            holder.interviewOpenDateTextView.setTextColor(skyBlueColorId);
            holder.interviewOpenTitleTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenOpenCloseDateView.setBackgroundColor(skyBlueColorId);
            holder.interviewCloseDateTextView.setTextColor(skyBlueColorId);
            holder.interviewCloseTitleTextView.setTextColor(skyBlueColorId);
            holder.lineBetweenCloseInterviewDateView.setBackgroundColor(skyBlueColorId);
            holder.interviewDateTextView.setTextColor(skyBlueColorId);
            holder.interviewDateTitleTextView.setTextColor(skyBlueColorId);
        }

        holder.interviewDateLocationTextView.setText(String.format(context.getString(R.string.registered_interview_date_location), FormatUtil.toShortDateFormat(project.getInterview().getInterviewDate()), project.getInterview().getLocation(), Integer.parseInt(project.getInterview().getSelectedTimeSlot().substring(4))));
        holder.interviewOpenDateTextView.setText(FormatUtil.toLongDateFormat(project.getInterview().getOpenDate()));
        holder.interviewCloseDateTextView.setText(FormatUtil.toLongDateFormat(project.getInterview().getCloseDate()));
        holder.interviewDateTextView.setText(FormatUtil.toLongDateFormat(project.getInterview().getInterviewDate()));
    }

    private void bindLocationAndEmergencyPhone(RegisteredInterviewItemViewHolder holder, Project project) {
        holder.interviewLocation.setText(String.format(context.getString(R.string.registered_interview_location), project.getInterview().getLocation(), project.getInterview().getLocationDescription()));
        holder.emergencyPhone.setText(project.getInterview().getEmergencyPhone());
    }

    private void bindButtonListener(RegisteredInterviewItemViewHolder holder, Project project, String interviewStatus) {
        holder.cancelInterviewTextView.setOnClickListener(v -> listener.onClickCancelInterview(project.getProjectId(), project.getInterview().getSeq(), project.getInterview().getSelectedTimeSlot(), project.getName(), interviewStatus, project.getInterview().getInterviewDate(), project.getInterview().getLocation()));
        holder.showInterviewButton.setOnClickListener(v -> listener.onClickInterviewDetail(project.getProjectId(), project.getInterview().getSeq()));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
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

        String interviewStatus = "완료";
        if (openDate.compareTo(currentDate) < 0 && currentDate.compareTo(closeDate) < 0) {
            interviewStatus = "신청";
        } else if (closeDate.compareTo(currentDate) < 0 && currentDate.compareTo(interviewDate) < 0) {
            interviewStatus = "확정";
        }

        return interviewStatus;
    }
}
