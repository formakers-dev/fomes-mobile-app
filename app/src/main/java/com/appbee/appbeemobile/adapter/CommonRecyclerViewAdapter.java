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

import java.util.List;

public class CommonRecyclerViewAdapter extends RecyclerView.Adapter<CommonRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Project> projectList;

    public CommonRecyclerViewAdapter(Context context, List<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);



        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.projectList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView;
        TextView statusTextView;
        TextView introduceTextView;
        TextView nameTextView;

        ViewHolder(View view) {
            super(view);

            titleTextView = (TextView) view.findViewById(R.id.project_title);
            imageView = (ImageView) view.findViewById(R.id.project_image);
            statusTextView = (TextView) view.findViewById(R.id.project_status);
            introduceTextView = (TextView) view.findViewById(R.id.project_introduce);
            nameTextView = (TextView) view.findViewById(R.id.project_name);
        }
    }
}
