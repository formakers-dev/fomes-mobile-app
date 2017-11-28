package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.MainListAdapter;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.view.decorator.ContentDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

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

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.blank_divier, null));
        interviewListRecyclerView.addItemDecoration(dividerItemDecoration);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_header, interviewListRecyclerView, false);
        ((TextView) headerView.findViewById(R.id.item_header_title)).setText(R.string.recommendation_apps_title);
        ((TextView) headerView.findViewById(R.id.item_header_subtitle)).setText(R.string.recommendation_apps_subtitle);

        MainListAdapter mainListAdapter = new MainListAdapter(interviewList, MainListAdapter.INTERVIEW_ITEM_VIEW_TYPE);
        mainListAdapter.setHeaderView(headerView);

        interviewListRecyclerView.setAdapter(mainListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        projectService.getAllInterviews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    interviewList.clear();
                    interviewList.addAll(result);
                    interviewListRecyclerView.getAdapter().notifyDataSetChanged();
                });
    }
}
