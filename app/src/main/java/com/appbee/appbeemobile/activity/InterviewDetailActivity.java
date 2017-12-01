package com.appbee.appbeemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.DetailPlansAdapter;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.custom.AppBeeAlertDialog;
import com.appbee.appbeemobile.fragment.ProjectYoutubePlayerFragment;
import com.appbee.appbeemobile.helper.ResourceHelper;
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

    @Inject
    ResourceHelper resourceHelper;

    @BindView(R.id.representation_image)
    ImageView representationImageView;

    @BindView(R.id.apps_description)
    TextView appsDescriptionTextView;

    @BindView(R.id.project_name)
    TextView projectNameTextView;

    @BindView(R.id.project_introduce)
    TextView projectIntroduceTextView;

    @BindView(R.id.interview_type_textview)
    TextView typeTextView;

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

    @BindView(R.id.owner_photo)
    ImageView ownerPhotoImageView;

    @BindView(R.id.owner_name)
    TextView ownerNameTextView;

    @BindView(R.id.interviewer_introduce)
    TextView ownerIntroduceTextView;

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

    @BindView(R.id.submit_arrow_button)
    Button submitArrowButton;

    @BindView(R.id.submit_button)
    Button submitButton;

    @BindView(R.id.scroll_view_layout)
    FrameLayout scrollViewLayout;

    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @BindView(R.id.project_video_layout)
    FrameLayout projectVideoLayout;

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

        bindProjectOverview(project);
        bindInterviewOverview(interview);
        bindProjectDetail(project);
        bindProjectVideo(project.getVideoUrl());
        bindInterviewDetail(interview);
        bindOwnerDetail(project.getOwner());
        bindInterviewRequestLayout(project);
        bindButtonLayout(!TextUtils.isEmpty(project.getInterview().getSelectedTimeSlot()));
    }

    private void bindProjectVideo(String videoUrl) {
        String youTubeId = FormatUtil.parseYouTubeId(videoUrl);

        if (!TextUtils.isEmpty(youTubeId)) {
            projectVideoLayout.setVisibility(View.VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putString(ProjectYoutubePlayerFragment.EXTRA_YOUTUBE_ID, youTubeId);

            ProjectYoutubePlayerFragment youTubePlayerFragment = new ProjectYoutubePlayerFragment();
            youTubePlayerFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add(R.id.project_video_layout, youTubePlayerFragment, "YouTubePlayerFragment").commit();
        }
    }

    private void bindButtonLayout(boolean isRegisteredInterview) {
        if (isRegisteredInterview) {
            submitArrowButton.setVisibility(View.GONE);
            submitButton.setText(R.string.registered_interview_submit_button);
            submitButton.setTextColor(resourceHelper.getColorValue(R.color.appbee_warm_gray));
            submitButton.setBackgroundColor(resourceHelper.getColorValue(R.color.appbee_dim_gray));
            submitButton.setClickable(false);
        }
    }

    private void bindProjectOverview(final Project project) {
        Glide.with(this)
                .load(project.getImage().getUrl()).apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(representationImageView);
        representationImageView.setTag(R.string.tag_key_image_url, project.getImage().getUrl());
        appsDescriptionTextView.setText(String.format(getString(R.string.recommand_to_user_of_similar_app), project.getInterview().getApps().get(0).getAppName()));
        projectNameTextView.setText(project.getName());
        projectIntroduceTextView.setText(project.getIntroduce());
    }

    private void bindInterviewOverview(Project.Interview interview) {
        typeTextView.setText(interview.getType());
        locationTextView.setText(interview.getLocation());
        String interviewDate = FormatUtil.convertInputDateFormat(interview.getInterviewDate(), "MM/dd");
        String dayOfDay = DateUtil.getDayOfWeek(interview.getInterviewDate());
        dateTextView.setText(String.format(getString(R.string.interview_date_text), interviewDate, dayOfDay));
        dDayTextView.setText(String.format(getString(R.string.d_day_text), DateUtil.calDateDiff(timeHelper.getCurrentTime(), interview.getCloseDate().getTime())));
    }

    private void bindProjectDetail(Project project) {
        descriptionImageViewPager.setAdapter(new ImagePagerAdapter(this, project.getDescriptionImages()));
        projectDescriptionTextView.setText(project.getDescription());
    }

    private void bindInterviewRequestLayout(Project project) {
        detailPlansTitle.setText(String.format(getString(R.string.interview_detail_plans_title_format), project.getName()));
        String interviewDate = FormatUtil.convertInputDateFormat(project.getInterview().getInterviewDate(), "M월 d일");
        String dayOfDay = DateUtil.getDayOfWeek(project.getInterview().getInterviewDate());
        detailPlansDescription.setText(String.format(getString(R.string.interview_detail_plans_description_format), interviewDate, dayOfDay, project.getInterview().getLocation()));

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        detailPlansRecyclerView.setLayoutManager(horizontalLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.interview_time_slot_divider, null));
        detailPlansRecyclerView.addItemDecoration(dividerItemDecoration);

        DetailPlansAdapter detailPlansAdapter = new DetailPlansAdapter(project.getInterview().getTimeSlots());
        detailPlansRecyclerView.setAdapter(detailPlansAdapter);
    }

    private void bindOwnerDetail(Project.Person owner) {
        Project.ImageObject ownerImage = owner.getImage();

        String ownerImageUrl = ownerImage.getUrl();
        if (!TextUtils.isEmpty(ownerImageUrl)) {
            Glide.with(this).load(ownerImageUrl)
                    .apply(new RequestOptions().override(200, 200).circleCrop())
                    .into(ownerPhotoImageView);
            ownerPhotoImageView.setTag(R.string.tag_key_image_url, ownerImage.getUrl());
        }

        ownerNameTextView.setText(owner.getName());
        ownerIntroduceTextView.setText(owner.getIntroduce());
    }

    private void bindInterviewDetail(Project.Interview interview) {
        interviewIntroduceTextView.setText(interview.getIntroduce());
    }

    @OnClick(R.id.back_button)
    void onBackButton(View view) {
        this.onBackPressed();
    }

    @OnClick(R.id.submit_arrow_button)
    void onClickOpenDetailLayout(View view) {
        if (detailPlansLayout.getVisibility() == View.GONE) {
            showDetailPlanLayout();
        } else {
            hideDetailPlanLayout();
        }
    }

    @OnClick(R.id.submit_button)
    void onSubmitButton(View view) {
        if (detailPlansLayout.getVisibility() == View.GONE) {
            showDetailPlanLayout();
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
                        DialogInterface.OnClickListener onClickListener = (dialog, which) -> moveToMyInterviewActivity(dialog);
                        AppBeeAlertDialog alertDialog = new AppBeeAlertDialog(this, R.drawable.dialog_success_image, getString(R.string.dialog_registered_interview_success_title), getString(R.string.dialog_registered_interview_success_message), onClickListener);
                        alertDialog.setOnCancelListener(this::moveToMyInterviewActivity);
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

    private void showDetailPlanLayout() {
        detailPlansLayout.setVisibility(View.VISIBLE);
        submitArrowButton.setBackground(getDrawable(R.drawable.submit_close));
        scrollViewLayout.setForeground(new ColorDrawable(resourceHelper.getColorValue(R.color.appbee_dim_foreground)));

        detailPlansLayout.setOnClickListener(v -> {
            return;
        });

        scrollView.setOnTouchListener((v, event) -> {
            hideDetailPlanLayout();
            return false;
        });
    }

    private void hideDetailPlanLayout() {
        detailPlansLayout.setVisibility(View.GONE);
        submitArrowButton.setBackground(getDrawable(R.drawable.submit_open));
        scrollViewLayout.setForeground(new ColorDrawable(resourceHelper.getColorValue(android.R.color.transparent)));
        scrollView.setOnTouchListener(null);
    }

    private void moveToMyInterviewActivity(DialogInterface dialog) {
        dialog.dismiss();
        Intent intent = new Intent(InterviewDetailActivity.this, MyInterviewActivity.class);
        startActivity(intent);
        finish();
    }
}