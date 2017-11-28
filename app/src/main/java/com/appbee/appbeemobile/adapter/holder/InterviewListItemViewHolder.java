package com.appbee.appbeemobile.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    private Context context;

    private ImageView projectImageView;
    private TextView projectNameTextView;
    private TextView projectIntroduceTextView;
    private TextView interviewSimilarAppTextView;
    private TextView interviewLocationTextView;
    private TextView interviewTypeTextView;
    private TextView interviewRewardTextView;

    private String projectId;
    private long seq;

    public InterviewListItemViewHolder(View view, Context context) {
        super(view);

        projectImageView = (ImageView) view.findViewById(R.id.project_image);
        projectNameTextView = (TextView) view.findViewById(R.id.project_name);
        projectIntroduceTextView = (TextView) view.findViewById(R.id.project_introduce);
        interviewSimilarAppTextView = (TextView) view.findViewById(R.id.interview_similar_app);
        interviewLocationTextView = (TextView) view.findViewById(R.id.location_textview);
        interviewTypeTextView = (TextView) view.findViewById(R.id.type_textview);
        interviewRewardTextView = (TextView) view.findViewById(R.id.rewards_textview);

        // TODO : View action 작업 뷰쪽으로 옮기기
        this.context = context;
        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, InterviewDetailActivity.class);
            intent.putExtra(EXTRA.PROJECT_ID, projectId);
            intent.putExtra(EXTRA.INTERVIEW_SEQ, seq);
            context.startActivity(intent);
        });
    }

    public void bind(@NonNull Project project) {
        Glide.with(context).load(project.getImage().getUrl())
                .apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(projectImageView);

        projectNameTextView.setText(project.getName());
        projectIntroduceTextView.setText(project.getIntroduce());
        interviewSimilarAppTextView.setText(String.format(interviewSimilarAppTextView.getContext().getString(R.string.recommand_to_user_of_similar_app), project.getInterview().getApps().get(0)));
        interviewLocationTextView.setText(project.getInterview().getLocation());
        interviewTypeTextView.setText(project.getInterview().getType());
        interviewRewardTextView.setText(R.string.rewards_coffee_coupon);

        projectId = project.getProjectId();
        seq = project.getInterview().getSeq();
    }
}
