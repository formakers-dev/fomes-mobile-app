package com.appbee.appbeemobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.holder.ProjectListHeaderViewHolder;
import com.appbee.appbeemobile.adapter.holder.ProjectListItemViewHolder;
import com.appbee.appbeemobile.model.Project;

import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Project> projectList;

    static final int HEADER_VIEW_TYPE = 0;
    static final int ITEM_VIEW_TYPE = 1;

    public ProjectListAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            return new ProjectListHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
        } else {
            return new ProjectListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false), parent.getContext());
        }
    }

    public void setProjectList(List<Project> projectList) {
        if(this.projectList != null) {
            this.projectList.clear();
        }

        this.projectList = projectList;

        this.notifyDataSetChanged();
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
