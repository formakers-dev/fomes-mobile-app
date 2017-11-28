package com.appbee.appbeemobile.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.RegisteredInterviewListAdapter;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class MyInterviewActivity extends BaseActivity {

    @Inject
    ProjectService projectService;

    @Inject
    TimeHelper timeHelper;

    @BindView(R.id.interview_recycler_view)
    RecyclerView interviewRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_my_interview);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        projectService.getRegisteredInterviews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showRegisteredInterviews);
    }

    private void showRegisteredInterviews(List<Project> projectList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        interviewRecyclerView.setLayoutManager(linearLayoutManager);

        RegisteredInterviewListAdapter registeredInterviewListAdapter = new RegisteredInterviewListAdapter(projectList, timeHelper);
        interviewRecyclerView.setAdapter(registeredInterviewListAdapter);
    }
}

