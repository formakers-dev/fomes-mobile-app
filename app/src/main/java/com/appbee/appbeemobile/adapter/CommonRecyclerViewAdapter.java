package com.appbee.appbeemobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.Project;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public abstract class CommonRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Project> projectList;

    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;

    public CommonRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false));
        } else {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false));
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Project project = projectList.get(position - 1);

            // TODO : 앱 이름, 이미지 패스 유효하지 않거나 여러개인 경우 처리
            if(project.getApps() != null && project.getApps().size() > 0 ) {
                ((ItemViewHolder) holder).itemCardTagTextView.setText(String.format(context.getString(R.string.item_card_tag), project.getApps().get(0)));
            }
            if(project.getImages() != null && project.getImages().size() > 0) {
                Glide.with(context).load(project.getImages().get(0)).apply(new RequestOptions().override(1300, 1000).centerCrop())
                        .into(((ItemViewHolder) holder).imageView);
            }
            ((ItemViewHolder) holder).statusTextView.setText(String.valueOf(project.getStatus()));
            ((ItemViewHolder) holder).introduceTextView.setText(project.getIntroduce());
            ((ItemViewHolder) holder).nameTextView.setText(project.getName());
        }
    }

    @Override
    public int getItemCount() {
        return this.projectList.size() + 1;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subtitleTextView;

        HeaderViewHolder(View view) {
            super(view);

            titleTextView = (TextView) view.findViewById(R.id.recommendation_apps_title);
            subtitleTextView = (TextView) view.findViewById(R.id.recommendation_apps_subtitle);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemCardTagTextView;
        ImageView imageView;
        TextView statusTextView;
        TextView introduceTextView;
        TextView nameTextView;

        ItemViewHolder(View view) {
            super(view);

            itemCardTagTextView = (TextView) view.findViewById(R.id.item_card_tag);
            imageView = (ImageView) view.findViewById(R.id.project_image);
            statusTextView = (TextView) view.findViewById(R.id.project_status);
            introduceTextView = (TextView) view.findViewById(R.id.project_introduce);
            nameTextView = (TextView) view.findViewById(R.id.project_name);
        }
    }
}
