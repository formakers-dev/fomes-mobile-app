package com.appbee.appbeemobile.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.InterviewDetailActivity;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class InterviewListItemViewHolder extends RecyclerView.ViewHolder {
    Context context;
    View mView;
    TextView itemCardTagTextView;
    ImageView imageView;
    TextView introduceTextView;
    TextView nameTextView;
    String projectId;
    long seq;

    public InterviewListItemViewHolder(View view, Context context) {
        super(view);
        this.context = context;
        mView = view;
        itemCardTagTextView = (TextView) view.findViewById(R.id.project_tag);
        imageView = (ImageView) view.findViewById(R.id.project_image);
        introduceTextView = (TextView) view.findViewById(R.id.project_introduce);
        nameTextView = (TextView) view.findViewById(R.id.project_name);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, InterviewDetailActivity.class);
            intent.putExtra(EXTRA.PROJECT_ID, projectId);
            intent.putExtra(EXTRA.INTERVIEW_SEQ, seq);
            context.startActivity(intent);
        });
    }

    public void bind(@NonNull Project project) {
        Glide.with(context).load(project.getImage().getUrl()).apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(imageView);
        introduceTextView.setText(project.getIntroduce());
        nameTextView.setText(project.getName());
        projectId = project.getProjectId();
        seq = project.getInterview().getSeq();
    }
}
