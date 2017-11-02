package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.custom.InterviewInfoView;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.FormatUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;

import butterknife.BindView;

import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

public class DetailActivity extends BaseActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    @Inject
    ProjectService projectService;

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
            Glide.with(this)
                    .load(project.getImages().get(0).getUrl()).apply(new RequestOptions().override(1300, 1000).centerCrop())
                    .into(representationImageView);

            representationImageView.setTag(R.string.tag_key_image_url, project.getImages().get(0).getUrl());
            clabBadgeImageView.setVisibility(project.isCLab() ? View.VISIBLE : View.GONE);
            projectIntroduceTextView.setText(project.getIntroduce());
            projectNameTextView.setText(project.getName());
            appsDescriptionTextView.setText(String.format(getString(R.string.apps_description_format), FormatUtil.formatAppsString(project.getApps())));

            Glide.with(this)
                    .load(project.getInterviewer().getUrl()).apply(new RequestOptions().override(200, 200).centerCrop())
                    .into(interviewerPhotoImageView);
            interviewerNameTextView.setText(project.getInterviewer().getName());
            interviewerIntroduceTextView.setText(project.getInterviewer().getIntroduce());

            projectDescriptionTextView.setText(project.getDescription());
            descriptionImageViewPager.setAdapter(new ImagePagerAdapter(this, project.getDescriptionImages()));

            interviewIntroduceTextView.setText(String.format(getString(R.string.interview_introduce_text), project.getName()));
            typeInterviewInfoView.setText(String.format(getString(R.string.interview_type_text), project.getInterview().getType()));
            locationInterviewInfoView.setText(String.format(getString(R.string.interview_location_text), project.getInterview().getLocation()));
            dateInterviewInfoView.setText(String.format(getString(R.string.interview_date_text), project.getInterview().getStartDate() + "~" + project.getInterview().getEndDate()));

            int minutes = 0;
            for(Project.InterviewPlan plan : project.getInterview().getInterviewPlanList()) {
                minutes += plan.getMinute();
            }
            timeInterviewInfoView.setText(String.format(getString(R.string.interview_time_text), String.valueOf(minutes)));
        }, error -> Log.d(TAG, error.getMessage()));
    }
}