package com.appbee.appbeemobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.holder.HeaderViewHolder;
import com.appbee.appbeemobile.adapter.holder.ItemViewHolder;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClabAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static final int HEADER_VIEW_TYPE = 0;
    static final int ITEM_VIEW_TYPE = 1;

    private Context context;
    private List<Project> projectList;

    @Inject
    public ClabAppsAdapter(Context context, ProjectService projectService) {
        this.context = context;

        projectList = new ArrayList<>();
        projectService.getClabProjects().subscribe(projectList -> {
            this.projectList = projectList;
            notifyDataSetChanged();
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false), HeaderViewHolder.HEADER_TYPE_CLAB);
        } else {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false), context);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_VIEW_TYPE;
        } else {
            return ITEM_VIEW_TYPE;
        }
    }

    public Project getItem(int position) {
        return projectList.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Project project = projectList.get(position - 1);
            ((ItemViewHolder) holder).bind(project);
        }
    }

    @Override
    public int getItemCount() {
        return this.projectList.size() + 1;
    }
}