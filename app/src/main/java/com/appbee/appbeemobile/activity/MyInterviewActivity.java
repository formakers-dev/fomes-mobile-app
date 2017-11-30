package com.appbee.appbeemobile.activity;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class MyInterviewActivity extends BaseActivity {

    private static final int CANCEL_INTERVIEW_REQUEST_CODE = 1001;

    @Inject
    ProjectService projectService;

    @Inject
    TimeHelper timeHelper;

    @BindView(R.id.interview_recycler_view)
    RecyclerView interviewRecyclerView;

    @BindView(R.id.not_found_registered_interview_text)
    View notFoundRegisteredInterviewText;

    List<Project> projectList = new ArrayList<>();

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        interviewRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider, null));
        interviewRecyclerView.addItemDecoration(dividerItemDecoration);

        RegisteredInterviewListAdapter registeredInterviewListAdapter = new RegisteredInterviewListAdapter(projectList, timeHelper, onItemClickListener);
        interviewRecyclerView.setAdapter(registeredInterviewListAdapter);

        requestRegisteredInterviews();
    }

    private void requestRegisteredInterviews() {
        projectService.getRegisteredInterviews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(projectList -> {
                    displayInterviewsLayout(projectList == null || projectList.isEmpty());
                    if (interviewRecyclerView.getVisibility() == View.VISIBLE) {
                        refreshInterviewRecyclerView(projectList);
                    }
                });
    }

    private void displayInterviewsLayout(boolean isEmpty) {
        interviewRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        notFoundRegisteredInterviewText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void refreshInterviewRecyclerView(List<Project> projectList) {
        this.projectList.clear();
        this.projectList.addAll(projectList);
        interviewRecyclerView.getAdapter().notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CANCEL_INTERVIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            requestRegisteredInterviews();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onClickInterviewDetail(String projectId, long interviewSeq) {
            Intent intent = new Intent(MyInterviewActivity.this, InterviewDetailActivity.class);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, projectId);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, interviewSeq);
            startActivity(intent);
        }

        @Override
        public void onClickCancelInterview(String projectId, long interviewSeq, String timeSlot, String projectName, String interviewStatus, Date interviewDate, String location) {
            Intent intent = new Intent(MyInterviewActivity.this, CancelInterviewActivity.class);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, projectId);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, interviewSeq);
            intent.putExtra(AppBeeConstants.EXTRA.TIME_SLOT, timeSlot);
            intent.putExtra(AppBeeConstants.EXTRA.PROJECT_NAME, projectName);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_STATUS, interviewStatus);
            intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_DATE, interviewDate);
            intent.putExtra(AppBeeConstants.EXTRA.LOCATION, location);
            startActivityForResult(intent, CANCEL_INTERVIEW_REQUEST_CODE);
        }
    };

    public interface OnItemClickListener {
        void onClickInterviewDetail(String projectId, long interviewSeq);

        void onClickCancelInterview(String projectId, long interviewSeq, String timeSlot, String projectName, String interviewStatus, Date interviewDate, String location);
    }
}

