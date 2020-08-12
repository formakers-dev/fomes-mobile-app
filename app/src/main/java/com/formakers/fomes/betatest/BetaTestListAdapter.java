package com.formakers.fomes.betatest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;
import com.google.android.material.chip.Chip;

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

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest, parent, false));
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resources res = context.getResources();
        BetaTest betaTest = betaTests.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.titleTextView.setText(betaTest.getTitle());
        viewHolder.subTitleTextView.setText(betaTest.getDisplayDescription());

        viewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));

        // 태그
        viewHolder.tagViewGroup.removeAllViews();
        for (String tag : betaTest.getTags()) {
            Chip tagView = (Chip) LayoutInflater.from(context).inflate(R.layout.item_betatest_tag, null);
            tagView.setText(tag);
            viewHolder.tagViewGroup.addView(tagView);
        }

        // 디데이
        viewHolder.projectStatusTextView.setBackgroundResource(R.drawable.label_betatest_dday);
        long remainDays = betaTest.getRemainDays();

        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format("%d 일\n남음", remainDays + 1);
        } else {
            projectStatus = "오늘\n종료";
            viewHolder.projectStatusTextView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.fomes_orange));
        }

        viewHolder.projectStatusTextView.setText(projectStatus);

        viewHolder.projectStatusTextView.setVisibility(View.VISIBLE);

//        viewHolder.projectStatusTextView.setBackground(res.getDrawable(R.drawable.item_rect_rounded_corner_background,
//                new ContextThemeWrapper(context, projectStatusStyleResId).getTheme()));

        // end of 디데이

        this.presenter.getImageLoader().loadImage(
                viewHolder.overviewImageView,
                betaTest.getCoverImageUrl(),
                new RequestOptions()
                .centerCrop()
                .transform(new RoundedCorners(4))
        );

        if (TextUtils.isEmpty(betaTest.getBugReportUrl())) {
            viewHolder.reportBugButton.setVisibility(View.GONE);
        } else {
            viewHolder.reportBugButton.setVisibility(View.VISIBLE);
            viewHolder.reportBugButton.setOnClickListener(v -> {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(presenter.getInterpretedUrl(betaTest.getBugReportUrl()))));

                this.presenter.sendEventLog(BETA_TEST_FRAGMENT_TAP_BUG_REPORT, betaTest.getId());
            });
        }

        viewHolder.titleTextView.setTextColor(betaTest.isCompleted() ? res.getColor(R.color.colorPrimary) : res.getColor(R.color.fomes_content_card_title_text_color));
        viewHolder.subTitleTextView.setTextColor(betaTest.isCompleted() ? res.getColor(R.color.colorPrimary) : res.getColor(R.color.fomes_content_card_subtitle_text_color));

        // 메인 태그 부분
        if (this.presenter.isActivatedPointSystem()) {
            viewHolder.planTextView.setVisibility(View.GONE);
            viewHolder.minRewardTextView.setVisibility(View.VISIBLE);
            viewHolder.maxRewardTextView.setVisibility(View.VISIBLE);

            try {
                BetaTest.Rewards.RewardItem minRewardItem = betaTest.getRewards().getMinReward();
                viewHolder.minRewardTextView.setText(getRewardText(minRewardItem,
                        res.getString(R.string.betatest_main_tag_min_reward), res.getString(R.string.betatest_main_tag_min_reward_point)));

                BetaTest.Rewards.RewardItem maxRewardItem = betaTest.getRewards().getMaxReward();
                viewHolder.maxRewardTextView.setText(getRewardText(maxRewardItem,
                        res.getString(R.string.betatest_main_tag_max_reward), res.getString(R.string.betatest_main_tag_max_reward_point)));
            } catch (Exception e) {
                viewHolder.minRewardTextView.setVisibility(View.GONE);
                viewHolder.maxRewardTextView.setVisibility(View.GONE);
            }
        } else {
            viewHolder.minRewardTextView.setVisibility(View.GONE);
            viewHolder.maxRewardTextView.setVisibility(View.GONE);
            viewHolder.planTextView.setVisibility(View.VISIBLE);

            @StyleRes int planStyleResId;
            @ColorRes int planNameColorId;
            if (betaTest.isPremiumPlan()) {
                planStyleResId = R.style.BetaTestTheme_Plan_Premium;
                planNameColorId = R.color.fomes_orange;
            } else {
                planStyleResId = R.style.BetaTestTheme_Plan_Lite;
                planNameColorId = R.color.colorPrimary;
            }

            viewHolder.planTextView.setText(betaTest.getPlanStringResId());
            viewHolder.planTextView.setTextColor(res.getColor(planNameColorId));
            viewHolder.planTextView.setBackground(res.getDrawable(R.drawable.item_rect_rounded_corner_background,
                    new ContextThemeWrapper(context, planStyleResId).getTheme()));
        }

        // 참여정보 표시 정책
        int myStatusVisibilty = betaTest.isAttended() ? View.VISIBLE : View.GONE;
        @StyleRes int myStatusStyleResId = betaTest.isCompleted() ? R.style.BetaTestTheme_MyStatus_Completed : R.style.BetaTestTheme_MyStatus;
        @StringRes int myStatusStringId = betaTest.isCompleted() ? R.string.betatest_my_status_completed : R.string.betatest_my_status_attend;
        @ColorRes int myStatusTextColorId = betaTest.isCompleted() ? R.color.colorPrimary : R.color.fomes_white;

        viewHolder.myStatusTextView.setVisibility(myStatusVisibilty);
        viewHolder.myStatusTextView.setText(myStatusStringId);
        viewHolder.myStatusTextView.setTextColor(res.getColor(myStatusTextColorId));
        viewHolder.myStatusTextView.setBackground(res.getDrawable(R.drawable.item_rect_rounded_corner_background,  new ContextThemeWrapper(context, myStatusStyleResId).getTheme()));

        viewHolder.shareButton.setOnClickListener(v -> presenter.shareToKaKao(betaTest));
    }

    private String getRewardText(BetaTest.Rewards.RewardItem rewardItem, String defaultTextFormat, String pointTextFormat) {
        if (FomesConstants.BetaTest.Reward.PAYMENT_TYPE_POINT.equals(rewardItem.getPaymentType())) {
            return String.format(pointTextFormat, rewardItem.getPrice());
        } else {
            return String.format(defaultTextFormat, rewardItem.getPaymentTypeDisplayString());
        }
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subTitleTextView;
        TextView projectStatusTextView;
        TextView minRewardTextView;
        TextView maxRewardTextView;
        TextView planTextView;
        TextView myStatusTextView;
        ImageView overviewImageView;
        TextView reportBugButton;
        TextView shareButton;
        ViewGroup tagViewGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            projectStatusTextView = itemView.findViewById(R.id.betatest_project_status);
            minRewardTextView = itemView.findViewById(R.id.betatest_reward_min);
            maxRewardTextView = itemView.findViewById(R.id.betatest_reward_max);
            planTextView = itemView.findViewById(R.id.betatest_plan);
            myStatusTextView = itemView.findViewById(R.id.betatest_my_status);
            overviewImageView = itemView.findViewById(R.id.betatest_overview_imageview);
            reportBugButton = itemView.findViewById(R.id.betatest_bug_button);
            tagViewGroup = itemView.findViewById(R.id.betatest_tag_layout);
            shareButton = itemView.findViewById(R.id.betatest_share_button);
        }
    }
}
