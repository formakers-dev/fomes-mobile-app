package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
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
import butterknife.OnClick;
import retrofit2.HttpException;
import rx.Observable;

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

    @BindView(R.id.interview_time)
    TextView timeTextView;

    @BindView(R.id.interview_d_day)
    TextView dDayTextView;

    @BindView(R.id.project_description)
    TextView projectDescriptionTextView;

    @BindView(R.id.description_image)
    ViewPager descriptionImageViewPager;

    @BindView(R.id.interview_introduce)
    TextView interviewIntroduceTextView;

    @BindView(R.id.interview_plan_layout)
    LinearLayout interviewPlanLayout;

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
        projectService.getInterview(projectId, seq).subscribe(this::displayProject);
    }

    private void displayProject(Project project) {
        Project.Interview interview = project.getInterview();

        displayProjectOverview(project);
        displayInterviewSummary(interview);
        displayProjectDetail(project);
        displayPlans(interview);
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
        String dayOfDay = FormatUtil.getDayOfWeek(interview.getInterviewDate());
        dateTextView.setText(String.format(getString(R.string.interview_date_text), interviewDate, dayOfDay));
        timeTextView.setText(String.format(getString(R.string.interview_time_text), getTotalInterviewMinute(interview)));
        dDayTextView.setText(String.format(getString(R.string.d_day_text), getDDayFromNow(interview.getCloseDate())));
    }

    private void displayProjectDetail(Project project) {
        descriptionImageViewPager.setAdapter(new ImagePagerAdapter(this, project.getDescriptionImages()));
        projectDescriptionTextView.setText(project.getDescription());
    }

    private void displayPlans(Project.Interview interview) {
        for (Project.InterviewPlan plan : interview.getPlans()) {
            View planLayout = LayoutInflater.from(this).inflate(R.layout.plan_list_item, null);
            ((TextView) planLayout.findViewById(R.id.minute)).setText(String.valueOf(plan.getMinute()));
            ((TextView) planLayout.findViewById(R.id.plan)).setText(plan.getPlan());
            interviewPlanLayout.addView(planLayout);
        }
    }

    private int getTotalInterviewMinute(Project.Interview interview) {
        return Observable.from(interview.getPlans()).map(Project.InterviewPlan::getMinute).scan((sum, item) -> sum + item).toBlocking().last();
    }

    private int getDDayFromNow(@NonNull Date closeDate) {
        int dDay = 0;

        try {
            String today = FormatUtil.INPUT_DATE_FORMAT.format(timeHelper.getCurrentTime());
            Date todayDate = FormatUtil.INPUT_DATE_FORMAT.parse(today);

            if (todayDate.compareTo(closeDate) < 0) {
                dDay = (int) ((closeDate.getTime() - todayDate.getTime()) / (1000 * 60 * 60 * 24));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dDay;
    }

    @OnClick(R.id.back_button)
    void onBackButton(View view) {
        this.onBackPressed();
    }

    @OnClick(R.id.submit_button)
    void onSubmitButton(View view) {
        // TODO: 인터뷰별로 변경
        projectService.postParticipate(projectId, seq, "").subscribe(result -> {
            if (result) {
                Toast.makeText(this, "인터뷰참가신청완료!!", Toast.LENGTH_LONG).show();
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