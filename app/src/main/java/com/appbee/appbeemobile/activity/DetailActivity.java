package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import javax.inject.Inject;

import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

public class DetailActivity extends BaseActivity{

    private static final String TAG = DetailActivity.class.getSimpleName();
    @Inject
    ProjectService projectService;

    Project project;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);


        String projectId = getIntent().getStringExtra(EXTRA.PROJECT_ID);
        projectService.getProject(projectId).subscribe(project -> this.project = project, error-> Log.d(TAG, error.getMessage()));
    }
}