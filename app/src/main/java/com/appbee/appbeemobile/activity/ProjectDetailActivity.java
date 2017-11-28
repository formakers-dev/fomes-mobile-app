package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

import static com.appbee.appbeemobile.model.Project.Person;
import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

public class ProjectDetailActivity extends BaseActivity {
    private static final String TAG = ProjectDetailActivity.class.getSimpleName();

    @Inject
    ProjectService projectService;

    @Inject
    TimeHelper timeHelper;

    @BindView(R.id.representation_image)
    ImageView representationImageView;

    @BindView(R.id.project_name)
    TextView projectNameTextView;

    @BindView(R.id.project_introduce)
    TextView projectIntroduceTextView;

    @BindView(R.id.project_description)
    TextView projectDescriptionTextView;

    @BindView(R.id.description_image)
    ViewPager descriptionImageViewPager;

    @BindView(R.id.owner_photo)
    ImageView interviewerPhotoImageView;

    @BindView(R.id.owner_name)
    TextView interviewerNameTextView;

    @BindView(R.id.interviewer_introduce)
    TextView interviewerIntroduceTextView;

    private String projectId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        projectId = getIntent().getStringExtra(EXTRA.PROJECT_ID);
        projectService.getProject(projectId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayProject);
    }

    private void displayProject(Project project) {
        Person owner = project.getOwner();

        displayProjectOverview(project);
        displayOwner(owner);
        displayProjectDetail(project);
    }

    private void displayProjectOverview(final Project project) {
        Glide.with(this).load(project.getImage().getUrl())
                .apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(representationImageView);
        representationImageView.setTag(R.string.tag_key_image_url, project.getImage().getUrl());
        projectIntroduceTextView.setText(project.getIntroduce());
        projectNameTextView.setText(project.getName());
    }

    private void displayProjectDetail(Project project) {
        projectDescriptionTextView.setText(project.getDescription());
        descriptionImageViewPager.setAdapter(new ImagePagerAdapter(this, project.getDescriptionImages()));
    }

    private void displayOwner(Person owner) {
        Project.ImageObject ownerImage = owner.getImage();

        if (ownerImage != null) {
            Glide.with(this).load(ownerImage.getUrl())
                    .apply(new RequestOptions().override(200, 200).circleCrop())
                    .into(interviewerPhotoImageView);
            interviewerPhotoImageView.setTag(R.string.tag_key_image_url, ownerImage.getUrl());
        } else {
            interviewerPhotoImageView.setImageResource(R.mipmap.ic_launcher_app);
        }

        interviewerNameTextView.setText(owner.getName());
        interviewerIntroduceTextView.setText(owner.getIntroduce());
    }

    @OnClick(R.id.back_button)
    void onBackButton(View view) {
        this.onBackPressed();
    }

}