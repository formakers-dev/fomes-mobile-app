package com.appbee.appbeemobile.adapter;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.holder.ContentsListHeaderViewHolder;
import com.appbee.appbeemobile.adapter.holder.ProjectListItemViewHolder;
import com.appbee.appbeemobile.model.Project;

import java.util.List;

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int HEADER_VIEW_TYPE = 0;
    static final int ITEM_VIEW_TYPE = 1;

    private final List<Project> projectList;
    private View headerView;

    public MainListAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            return new ContentsListHeaderViewHolder(headerView);
        } else {
            return new ProjectListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false), parent.getContext());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? HEADER_VIEW_TYPE : ITEM_VIEW_TYPE;
    }

    public Project getItem(int position) {
        if (position == 0) {
            throw new IllegalArgumentException("this is a header!");
        }

        return projectList.get(position - 1);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectListItemViewHolder) {
            Project project = projectList.get(position - 1);
            ((ProjectListItemViewHolder) holder).bind(project);
        }
    }

    @Override
    public int getItemCount() {
        return this.projectList.size() + 1;
    }
}
