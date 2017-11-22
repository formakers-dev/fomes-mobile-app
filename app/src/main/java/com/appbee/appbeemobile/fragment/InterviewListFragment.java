package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.InterviewListAdapter;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class InterviewListFragment extends BaseFragment {

    @BindView(R.id.interview_list_recycler_view)
    RecyclerView interviewListRecyclerView;

    @Inject
    ProjectService projectService;

    private List<Project> interviewList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppBeeApplication) getActivity().getApplication()).getComponent().inject(this);
        return inflater.inflate(R.layout.fragment_interview_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager interviewLayoutManger = new LinearLayoutManager(getActivity());
        interviewLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        interviewListRecyclerView.setLayoutManager(interviewLayoutManger);
        interviewListRecyclerView.setAdapter(new InterviewListAdapter(interviewList, R.string.recommendation_apps_title, R.string.recommendation_apps_subtitle));
    }

    @Override
    public void onResume() {
        super.onResume();

        projectService.getAllInterviews().subscribe(result -> {
            interviewList.clear();
            interviewList.addAll(result);
            interviewListRecyclerView.getAdapter().notifyDataSetChanged();
        });
    }
}
