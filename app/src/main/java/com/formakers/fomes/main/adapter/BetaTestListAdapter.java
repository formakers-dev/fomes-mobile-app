package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.Feature;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.view.adapter.listener.OnRecyclerItemClickListener;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;

import java.util.ArrayList;
import java.util.List;

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
        BetaTest item = betaTests.get(position);

        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;

        baseViewHolder.titleTextView.setText(item.getTitle());
        baseViewHolder.subTitleTextView.setText(item.getSubTitle());

        baseViewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));

        // Enable 처리
        baseViewHolder.itemView.setEnabled(item.isOpened() && !item.isCompleted());

        baseViewHolder.requiredTimeTextView.setText(String.format(context.getString(R.string.betatest_required_time_format), item.getRequiredTime(DateUtil.CONVERT_TYPE_MINUTES)));
        baseViewHolder.amountTextView.setText(item.getAmount());

        String reward = item.getReward();
        if (TextUtils.isEmpty(reward)) {
            baseViewHolder.rewardTextView.setText(R.string.betatest_reward_none);
        } else {
            baseViewHolder.rewardTextView.setText(reward);
        }

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

        @StyleRes int projectStatusStyleId;
        @ColorRes int projectStatusColorId;
        if (remainDays < 2) {
            projectStatusStyleId = R.style.BetaTestTheme_TagBackground_Red;
            projectStatusColorId = R.color.fomes_red;
        } else if (remainDays < 4) {
            projectStatusStyleId = R.style.BetaTestTheme_TagBackground_Squash;
            projectStatusColorId = R.color.fomes_squash;
        } else {
            projectStatusStyleId = R.style.BetaTestTheme_TagBackground;
            projectStatusColorId = R.color.colorPrimary;
        }

        baseViewHolder.projectStatusTextView.setVisibility(View.VISIBLE);
        baseViewHolder.projectStatusTextView.setBackground(context.getResources().getDrawable(R.drawable.item_rect_rounded_corner_background,
                new ContextThemeWrapper(context, projectStatusStyleId).getTheme()));
        baseViewHolder.projectStatusTextView.setTextColor(context.getResources().getColor(projectStatusColorId));

        // end of 디데이

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        Glide.with(context).load(item.getOverviewImageUrl())
                .apply(new RequestOptions()
                        .centerCrop()
                        .transform(new RoundedCorners(4))
                        .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                .into(itemViewHolder.overviewImageView);

        if (TextUtils.isEmpty(item.getBugReportUrl())) {
            itemViewHolder.reportBugButton.setVisibility(View.GONE);
        }

        if (Feature.FOMES_V_2_5_TEMPORARY_DESIGN) {
            baseViewHolder.disableBackgroundView.setVisibility(!baseViewHolder.itemView.isEnabled() ? View.VISIBLE : View.GONE);

            itemViewHolder.progressBar.setVisibility(View.GONE);
            itemViewHolder.progressTextView.setVisibility(View.GONE);
            itemViewHolder.progressLabelTextView.setVisibility(View.GONE);

            baseViewHolder.requiredTimeTextView.setVisibility(View.VISIBLE);
            baseViewHolder.amountTextView.setVisibility(View.VISIBLE);
            baseViewHolder.rewardTextView.setVisibility(View.VISIBLE);

            itemViewHolder.completedLabelTextView.setText(String.format(context.getString(R.string.beta_test_completed_label_title), item.getTags().get(0)));
            itemViewHolder.completedLabelView.setVisibility(item.isCompleted() ? View.VISIBLE : View.GONE);
            itemViewHolder.closedLabelTextView.setText(String.format(context.getString(R.string.beta_test_closed_label_title), item.getTags().get(0)));
            itemViewHolder.closedLabelView.setVisibility(!item.isOpened() && !item.isCompleted() ? View.VISIBLE : View.GONE);
        } else {
            itemViewHolder.progressLabelTextView.setVisibility(item.isCompleted() ? View.GONE : View.VISIBLE);

            itemViewHolder.attendLabelImageView.setVisibility(item.isCompleted() ? View.VISIBLE : View.GONE);

            baseViewHolder.titleTextView.setTextColor(item.isCompleted() ? context.getResources().getColor(R.color.colorPrimary) : context.getResources().getColor(R.color.fomes_white));
            baseViewHolder.subTitleTextView.setTextColor(item.isCompleted() ? context.getResources().getColor(R.color.colorPrimary) : context.getResources().getColor(R.color.fomes_light_gray));

        }
    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getPositionById(int id) {
        for (BetaTest betaTest : betaTests) {
            if (betaTest.getId() == id) {
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
