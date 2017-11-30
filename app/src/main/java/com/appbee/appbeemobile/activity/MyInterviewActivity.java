package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.RegisteredInterviewListAdapter;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.AppBeeConstants;

import java.util.Date;
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

    @BindView(R.id.not_found_interview_text)
    View notFoundInterviewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_my_interview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        projectService.getRegisteredInterviews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(projectList -> {
                    displayInterviewsLayout(projectList == null || projectList.isEmpty());
                    bindRegisteredInterviews(projectList);
                });
    }

    private void displayInterviewsLayout(boolean isEmpty) {
        interviewRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        notFoundInterviewView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void bindRegisteredInterviews(List<Project> projectList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        interviewRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, null));
        interviewRecyclerView.addItemDecoration(dividerItemDecoration);

        RegisteredInterviewListAdapter registeredInterviewListAdapter = new RegisteredInterviewListAdapter(projectList, timeHelper, actionListener);
        interviewRecyclerView.setAdapter(registeredInterviewListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void onSelectProject(String projectId) {
            Intent intent = new Intent(MyInterviewActivity.this, ProjectDetailActivity.class);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, projectId);
            startActivity(intent);
        }

        @Override
        public void onRequestToCancelInterview(String projectId, long interviewSeq, String timeSlot, String projectName, String interviewStatus, Date interviewDate, String location) {
            Intent intent = new Intent(MyInterviewActivity.this, CancelInterviewActivity.class);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, projectId);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, interviewSeq);
            intent.putExtra(AppBeeConstants.EXTRA.TIME_SLOT, timeSlot);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_NAME, projectName);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_STATUS, interviewStatus);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_DATE, interviewDate);
            intent.putExtra(AppBeeConstants.EXTRA.LOCATION, location);
            startActivity(intent);
        }
    };

    public interface ActionListener {
        void onSelectProject(String projectId);

        void onRequestToCancelInterview(String projectId, long interviewSeq, String timeSlot, String projectName, String interviewStatus, Date interviewDate, String location);
    }
}

