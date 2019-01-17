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
import com.formakers.fomes.common.view.adapter.listener.OnRecyclerItemClickListener;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.main.contract.BetaTestListAdapterContract;

import java.util.ArrayList;
import java.util.Date;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BetaTest item = betaTests.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        Glide.with(context).load(item.getOverviewImageUrl())
                .apply(new RequestOptions().override(76, 76)
                        .centerCrop()
                        .transform(new RoundedCorners(4))
                        .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                .into(viewHolder.overviewImageView);

        viewHolder.titleTextView.setText(item.getTitle());
        viewHolder.subTitleTextView.setText(item.getSubTitle());

        List<String> targetApps = item.getApps();

        if (targetApps == null || targetApps.isEmpty()) {
            viewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), context.getString(R.string.app_name)));
        } else {
            viewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), targetApps.get(0)));
        }

        viewHolder.testTypeTextView.setText(item.getTypeTags().get(0));
        viewHolder.projectStatusTextView.setText(String.format(context.getString(R.string.betatest_project_status_format), (item.getCloseDate().getTime()/1000 - new Date().getTime()/1000) / (24*60*60)));
        viewHolder.requiredTimeTextView.setText(String.format(context.getString(R.string.betatest_required_time_format), 3));
//        sizeTextView.setText(betaTest.getTestSize());

        String reward = item.getReward();
        if (TextUtils.isEmpty(reward)) {
            viewHolder.rewardTextView.setText(R.string.betatest_reward_none);
        } else {
            viewHolder.rewardTextView.setText(reward);
        }

        viewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));

        // Enable 처리
        viewHolder.itemView.setEnabled(!item.isCompleted());
        viewHolder.completedLabelView.setVisibility(item.isCompleted() ? View.VISIBLE : View.GONE);

        viewHolder.disableBackgroundView.setVisibility(!viewHolder.itemView.isEnabled() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.itemClickListener = listener;
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
        ImageView overviewImageView;
        TextView titleTextView;
        TextView subTitleTextView;
        TextView targetTextView;
        TextView testTypeTextView;
        TextView projectStatusTextView;
        TextView requiredTimeTextView;
        TextView sizeTextView;
        TextView rewardTextView;
        View disableBackgroundView;
        View completedLabelView;

        public ViewHolder(View itemView) {
            super(itemView);
            overviewImageView = itemView.findViewById(R.id.betatest_overview_imageview);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            targetTextView = itemView.findViewById(R.id.betatest_target);
            testTypeTextView = itemView.findViewById(R.id.betatest_test_type);
            projectStatusTextView = itemView.findViewById(R.id.betatest_project_status);
            requiredTimeTextView = itemView.findViewById(R.id.betatest_required_time);
            sizeTextView = itemView.findViewById(R.id.betatest_size);
            rewardTextView = itemView.findViewById(R.id.betatest_reward);
            disableBackgroundView = itemView.findViewById(R.id.betatest_disable_background);
            completedLabelView = itemView.findViewById(R.id.betatest_completed_label);
        }
    }
}
