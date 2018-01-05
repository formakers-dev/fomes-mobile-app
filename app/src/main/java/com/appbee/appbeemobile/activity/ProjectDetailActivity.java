package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.DescriptionImageAdapter;
import com.appbee.appbeemobile.fragment.ProjectYoutubePlayerFragment;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.ImageLoader;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.util.FormatUtil;
import com.bumptech.glide.request.RequestOptions;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.appbee.appbeemobile.model.Project.Person;
import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA.PROJECT_ID;

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
    LocalStorageHelper localStorageHelper;

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