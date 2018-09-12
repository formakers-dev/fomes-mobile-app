package com.formakers.fomes.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.activity.ProjectDetailActivity;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.util.FomesConstants.EXTRA;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ProjectListItemViewHolder extends RecyclerView.ViewHolder {
    Context context;
    View mView;
    TextView itemCardTagTextView;
    ImageView imageView;
    TextView introduceTextView;
    TextView nameTextView;
    String projectId;

    public ProjectListItemViewHolder(View view, Context context) {
        super(view);
        this.context = context;
        mView = view;
        itemCardTagTextView = (TextView) view.findViewById(R.id.project_description);
        imageView = (ImageView) view.findViewById(R.id.project_image);
        introduceTextView = (TextView) view.findViewById(R.id.project_introduce);
        nameTextView = (TextView) view.findViewById(R.id.project_name);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProjectDetailActivity.class);
            intent.putExtra(EXTRA.PROJECT_ID, projectId);
            context.startActivity(intent);
        });
    }

    public void bind(@NonNull Project project) {
        // TODO : 앱 이름, 이미지 패스 유효하지 않거나 여러개인 경우 처리
//        if(project.getApps() != null && project.getApps().size() > 0 ) {
//            projectDescriptionTextView.setText(String.format(context.getString(R.string.item_card_tag), project.getApps().get(0)));
//        }
        Glide.with(context).load(project.getImage().getUrl())
                .apply(new RequestOptions().override(1300, 1000).centerCrop())
                .into(imageView);
        introduceTextView.setText(project.getIntroduce());
        nameTextView.setText(project.getName());
        projectId = project.getProjectId();
    }
}
