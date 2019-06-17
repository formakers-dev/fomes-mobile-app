package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.constant.Feature;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.view.adapter.listener.OnRecyclerItemClickListener;
import com.formakers.fomes.main.contract.FinishedBetaTestContract;
import com.formakers.fomes.main.contract.FinishedBetaTestListAdapterContract;

import java.util.ArrayList;
import java.util.List;

public class FinishedBetaTestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FinishedBetaTestListAdapterContract.Model, FinishedBetaTestListAdapterContract.View {

    Context context;

    List<BetaTest> betaTestList = new ArrayList<>();

    OnRecyclerItemClickListener itemClickListener;
    FinishedBetaTestContract.Presenter presenter;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        RecyclerView.ViewHolder viewHolder;

        if (isNewDesign()) {
            viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_finished_betatest, parent, false));
        } else {
            viewHolder = new GroupViewHolder(LayoutInflater.from(context).inflate(R.layout.item_group_betatest_old, parent, false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BetaTest item = betaTestList.get(position);

        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;

        baseViewHolder.titleTextView.setText(item.getTitle());

        List<String> targetApps = item.getApps();

        if (targetApps == null || targetApps.isEmpty()) {
            baseViewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), context.getString(R.string.app_name)));
        } else {
            baseViewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), targetApps.get(0)));
        }

        baseViewHolder.testTypeTextView.setText(item.getTags().get(0));

        if (isNewDesign()) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.subTitleTextView.setText(item.getSubTitle());
            viewHolder.periodTextView.setText(String.format("%s ~ %s", item.getOpenDate(), item.getCloseDate()));

            Glide.with(context).load(item.getIconImageUrl())
                    .apply(new RequestOptions().override(76, 76)
                            .centerCrop()
                            .transform(new RoundedCorners(8))
                            .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                    .into(viewHolder.iconImageView);

            viewHolder.labelImageView.setImageResource(item.isCompleted() ? R.drawable.label_attend : R.drawable.label_absent);

            viewHolder.epilogueButton.setText("자세히 보기");

            BetaTest.AfterService afterService = item.getAfterService();

            if (afterService != null) {
                viewHolder.awardTextView.setText(afterService.getAward());
                viewHolder.companySaysTextView.setText(afterService.getCompanySays());
                viewHolder.epilogueButton.setOnClickListener(v -> {
                    Uri uri = Uri.parse(afterService.getEpilogue());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                });
                viewHolder.epilogueButton.setEnabled(true);
            } else {
                viewHolder.awardGroup.setVisibility(View.GONE);
                viewHolder.companySaysTextView.setText(R.string.betatest_company_says_not_collected);
                viewHolder.epilogueButton.setOnClickListener(null);
                viewHolder.epilogueButton.setEnabled(false);
            }

            if (!Feature.BETATEST_GROUP_DATA_MIGRATION && Feature.FOMES_V_2_5_DESIGN) {
                baseViewHolder.targetTextView.setVisibility(View.VISIBLE);
                baseViewHolder.testTypeTextView.setVisibility(View.VISIBLE);
                viewHolder.subTitleTextView.setVisibility(View.GONE);
            }

        } else {
            GroupViewHolder viewHolder = (GroupViewHolder) holder;

            baseViewHolder.targetTextView.setVisibility(View.VISIBLE);
            baseViewHolder.testTypeTextView.setVisibility(View.VISIBLE);

            baseViewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));
            baseViewHolder.itemView.setEnabled(item.isOpened() && !item.isCompleted());

            long remainDays = item.getRemainDays();

            String projectStatus;
            if (remainDays > 0) {
                projectStatus = String.format(context.getString(R.string.betatest_project_status_format), remainDays);
            } else if (remainDays == 0) {
                projectStatus = context.getString(R.string.beta_test_today_close);
            } else {
                projectStatus = context.getString(R.string.common_close);
            }
            viewHolder.projectStatusTextView.setText(projectStatus);
            viewHolder.disableBackgroundView.setVisibility(!baseViewHolder.itemView.isEnabled() ? View.VISIBLE : View.GONE);

            viewHolder.stampImageView.setVisibility(View.VISIBLE);
            viewHolder.stampImageView.setImageResource(item.isCompleted() ? R.drawable.label_attend : R.drawable.label_absent);

            // for 완료 여부
            if (item.isCompleted()) {
                viewHolder.progressTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_white));
                viewHolder.progressSubTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_warm_gray_2));
                viewHolder.companySaysTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                viewHolder.progressTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_warm_gray));
                viewHolder.progressSubTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_warm_gray));
                viewHolder.companySaysTextView.setTextColor(context.getResources().getColor(R.color.fomes_warm_gray));
            }

            // for flag
            if (item.getAfterService() != null && item.isCompleted()) {
                // 전달 완료
                viewHolder.progressTitleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_completed_flag, 0, R.drawable.icon_completed_flag_reverse, 0);
                viewHolder.progressTitleTextView.setText(R.string.betatest_delivered_my_feedback);
            } else if (item.isCompleted()) {
                // 제출 완료
                viewHolder.progressTitleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_racing_flag, 0, R.drawable.icon_racing_flag_reverse, 0);
                viewHolder.progressTitleTextView.setText(R.string.betatest_submitted_my_feedback);
            } else {
                // 미완료
                viewHolder.progressTitleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_grey_flag, 0, R.drawable.icon_grey_flag_reverse, 0);
                viewHolder.progressTitleTextView.setText(R.string.betatest_not_submitted_my_feedback);
            }

            // for as
            if (item.getAfterService() != null) {
                viewHolder.progressSubTitleTextView.setText(R.string.betatest_delivered_all_results);
                viewHolder.epilogueButton.setText(R.string.betatest_epilogue_opened);

                viewHolder.epilogueButtonTextView.setVisibility(View.GONE);
                viewHolder.epilogueButtonIcon.setVisibility(View.GONE);
                viewHolder.epilogueButton.setEnabled(true);
                viewHolder.companySaysTextView.setText(String.format(context.getString(R.string.betatest_company_says), item.getAfterService().getCompanySays()));

                viewHolder.epilogueButton.setOnClickListener(v -> {
                    presenter.getAnalytics().sendClickEventLog(FomesConstants.BetaTest.Log.TARGET_EPILOGUE_BUTTON, item.getObjectId());

                    Uri uri = Uri.parse(item.getAfterService().getEpilogue());

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                });
            } else {
                viewHolder.progressSubTitleTextView.setText(R.string.betatest_collecting_results);
                viewHolder.epilogueButton.setVisibility(View.VISIBLE);
                viewHolder.epilogueButton.setText("");
                viewHolder.epilogueButtonTextView.setVisibility(View.VISIBLE);
                viewHolder.epilogueButtonIcon.setVisibility(View.VISIBLE);
                viewHolder.epilogueButton.setEnabled(false);

                viewHolder.companySaysTextView.setText(R.string.betatest_company_says_not_collected);

                viewHolder.epilogueButton.setOnClickListener(null);
            }
        }
    }

    private boolean isNewDesign() {
        return Feature.BETATEST_GROUP_DATA_MIGRATION || Feature.FOMES_V_2_5_DESIGN;
    }

    @Override
    public int getItemCount() {
        return betaTestList.size();
    }

    @Override
    public Object getItem(int position) {
        return betaTestList.get(position);
    }

    @Override
    public List<BetaTest> getAllItems() {
        return betaTestList;
    }

    @Override
    public void add(BetaTest item) {
        betaTestList.add(item);
    }

    @Override
    public void addAll(List<BetaTest> items) {
        betaTestList.addAll(items);
    }

    @Override
    public void clear() {
        betaTestList.clear();
    }

    @Override
    public void setPresenter(FinishedBetaTestContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.itemClickListener = listener;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        @Deprecated TextView targetTextView;
        @Deprecated TextView testTypeTextView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            targetTextView = itemView.findViewById(R.id.betatest_target);
            testTypeTextView = itemView.findViewById(R.id.betatest_test_type);
        }
    }

    class ViewHolder extends BaseViewHolder {
        TextView companySaysTextView;
        ImageView labelImageView;
        ImageView iconImageView;
        TextView subTitleTextView;
        TextView periodTextView;
        Group awardGroup;
        TextView awardTextView;
        Button epilogueButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companySaysTextView = itemView.findViewById(R.id.betatest_finished_company_says);
            labelImageView = itemView.findViewById(R.id.betatest_label);
            iconImageView = itemView.findViewById(R.id.betatest_icon_imageview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            periodTextView = itemView.findViewById(R.id.betatest_period_textview);
            awardGroup = itemView.findViewById(R.id.betatest_award_group);
            awardTextView = itemView.findViewById(R.id.betatest_award_contents);
            epilogueButton = itemView.findViewById(R.id.betatest_finished_epilogue_button);
        }
    }

    @Deprecated
    class GroupViewHolder extends BaseViewHolder {
        TextView projectStatusTextView;
        ImageView stampImageView;
        TextView progressTitleTextView;
        TextView progressSubTitleTextView;
        Button epilogueButton;
        ImageView epilogueButtonIcon;
        TextView epilogueButtonTextView;
        TextView companySaysTextView;
        View disableBackgroundView;

        public GroupViewHolder(View itemView) {
            super(itemView);
            disableBackgroundView = itemView.findViewById(R.id.betatest_disable_background);
            projectStatusTextView = itemView.findViewById(R.id.betatest_project_status);
            stampImageView = itemView.findViewById(R.id.betatest_label);
            progressTitleTextView = itemView.findViewById(R.id.betatest_finished_progress_title);
            progressSubTitleTextView = itemView.findViewById(R.id.betatest_finished_progress_subtitle);
            epilogueButton = itemView.findViewById(R.id.betatest_finished_epilogue_button);
            epilogueButtonIcon = itemView.findViewById(R.id.betatest_finished_epilogue_button_icon);
            epilogueButtonTextView = itemView.findViewById(R.id.betatest_finished_epilogue_button_text);
            companySaysTextView = itemView.findViewById(R.id.betatest_finished_company_says);
        }
    }
}
