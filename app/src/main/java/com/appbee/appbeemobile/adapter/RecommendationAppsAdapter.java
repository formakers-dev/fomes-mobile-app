package com.appbee.appbeemobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.adapter.holder.HeaderViewHolder;
import com.appbee.appbeemobile.adapter.holder.ItemViewHolder;
import com.appbee.appbeemobile.model.Project;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecommendationAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Project> projectList = new ArrayList<>();

    static final int HEADER_VIEW_TYPE = 0;
    static final int ITEM_VIEW_TYPE = 1;

    @Inject
    public RecommendationAppsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false), HeaderViewHolder.HEADER_TYPE_RECOMMENDATION);
        } else {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false), context);
        }
    }

    public void setProjectList(List<Project> projectList) {
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
