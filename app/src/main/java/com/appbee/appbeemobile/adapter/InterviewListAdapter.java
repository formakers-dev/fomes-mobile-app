package com.appbee.appbeemobile.adapter;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.holder.ContentsListHeaderViewHolder;
import com.appbee.appbeemobile.adapter.holder.InterviewListItemViewHolder;
import com.appbee.appbeemobile.model.Project;

import java.util.List;

public class InterviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int HEADER_VIEW_TYPE = 0;
    static final int ITEM_VIEW_TYPE = 1;

    private final List<Project> projectList;
    private final
    @StringRes
    int headerTitle;
    private final
    @StringRes
    int headerSubTitle;

    public InterviewListAdapter(List<Project> projectList, int headerTitle, int headerSubTitle) {
        this.projectList = projectList;
        this.headerTitle = headerTitle;
        this.headerSubTitle = headerSubTitle;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            ((TextView) headerView.findViewById(R.id.item_header_title)).setText(headerTitle);
            ((TextView) headerView.findViewById(R.id.item_header_subtitle)).setText(headerSubTitle);
            return new ContentsListHeaderViewHolder(headerView);
        } else {
            return new InterviewListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false), parent.getContext());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? HEADER_VIEW_TYPE : ITEM_VIEW_TYPE;
    }

    public Project getItem(int position) {
        return projectList.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InterviewListItemViewHolder) {
            Project project = projectList.get(position - 1);
            ((InterviewListItemViewHolder) holder).bind(project);
        }
    }

    @Override
    public int getItemCount() {
        return this.projectList.size() + 1;
    }
}
