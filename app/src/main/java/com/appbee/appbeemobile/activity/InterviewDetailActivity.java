package com.appbee.appbeemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.DetailPlansAdapter;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.custom.AppBeeAlertDialog;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.DateUtil;
import com.appbee.appbeemobile.util.FormatUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.HttpException;
import rx.android.schedulers.AndroidSchedulers;

import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

public class InterviewDetailActivity extends BaseActivity {
    private static final String TAG = InterviewDetailActivity.class.getSimpleName();

    @Inject
    ProjectService projectService;

    @Inject
    TimeHelper timeHelper;

    @BindView(R.id.representation_image)
    ImageView representationImageView;

    @BindView(R.id.apps_description)
    TextView appsDescriptionTextView;

    @BindView(R.id.project_name)
    TextView projectNameTextView;

    @BindView(R.id.project_introduce)
    TextView projectIntroduceTextView;

    @BindView(R.id.interview_location)
    TextView locationTextView;

    @BindView(R.id.interview_date)
    TextView dateTextView;

    @BindView(R.id.interview_d_day)
    TextView dDayTextView;

    @BindView(R.id.project_description)
    TextView projectDescriptionTextView;

    @BindView(R.id.description_image)
    ViewPager descriptionImageViewPager;

    @BindView(R.id.interview_introduce)
    TextView interviewIntroduceTextView;

    @BindView(R.id.submit_button_layout)
    LinearLayout submitButtonLayout;

    @BindView(R.id.detail_plans_layout)
    LinearLayout detailPlansLayout;

    @BindView(R.id.detail_plans_title)
    TextView detailPlansTitle;

    @BindView(R.id.detail_plans_description)
    TextView detailPlansDescription;

    @BindView(R.id.detail_plans_recycler_view)
    RecyclerView detailPlansRecyclerView;

    private String projectId;
    private long seq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_detail);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        projectId = getIntent().getStringExtra(EXTRA.PROJECT_ID);
        seq = getIntent().getLongExtra(EXTRA.INTERVIEW_SEQ, 0L);
        projectService.getInterview(projectId, seq)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayProject);
    }

    private void displayProject(Project project) {
        Project.Interview interview = project.getInterview();

        displayProjectOverview(project);
        displayInterviewSummary(interview);
        displayProjectDetail(project);
        displayProjectDetailPlans(project);
    }

    private void displayProjectOverview(final Project project) {
        Glide.with(this)
                .load(project.getImage().getUrl()).apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(representationImageView);
        representationImageView.setTag(R.string.tag_key_image_url, project.getImage().getUrl());
        appsDescriptionTextView.setText(String.format(getString(R.string.apps_text), project.getInterview().getApps().get(0)));
        projectNameTextView.setText(project.getName());
        projectIntroduceTextView.setText(project.getIntroduce());
    }

    private void displayInterviewSummary(Project.Interview interview) {
        locationTextView.setText(interview.getLocation());
        String interviewDate = FormatUtil.convertInputDateFormat(interview.getInterviewDate(), "MM/dd");
        String dayOfDay = DateUtil.getDayOfWeek(interview.getInterviewDate());
        dateTextView.setText(String.format(getString(R.string.interview_date_text), interviewDate, dayOfDay));
        dDayTextView.setText(String.format(getString(R.string.d_day_text), DateUtil.calDateDiff(timeHelper.getCurrentTime(), interview.getCloseDate().getTime())));
    }

    private void displayProjectDetail(Project project) {
        descriptionImageViewPager.setAdapter(new ImagePagerAdapter(this, project.getDescriptionImages()));
        projectDescriptionTextView.setText(project.getDescription());
    }

    private void displayProjectDetailPlans(Project project) {
        detailPlansTitle.setText(String.format(getString(R.string.interview_detail_plans_title_format), project.getName()));
        String interviewDate = FormatUtil.convertInputDateFormat(project.getInterview().getInterviewDate(), "M월 d일");
        String dayOfDay = DateUtil.getDayOfWeek(project.getInterview().getInterviewDate());
        detailPlansDescription.setText(String.format(getString(R.string.interview_detail_plans_description_format), interviewDate, dayOfDay, project.getInterview().getLocation()));


        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailPlansRecyclerView.setLayoutManager(horizontalLayoutManager);

        DetailPlansAdapter detailPlansAdapter = new DetailPlansAdapter(project.getInterview().getTimeSlots());
        detailPlansRecyclerView.setAdapter(detailPlansAdapter);
    }

    @OnClick(R.id.back_button)
    void onBackButton(View view) {
        this.onBackPressed();
    }

    @OnClick(R.id.submit_button_layout)
    void onSubmitButton(View view) {
        if (detailPlansLayout.getVisibility() == View.GONE) {
            detailPlansLayout.setVisibility(View.VISIBLE);
            return;
        }

        String slotId = ((DetailPlansAdapter) detailPlansRecyclerView.getAdapter()).getSelectedTimeSlot();

        if (slotId == null || slotId.isEmpty()) {
            AppBeeAlertDialog alertDialog = new AppBeeAlertDialog(this, "시간을 선택해주세요.", "세부일정 선택은 필수입니다.", (dialog, which) -> dialog.dismiss());
            alertDialog.show();
            return;
        }

        projectService.postParticipate(projectId, seq, slotId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result) {
                        DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(InterviewDetailActivity.this, MyInterviewActivity.class);
                            startActivity(intent);
                            finish();
                        };
                        AppBeeAlertDialog alertDialog = new AppBeeAlertDialog(this, R.drawable.dialog_success_image, getString(R.string.dialog_registered_interview_success_title), getString(R.string.dialog_registered_interview_success_message), onClickListener);
                        alertDialog.show();
                    }
                }, err -> {
                    if (err instanceof HttpException) {
                        Toast.makeText(this, String.valueOf(((HttpException) err).code()), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, String.valueOf(err.getCause()), Toast.LENGTH_LONG).show();
                    }
                });
    }

}