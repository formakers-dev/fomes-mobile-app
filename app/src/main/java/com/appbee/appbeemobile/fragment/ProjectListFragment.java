package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.ProjectListAdapter;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class ProjectListFragment extends BaseFragment {

    @BindView(R.id.project_list_recycler_view)
    RecyclerView projectListRecyclerView;

    @Inject
    ProjectService projectService;

    List<Project> projectList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppBeeApplication) getActivity().getApplication()).getComponent().inject(this);
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProjectListAdapter projectListAdapter = new ProjectListAdapter(projectList, R.string.project_list_header_title, R.string.project_list_header_subtitle);

        GridLayoutManager projectListLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        projectListLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return projectListAdapter.isHeader(position) ? projectListLayoutManager.getSpanCount() : 1;
            }
        });
        projectListRecyclerView.setLayoutManager(projectListLayoutManager);
        projectListRecyclerView.setAdapter(projectListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        projectService.getAllProjects()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    projectList.clear();
                    projectList.addAll(result);
                    projectListRecyclerView.getAdapter().notifyDataSetChanged();
                });
    }
}
