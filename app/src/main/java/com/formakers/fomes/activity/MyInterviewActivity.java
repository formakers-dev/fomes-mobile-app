package com.formakers.fomes.activity;

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

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.adapter.RegisteredInterviewListAdapter;
import com.formakers.fomes.helper.ResourceHelper;
import com.formakers.fomes.helper.TimeHelper;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.common.network.ProjectService;
import com.formakers.fomes.util.FomesConstants;

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

    @Inject
    ResourceHelper resourceHelper;

    @BindView(R.id.interview_recycler_view)
    RecyclerView interviewRecyclerView;

    @BindView(R.id.empty_content_text_view)
    View emptyContentTextView;

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

        RegisteredInterviewListAdapter registeredInterviewListAdapter = new RegisteredInterviewListAdapter(projectList, timeHelper, onItemClickListener, resourceHelper);
        interviewRecyclerView.setAdapter(registeredInterviewListAdapter);

        requestRegisteredInterviews();
    }

    private void requestRegisteredInterviews() {
        addToCompositeSubscription(
                projectService.getRegisteredInterviews()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(projectList -> {
                            displayInterviewsLayout(projectList == null || projectList.isEmpty());
                            if (interviewRecyclerView.getVisibility() == View.VISIBLE) {
                                refreshInterviewRecyclerView(projectList);
                            }
                        }, this::logError)
        );
    }

    private void displayInterviewsLayout(boolean isEmpty) {
        interviewRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyContentTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
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
            intent.putExtra(FomesConstants.EXTRA.PROJECT_ID, projectId);
            intent.putExtra(FomesConstants.EXTRA.INTERVIEW_SEQ, interviewSeq);
            startActivity(intent);
        }

        @Override
        public void onClickCancelInterview(String projectId, long interviewSeq, String timeSlot, String projectName, String interviewStatus, Date interviewDate, String location) {
            Intent intent = new Intent(MyInterviewActivity.this, CancelInterviewActivity.class);
            intent.putExtra(FomesConstants.EXTRA.PROJECT_ID, projectId);
            intent.putExtra(FomesConstants.EXTRA.INTERVIEW_SEQ, interviewSeq);
            intent.putExtra(FomesConstants.EXTRA.TIME_SLOT, timeSlot);
            intent.putExtra(FomesConstants.EXTRA.PROJECT_NAME, projectName);
            intent.putExtra(FomesConstants.EXTRA.INTERVIEW_STATUS, interviewStatus);
            intent.putExtra(FomesConstants.EXTRA.INTERVIEW_DATE, interviewDate);
            intent.putExtra(FomesConstants.EXTRA.LOCATION, location);
            startActivityForResult(intent, CANCEL_INTERVIEW_REQUEST_CODE);
        }
    };

    public interface OnItemClickListener {
        void onClickInterviewDetail(String projectId, long interviewSeq);

        void onClickCancelInterview(String projectId, long interviewSeq, String timeSlot, String projectName, String interviewStatus, Date interviewDate, String location);
    }
}

