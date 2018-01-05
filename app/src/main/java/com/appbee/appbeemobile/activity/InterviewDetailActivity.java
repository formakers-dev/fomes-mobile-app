package com.appbee.appbeemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.DescriptionImageAdapter;
import com.appbee.appbeemobile.custom.AppBeeAlertDialog;
import com.appbee.appbeemobile.fragment.ProjectYoutubePlayerFragment;
import com.appbee.appbeemobile.helper.ImageLoader;
import com.appbee.appbeemobile.helper.ResourceHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.AppBeeConstants;
import com.appbee.appbeemobile.util.DateUtil;
import com.appbee.appbeemobile.util.FormatUtil;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

public class InterviewDetailActivity extends BaseActivity {
    private static final String TAG = "InterviewDetailActivity";
    private static final String RADIO_BUTTON_CHECKING = "checking";
    private static final String RADIO_BUTTON_CHECKED = "checked";
    private static final String RADIO_BUTTON_UNCHECKED = "";

    @Inject
    ProjectService projectService;

    @Inject
    TimeHelper timeHelper;

    @Inject
    ImageLoader imageLoader;

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

    @BindView(R.id.description_image_recycler_view)
    RecyclerView descriptionImageRecyclerView;

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

    @BindView(R.id.time_slot_radio_group)
    RadioGroup timeSlotRadioGroup;

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

        addToCompositeSubscription(
                projectService.getInterview(projectId, seq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::displayProject)
        );
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
        bindButtonLayout(project.getInterview().getSelectedTimeSlot(), project.getInterview().getTimeSlots().size());
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

    private void bindButtonLayout(String selectedTimeSlot, int timeSlotSize) {
        if (!TextUtils.isEmpty(selectedTimeSlot)) {
            submitArrowButton.setVisibility(View.GONE);
            submitButton.setText(R.string.registered_interview_submit_button);
            submitButton.setTextColor(resourceHelper.getColorValue(R.color.appbee_warm_gray));
            submitButton.setBackgroundColor(resourceHelper.getColorValue(R.color.appbee_dim_gray));
            submitButton.setClickable(false);
        } else if (timeSlotSize == 0) {
            submitArrowButton.setVisibility(View.GONE);
            submitButton.setText(R.string.closed_interview_submit_button);
            submitButton.setTextColor(resourceHelper.getColorValue(R.color.appbee_warm_gray));
            submitButton.setBackgroundColor(resourceHelper.getColorValue(R.color.appbee_dim_gray));
            submitButton.setClickable(false);
        }
    }

    private void bindProjectOverview(final Project project) {
        imageLoader.loadImage(representationImageView, project.getImage().getUrl(), new RequestOptions().override(1300, 1000).centerCrop());
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
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        descriptionImageRecyclerView.setLayoutManager(horizontalLayoutManager);

        descriptionImageRecyclerView.setAdapter(new DescriptionImageAdapter(project.getDescriptionImages(), imageLoader));
        projectDescriptionTextView.setText(project.getDescription());
    }

    private void bindInterviewRequestLayout(Project project) {
        detailPlansTitle.setText(String.format(getString(R.string.interview_detail_plans_title_format), project.getName()));
        String interviewDate = FormatUtil.convertInputDateFormat(project.getInterview().getInterviewDate(), "M월 d일");
        String dayOfDay = DateUtil.getDayOfWeek(project.getInterview().getInterviewDate());
        detailPlansDescription.setText(String.format(getString(R.string.interview_detail_plans_description_format), interviewDate, dayOfDay, project.getInterview().getLocation()));

        for (String timeSlot : project.getInterview().getTimeSlots()) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.item_time_button, null);

            int timeSlotId = Integer.parseInt(timeSlot.substring(4));

            radioButton.setId(timeSlotId);
            radioButton.setText(String.format(Locale.KOREA, "%02d:00", timeSlotId));

            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    buttonView.setTag(RADIO_BUTTON_CHECKING);
                } else {
                    buttonView.setTag(RADIO_BUTTON_UNCHECKED);
                }
            });

            radioButton.setOnClickListener(v -> {
                final RadioButton selectedButton = ((RadioButton) v);

                if (selectedButton.isChecked()) {
                    if (RADIO_BUTTON_CHECKING.equals(v.getTag())) {
                        selectedButton.setTag(RADIO_BUTTON_CHECKED);
                    } else if (RADIO_BUTTON_CHECKED.equals(v.getTag())) {
                        timeSlotRadioGroup.clearCheck();
                        selectedButton.setTag(RADIO_BUTTON_UNCHECKED);
                    }
                }
            });

            timeSlotRadioGroup.addView(radioButton);
        }
    }

    private void bindOwnerDetail(Project.Person owner) {
        Project.ImageObject ownerImage = owner.getImage();

        String ownerImageUrl = ownerImage.getUrl();
        if (!TextUtils.isEmpty(ownerImageUrl)) {
            imageLoader.loadImage(ownerPhotoImageView, ownerImageUrl, new RequestOptions().override(200, 200).circleCrop());
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

        int radioButtonId = timeSlotRadioGroup.getCheckedRadioButtonId();
        if (radioButtonId < 0) {
            AppBeeAlertDialog alertDialog = new AppBeeAlertDialog(this, "시간을 선택해주세요.", "세부일정 선택은 필수입니다.", (dialog, which) -> dialog.dismiss());
            alertDialog.show();
            return;
        }

        RadioButton radioButton = (RadioButton) timeSlotRadioGroup.findViewById(radioButtonId);
        String slotId = "time" + radioButton.getId();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        addToCompositeSubscription(
                projectService.postParticipate(projectId, seq, slotId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            showSuccessAlertDialog();
                        }, err -> {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            if (err instanceof HttpException) {
                                showPostParticipateHttpErrorMessage((HttpException) err);
                            } else {
                                showErrorAlertDialog(R.string.participate_http_fail_message);
                            }
                        })
        );
    }

    private void showPostParticipateHttpErrorMessage(HttpException err) {
        switch (err.code()) {
            case AppBeeConstants.HTTP_STATUS.CODE_409_CONFLICT:
            case AppBeeConstants.HTTP_STATUS.CODE_412_PRECONDITION_FAILED:
                showErrorAlertDialog(R.string.dialog_interview_register_fail_message);
                break;
            case AppBeeConstants.HTTP_STATUS.CODE_405_METHOD_NOT_ALLOWED:
                showErrorAlertDialog(R.string.dialog_interview_already_registered_fail_message);
                break;
            default:
                showErrorAlertDialog(R.string.participate_http_fail_message);
        }
    }

    private void showSuccessAlertDialog() {
        DialogInterface.OnClickListener onClickListener = (dialog, which) -> moveToMyInterviewActivity(dialog);
        AppBeeAlertDialog alertDialog = new AppBeeAlertDialog(this, R.drawable.dialog_success_image, getString(R.string.dialog_registered_interview_success_title), getString(R.string.dialog_registered_interview_success_message), onClickListener);
        alertDialog.setOnCancelListener(this::moveToMyInterviewActivity);
        alertDialog.show();
    }

    private void showErrorAlertDialog(@StringRes int messageResId) {
        DialogInterface.OnClickListener onClickListener = (dialog, which) -> refreshInterviewDetailActivity(dialog);
        AppBeeAlertDialog alertDialog = new AppBeeAlertDialog(this, getString(R.string.dialog_interview_register_fail_title), getString(messageResId), onClickListener);
        alertDialog.setOnCancelListener(this::refreshInterviewDetailActivity);
        alertDialog.show();
    }

    private void showDetailPlanLayout() {
        detailPlansLayout.setVisibility(View.VISIBLE);
        submitArrowButton.setBackground(getDrawable(R.drawable.submit_close));
        scrollViewLayout.setForeground(new ColorDrawable(resourceHelper.getColorValue(R.color.appbee_dim_foreground)));

        detailPlansLayout.setOnClickListener(v -> {
            // do nothing
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

    private void refreshInterviewDetailActivity(DialogInterface dialog) {
        dialog.dismiss();
        Intent intent = new Intent(InterviewDetailActivity.this, InterviewDetailActivity.class);

        intent.putExtra(EXTRA.PROJECT_ID, projectId);
        intent.putExtra(EXTRA.INTERVIEW_SEQ, seq);

        startActivity(intent);
        finish();
    }
}