package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.adapter.PlanListAdapter;
import com.appbee.appbeemobile.custom.InterviewInfoView;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.FormatUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;

import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

public class DetailActivity extends BaseActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    @Inject
    ProjectService projectService;

    @Inject
    TimeHelper timeHelper;

    @BindView(R.id.back_button)
    Button backButton;

    @BindView(R.id.representation_image)
    ImageView representationImageView;

    @BindView(R.id.clab_badge)
    ImageView clabBadgeImageView;

    @BindView(R.id.project_introduce)
    TextView projectIntroduceTextView;

    @BindView(R.id.project_name)
    TextView projectNameTextView;

    @BindView(R.id.apps_description)
    TextView appsDescriptionTextView;

    @BindView(R.id.interviewer_photo)
    ImageView interviewerPhotoImageView;

    @BindView(R.id.interviewer_name)
    TextView interviewerNameTextView;

    @BindView(R.id.interviewer_introduce)
    TextView interviewerIntroduceTextView;

    @BindView(R.id.project_description)
    TextView projectDescriptionTextView;

    @BindView(R.id.description_image)
    ViewPager descriptionImageViewPager;

    @BindView(R.id.interview_introduce)
    TextView interviewIntroduceTextView;

    @BindView(R.id.interview_type)
    InterviewInfoView typeInterviewInfoView;

    @BindView(R.id.interview_location)
    InterviewInfoView locationInterviewInfoView;

    @BindView(R.id.interview_date)
    InterviewInfoView dateInterviewInfoView;

    @BindView(R.id.interview_time)
    InterviewInfoView timeInterviewInfoView;

    @BindView(R.id.interview_type_summary)
    TextView interviewTypeSummaryTextView;

    @BindView(R.id.available_interviewer_count)
    TextView availableInterviewerCountTextView;

    @BindView(R.id.d_day)
    TextView dDayTextView;

    @BindView(R.id.participation_status)
    TextView participationStatus;

    @BindView(R.id.close_date)
    TextView closeDate;

    @BindView(R.id.interview_plan)
    ListView interviewPlanListView;

    @BindView(R.id.interview_summary)
    TextView interviewSummaryTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String projectId = getIntent().getStringExtra(EXTRA.PROJECT_ID);
        projectService.getProject(projectId).subscribe(project -> {
            Project.Interview interview = project.getInterview();
            Project.Interviewer interviewer = project.getInterviewer();
            int participantCount = interview.getParticipants().size();

            Glide.with(this)
                    .load(project.getImages().get(0).getUrl()).apply(new RequestOptions().override(1300, 1000).centerCrop())
                    .into(representationImageView);

            representationImageView.setTag(R.string.tag_key_image_url, project.getImages().get(0).getUrl());
            clabBadgeImageView.setVisibility(project.isCLab() ? View.VISIBLE : View.GONE);
            projectIntroduceTextView.setText(project.getIntroduce());
            projectNameTextView.setText(project.getName());
            appsDescriptionTextView.setText(String.format(getString(R.string.apps_description_format), FormatUtil.formatAppsString(project.getApps())));

            interviewTypeSummaryTextView.setText(interview.getType());
            availableInterviewerCountTextView.setText(String.valueOf(interview.getTotalCount() - participantCount));
            dDayTextView.setText(String.format(getString(R.string.d_day_text), getDDayFromNow(interview.getCloseDate())));

            Glide.with(this)
                    .load(interviewer.getUrl()).apply(new RequestOptions().override(200, 200).centerCrop())
                    .into(interviewerPhotoImageView);
            interviewerNameTextView.setText(interviewer.getName());
            interviewerIntroduceTextView.setText(interviewer.getIntroduce());

            projectDescriptionTextView.setText(project.getDescription());
            descriptionImageViewPager.setAdapter(new ImagePagerAdapter(this, project.getDescriptionImages()));

            interviewIntroduceTextView.setText(String.format(getString(R.string.interview_introduce_text), project.getName()));
            typeInterviewInfoView.setText(String.format(getString(R.string.interview_type_text), interview.getType()));
            locationInterviewInfoView.setText(String.format(getString(R.string.interview_location_text), interview.getLocation()));
            String interviewStartDate = FormatUtil.convertInputDateFormat(interview.getStartDate(), "MM월 dd일");
            String interviewEndDate = FormatUtil.convertInputDateFormat(interview.getEndDate(), "MM월 dd일");
            dateInterviewInfoView.setText(String.format(getString(R.string.interview_date_text), interviewStartDate + "~" + interviewEndDate));

            int minutes = 0;
            for (Project.InterviewPlan plan : interview.getPlans()) {
                minutes += plan.getMinute();
            }
            timeInterviewInfoView.setText(String.format(getString(R.string.interview_time_text), minutes));

            participationStatus.setText(String.format(getString(R.string.participation_status), participantCount, interview.getTotalCount()));
            closeDate.setText(String.format(getString(R.string.close_date), FormatUtil.convertInputDateFormat(interview.getCloseDate(), "yy.MM.dd")));

            interviewPlanListView.setAdapter(new PlanListAdapter(interview.getPlans()));
            String startDate = FormatUtil.convertInputDateFormat(interview.getStartDate(), "MM.dd");
            String endDate = FormatUtil.convertInputDateFormat(interview.getEndDate(), "MM.dd");
            interviewSummaryTextView.setText(String.format(getString(R.string.interview_summary), interview.getLocation(), startDate, endDate, minutes));

        }, error -> Log.d(TAG, error.getMessage()));
    }

    private int getDDayFromNow(String closeDate) {
        int dDay = 0;

        try {
            String today = FormatUtil.INPUT_DATE_FORMAT.format(timeHelper.getCurrentTime());
            Date todayDate = FormatUtil.INPUT_DATE_FORMAT.parse(today);
            Date dDayDate = FormatUtil.INPUT_DATE_FORMAT.parse(closeDate);

            if (todayDate.compareTo(dDayDate) < 0) {
                dDay = (int) ((dDayDate.getTime() - todayDate.getTime()) / (1000 * 60 * 60 * 24));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dDay;
    }

}