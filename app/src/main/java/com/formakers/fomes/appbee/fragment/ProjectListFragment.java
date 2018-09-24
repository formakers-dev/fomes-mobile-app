package com.formakers.fomes.appbee.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.appbee.adapter.MainListAdapter;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.common.network.ProjectService;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class ProjectListFragment extends BaseFragment {

    @BindView(R.id.main_list_recycler_view)
    RecyclerView projectListRecyclerView;

    @BindView(R.id.empty_content_text_view)
    TextView emptyContentTextView;

    @Inject
    ProjectService projectService;

    List<Project> projectList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppBeeApplication) getActivity().getApplication()).getComponent().inject(this);
        return inflater.inflate(R.layout.fragment_main_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyContentTextView.setText(R.string.empty_project);

        MainListAdapter mainListAdapter = new MainListAdapter(projectList, MainListAdapter.PROJECT_ITEM_VIEW_TYPE);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.GRID);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.project_list_divider, null));
        projectListRecyclerView.addItemDecoration(dividerItemDecoration);

        GridLayoutManager projectListLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        projectListLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mainListAdapter.isHeader(position) ? projectListLayoutManager.getSpanCount() : 1;
            }
        });
        projectListRecyclerView.setLayoutManager(projectListLayoutManager);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_header, projectListRecyclerView, false);
        ((TextView) headerView.findViewById(R.id.item_header_title)).setText(R.string.project_list_header_title);
        ((TextView) headerView.findViewById(R.id.item_header_subtitle)).setText(R.string.project_list_header_subtitle);
        mainListAdapter.setHeaderView(headerView);

        projectListRecyclerView.setAdapter(mainListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        addCompositeSubscription(
                projectService.getAllProjects()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(projectList -> {
                            displayEmptyContentTextView(projectList == null || projectList.isEmpty());
                            refreshProjectRecyclerView(projectList);
                        }, this::logError)
        );
    }

    private void displayEmptyContentTextView(boolean isEmpty) {
        emptyContentTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void refreshProjectRecyclerView(List<Project> projectList) {
        this.projectList.clear();
        this.projectList.addAll(projectList);
        projectListRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
