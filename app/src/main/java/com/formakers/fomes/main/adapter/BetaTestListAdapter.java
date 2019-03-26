package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
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

    private static final int GROUP_VIEW_TYPE = 1;
    private static final int ITEM_VIEW_TYPE = 2;

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

        if (viewType == GROUP_VIEW_TYPE) {
            return new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_betatest, parent, false));
        } else {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BetaTest item = betaTests.get(position);

        int viewType = holder.getItemViewType();
        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;

        baseViewHolder.titleTextView.setText(item.getTitle());

        List<String> targetApps = item.getApps();

        if (targetApps == null || targetApps.isEmpty()) {
            baseViewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), context.getString(R.string.app_name)));
        } else {
            baseViewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), targetApps.get(0)));
        }

        baseViewHolder.testTypeTextView.setText(item.getTags().get(0));

        long remainDays = item.getRemainDays();

        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format(context.getString(R.string.betatest_project_status_format), remainDays);
        } else if (remainDays == 0) {
            projectStatus = context.getString(R.string.beta_test_today_close);
        } else {
            projectStatus = context.getString(R.string.common_close);
        }
        baseViewHolder.projectStatusTextView.setText(projectStatus);

        baseViewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));

        // Enable 처리
        baseViewHolder.itemView.setEnabled(item.isOpened() && !item.isCompleted());
        baseViewHolder.disableBackgroundView.setVisibility(!baseViewHolder.itemView.isEnabled() ? View.VISIBLE : View.GONE);

        if (viewType == GROUP_VIEW_TYPE) {
            GroupViewHolder viewHolder = (GroupViewHolder) holder;

            viewHolder.stampImageView.setVisibility(View.VISIBLE);
            viewHolder.stampImageView.setImageResource(item.isCompleted() ? R.drawable.stamp_attend : R.drawable.stamp_absent);
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            Glide.with(context).load(item.getOverviewImageUrl())
                    .apply(new RequestOptions().override(76, 76)
                            .centerCrop()
                            .transform(new RoundedCorners(4))
                            .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                    .into(itemViewHolder.overviewImageView);

            itemViewHolder.subTitleTextView.setText(item.getSubTitle());

            itemViewHolder.requiredTimeTextView.setText(String.format(context.getString(R.string.betatest_required_time_format), item.getRequiredTime(DateUtil.CONVERT_TYPE_MINUTES)));
            itemViewHolder.amountTextView.setText(item.getAmount());

            String reward = item.getReward();
            if (TextUtils.isEmpty(reward)) {
                itemViewHolder.rewardTextView.setText(R.string.betatest_reward_none);
            } else {
                itemViewHolder.rewardTextView.setText(reward);
            }

            itemViewHolder.completedLabelView.setVisibility(item.isCompleted() ? View.VISIBLE : View.GONE);
            itemViewHolder.closedLabelView.setVisibility(!item.isOpened() && !item.isCompleted() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (betaTests.get(position).isGroup()) {
            return GROUP_VIEW_TYPE;
        } else {
            return ITEM_VIEW_TYPE;
        }
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
        TextView targetTextView;
        TextView testTypeTextView;
        TextView projectStatusTextView;
        View disableBackgroundView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            targetTextView = itemView.findViewById(R.id.betatest_target);
            testTypeTextView = itemView.findViewById(R.id.betatest_test_type);
            projectStatusTextView = itemView.findViewById(R.id.betatest_project_status);
            disableBackgroundView = itemView.findViewById(R.id.betatest_disable_background);
        }
    }

    class ItemViewHolder extends BaseViewHolder{
        ImageView overviewImageView;
        TextView subTitleTextView;
        TextView requiredTimeTextView;
        TextView amountTextView;
        TextView rewardTextView;
        View completedLabelView;
        View closedLabelView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            overviewImageView = itemView.findViewById(R.id.betatest_overview_imageview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            requiredTimeTextView = itemView.findViewById(R.id.betatest_required_time);
            amountTextView = itemView.findViewById(R.id.betatest_amount);
            rewardTextView = itemView.findViewById(R.id.betatest_reward);
            completedLabelView = itemView.findViewById(R.id.betatest_completed_label);
            closedLabelView = itemView.findViewById(R.id.betatest_closed_label);
        }
    }

    class GroupViewHolder extends BaseViewHolder {
        ImageView stampImageView;

        public GroupViewHolder(View itemView) {
            super(itemView);
            stampImageView = itemView.findViewById(R.id.betatest_stamp);
        }
    }
}
