package com.formakers.fomes.betatest;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_FRAGMENT_TAP_BUG_REPORT;

public class BetaTestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BetaTestListAdapterContract.Model, BetaTestListAdapterContract.View {

    private static final String TAG = "BetaTestListAdapter";

    private Context context;
    private OnRecyclerItemClickListener itemClickListener;

    private List<BetaTest> betaTests = new ArrayList<>();

    private BetaTestContract.Presenter presenter;

    /**
     * View
     */

    @Override
    public void setPresenter(BetaTestContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resources res = context.getResources();
        BetaTest item = betaTests.get(position);

        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;

        baseViewHolder.titleTextView.setText(item.getTitle());

        baseViewHolder.subTitleTextView.setText(item.getDisplayDescription());

        baseViewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));

        // 디데이
        long remainDays = item.getRemainDays();

        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format("D - %d", remainDays);
        } else if (remainDays == 0) {
            projectStatus = "오늘 마감";
        } else {
            projectStatus = context.getString(R.string.common_close);
        }

        baseViewHolder.projectStatusTextView.setText(projectStatus);

        @ColorRes int projectStatusColorId;
        if (remainDays < 2) {
            projectStatusColorId = R.color.fomes_red;
        } else if (remainDays < 4) {
            projectStatusColorId = R.color.fomes_squash;
        } else {
            projectStatusColorId = R.color.colorPrimary;
        }

        baseViewHolder.projectStatusTextView.setVisibility(View.VISIBLE);
        baseViewHolder.projectStatusTextView.setTextColor(res.getColor(projectStatusColorId));

        // end of 디데이

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        Glide.with(context).load(item.getOverviewImageUrl())
                .apply(new RequestOptions()
                        .centerCrop()
                        .transform(new RoundedCorners(4))
                        .placeholder(new ColorDrawable(res.getColor(R.color.fomes_deep_gray))))
                .into(itemViewHolder.overviewImageView);

        if (TextUtils.isEmpty(item.getBugReportUrl())) {
            itemViewHolder.reportBugButton.setVisibility(View.GONE);
        } else {
            itemViewHolder.reportBugButton.setVisibility(View.VISIBLE);
            itemViewHolder.reportBugButton.setOnClickListener(v -> {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(presenter.getInterpretedUrl(item.getBugReportUrl()))));

                this.presenter.sendEventLog(BETA_TEST_FRAGMENT_TAP_BUG_REPORT, item.getId());
            });
        }

        int progressRate = Math.round(item.getCompletedItemCount() * 100 / item.getTotalItemCount());
        itemViewHolder.progressBar.setProgress(progressRate);

        if (item.isCompleted()) {
            itemViewHolder.progressLabelTextView.setVisibility(View.GONE);
            itemViewHolder.progressTextView.setText(item.getProgressText().getDone());
        } else {
            itemViewHolder.progressLabelTextView.setVisibility(View.VISIBLE);
            itemViewHolder.progressLabelTextView.setBackground(res.getDrawable(R.drawable.item_rect_rounded_corner_background,
                    new ContextThemeWrapper(context, progressRate > 0 ? R.style.BetaTestTheme_ProgressLabelBackground_Doing : R.style.BetaTestTheme_ProgressLabelBackground).getTheme()));
            itemViewHolder.progressLabelTextView.setTextColor(progressRate > 0 ? res.getColor(R.color.fomes_dark_gray) : res.getColor(R.color.fomes_warm_gray_2));
            itemViewHolder.progressLabelTextView.setText(progressRate > 0 ? R.string.betatest_progress_attend_label_doing : R.string.betatest_progress_attend_label);
            itemViewHolder.progressTextView.setText(progressRate > 0 ? item.getProgressText().getDoing() : item.getProgressText().getReady());
        }

        itemViewHolder.attendLabelImageView.setVisibility(item.isCompleted() ? View.VISIBLE : View.GONE);

        baseViewHolder.titleTextView.setTextColor(item.isCompleted() ? res.getColor(R.color.colorPrimary) : res.getColor(R.color.fomes_white));
        baseViewHolder.subTitleTextView.setTextColor(item.isCompleted() ? res.getColor(R.color.colorPrimary) : res.getColor(R.color.fomes_light_gray));

    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getPositionById(String id) {
        for (BetaTest betaTest : betaTests) {
            if (TextUtils.equals(id, betaTest.getId())) {
                return betaTests.indexOf(betaTest);
            }
        }

        return -1;
    }

    /**
     * Model
     */

    @Override
    public int getItemCount() {
        return betaTests.size();
    }

    @Override
    public Object getItem(int position) {
        return betaTests.get(position);
    }

    @Override
    public List<BetaTest> getAllItems() {
        return betaTests;
    }

    @Override
    public void add(BetaTest item) {
        betaTests.add(item);
    }

    @Override
    public void addAll(List<BetaTest> items) {
        betaTests.addAll(items);
    }

    @Override
    public void clear() {
        betaTests.clear();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subTitleTextView;
        TextView projectStatusTextView;
        @Deprecated View disableBackgroundView;
        @Deprecated TextView requiredTimeTextView;
        @Deprecated TextView amountTextView;
        @Deprecated TextView rewardTextView;
        @Deprecated View completedLabelView;
        @Deprecated TextView completedLabelTextView;
        @Deprecated View closedLabelView;
        @Deprecated TextView closedLabelTextView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            projectStatusTextView = itemView.findViewById(R.id.betatest_project_status);
            disableBackgroundView = itemView.findViewById(R.id.betatest_disable_background);
            requiredTimeTextView = itemView.findViewById(R.id.betatest_required_time);
            amountTextView = itemView.findViewById(R.id.betatest_amount);
            rewardTextView = itemView.findViewById(R.id.betatest_reward);
            completedLabelView = itemView.findViewById(R.id.betatest_completed_label);
            completedLabelTextView = itemView.findViewById(R.id.betatest_completed_label_textview);
            closedLabelView = itemView.findViewById(R.id.betatest_closed_label);
            closedLabelTextView = itemView.findViewById(R.id.betatest_closed_label_textview);
        }
    }

    class ItemViewHolder extends BaseViewHolder {
        ImageView overviewImageView;
        TextView progressTextView;
        TextView progressLabelTextView;
        ProgressBar progressBar;
        ImageView attendLabelImageView;
        TextView reportBugButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            overviewImageView = itemView.findViewById(R.id.betatest_overview_imageview);
            progressTextView = itemView.findViewById(R.id.betatest_progress_textview);
            progressLabelTextView = itemView.findViewById(R.id.betatest_progress_label);
            progressBar = itemView.findViewById(R.id.betatest_progress_bar);
            attendLabelImageView = itemView.findViewById(R.id.betatest_label);
            reportBugButton = itemView.findViewById(R.id.betatest_bug_button);
        }
    }
}