package com.formakers.fomes.appbee.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.appbee.adapter.DescriptionImageAdapter;
import com.formakers.fomes.appbee.fragment.ProjectYoutubePlayerFragment;
import com.formakers.fomes.common.network.ProjectService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.helper.TimeHelper;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.util.FormatUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

import static com.formakers.fomes.model.Project.Person;
import static com.formakers.fomes.util.FomesConstants.EXTRA.PROJECT_ID;

@Deprecated
public class ProjectDetailActivity extends BaseActivity {
    private static final String TAG = "ProjectDetailActivity";

    @Inject
    ProjectService projectService;

    @Inject
    TimeHelper timeHelper;

    @Inject
    ImageLoader imageLoader;

    @Inject
    GoogleSignInAPIHelper googleSignInAPIHelper;

    @Inject
    UserService userService;

    @Inject
    SharedPreferencesHelper SharedPreferencesHelper;

    @BindView(R.id.representation_image)
    ImageView representationImageView;

    @BindView(R.id.project_name)
    TextView projectNameTextView;

    @BindView(R.id.project_introduce)
    TextView projectIntroduceTextView;

    @BindView(R.id.project_description)
    TextView projectDescriptionTextView;

    @BindView(R.id.project_video_layout)
    FrameLayout projectVideoLayout;

    @BindView(R.id.description_image_recycler_view)
    RecyclerView descriptionImageRecyclerView;

    @BindView(R.id.owner_photo)
    ImageView ownerPhotoImageView;

    @BindView(R.id.owner_name)
    TextView ownerNameTextView;

    @BindView(R.id.interviewer_introduce)
    TextView ownerIntroduceTextView;

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

        projectId = getIntent().getStringExtra(PROJECT_ID);

        addToCompositeSubscription(
                projectService.getProject(projectId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::displayProject, this::logError)
        );
    }

    private void displayProject(Project project) {
        Person owner = project.getOwner();

        displayProjectOverview(project);
        displayOwner(owner);
        displayProjectDetail(project);
        displayProjectVideo(project.getVideoUrl());
    }

    private void displayProjectOverview(final Project project) {
        imageLoader.loadImage(representationImageView, project.getImage().getUrl(), new RequestOptions().override(1300, 1000).centerCrop());
        representationImageView.setTag(R.string.tag_key_image_url, project.getImage().getUrl());
        projectIntroduceTextView.setText(project.getIntroduce());
        projectNameTextView.setText(project.getName());
    }

    private void displayOwner(Person owner) {
        Project.ImageObject ownerImage = owner.getImage();

        String ownerImageUrl = ownerImage.getUrl();
        if (!TextUtils.isEmpty(ownerImageUrl)) {
            imageLoader.loadImage(ownerPhotoImageView, ownerImageUrl, new RequestOptions().override(200, 200).circleCrop());
            ownerPhotoImageView.setTag(R.string.tag_key_image_url, ownerImage.getUrl());
        }

        ownerNameTextView.setText(owner.getName());
        ownerIntroduceTextView.setText(owner.getIntroduce());
    }

    private void displayProjectDetail(Project project) {
        projectDescriptionTextView.setText(project.getDescription());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        descriptionImageRecyclerView.setLayoutManager(horizontalLayoutManager);
        descriptionImageRecyclerView.setAdapter(new DescriptionImageAdapter(project.getDescriptionImages(), imageLoader));
    }

    private void displayProjectVideo(String videoUrl) {
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

    @OnClick(R.id.back_button)
    void onBackButton(View view) {
        this.onBackPressed();
    }
}