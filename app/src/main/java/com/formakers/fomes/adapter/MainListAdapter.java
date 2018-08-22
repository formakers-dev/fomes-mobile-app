package com.formakers.fomes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.adapter.holder.ContentsListHeaderViewHolder;
import com.formakers.fomes.adapter.holder.InterviewListItemViewHolder;
import com.formakers.fomes.adapter.holder.ProjectListItemViewHolder;
import com.formakers.fomes.model.Project;

import java.util.List;

// TODO : 변경 필요 - 변경 포인트 : 헤더, 아이템뷰타입
public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int HEADER_VIEW_TYPE = 0;
    public static final int PROJECT_ITEM_VIEW_TYPE = 1;
    public static final int INTERVIEW_ITEM_VIEW_TYPE = 2;

    private final List<Project> projectList;
    private View headerView;
    private int itemViewType;

    public MainListAdapter(List<Project> projectList, int itemViewType) {
        this.projectList = projectList;
        this.itemViewType = itemViewType;
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
        } else if (viewType == PROJECT_ITEM_VIEW_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item_card, parent, false);
            return new ProjectListItemViewHolder(itemView, parent.getContext());
        } else if (viewType == INTERVIEW_ITEM_VIEW_TYPE){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.interview_item_card, parent, false);
            return new InterviewListItemViewHolder(itemView, parent.getContext());
        } else {
            throw new IllegalArgumentException("itemViewType is wrong!");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? HEADER_VIEW_TYPE : itemViewType;
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
        } else if (holder instanceof InterviewListItemViewHolder) {
            Project project = projectList.get(position - 1);
            ((InterviewListItemViewHolder) holder).bind(project);
        }
    }

    @Override
    public int getItemCount() {
        return this.projectList.size() + 1;
    }
}
