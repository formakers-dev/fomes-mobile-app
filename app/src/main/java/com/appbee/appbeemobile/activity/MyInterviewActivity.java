package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.RegisteredInterviewListAdapter;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.AppBeeConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.line_divider));
        interviewRecyclerView.addItemDecoration(dividerItemDecoration);

        RegisteredInterviewListAdapter registeredInterviewListAdapter = new RegisteredInterviewListAdapter(projectList, timeHelper, actionListener);
        interviewRecyclerView.setAdapter(registeredInterviewListAdapter);
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClick() {
        super.onBackPressed();
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void onSelectProject(String projectId) {
            Intent intent = new Intent(MyInterviewActivity.this, ProjectDetailActivity.class);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, projectId);
            startActivity(intent);
        }

        @Override
        public void onRequestToCancelInterview(String projectId, long interviewSeq) {
            Intent intent = new Intent(MyInterviewActivity.this, CancelInterviewActivity.class);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, projectId);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, interviewSeq);
            startActivity(intent);
        }
    };

    public interface ActionListener {
        void onSelectProject(String projectId);
        void onRequestToCancelInterview(String projectId, long interviewSeq);
    }
}

