package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.List;

public class BetaTestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BetaTestListAdapterContract.Model, BetaTestListAdapterContract.View {

    private static final String TAG = "BetaTestListAdapter";

    private static final int FINISHED_ITEM_VIEW_TYPE = 1;
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

        if (viewType == FINISHED_ITEM_VIEW_TYPE) {
            return new FinishedItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finished_betatest, parent, false));
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
        baseViewHolder.subTitleTextView.setText(item.getSubTitle());

//        List<String> targetApps = item.getApps();

//        if (targetApps == null || targetApps.isEmpty()) {
//            baseViewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), context.getString(R.string.app_name)));
//        } else {
//            baseViewHolder.targetTextView.setText(String.format(context.getString(R.string.betatest_target_format), targetApps.get(0)));
//        }

//        baseViewHolder.testTypeTextView.setText(item.getTags().get(0));

        long remainDays = item.getRemainDays();

        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format(context.getString(R.string.betatest_project_status_format), remainDays);
        } else if (remainDays == 0) {
            projectStatus = context.getString(R.string.beta_test_today_close);
        } else {
            projectStatus = context.getString(R.string.common_close);
        }

        baseViewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));

        // Enable 처리
        baseViewHolder.itemView.setEnabled(item.isOpened() && !item.isCompleted());

        baseViewHolder.attendLabelImageView.setVisibility(item.isCompleted() || !item.isOpened() ? View.VISIBLE : View.GONE);

        if (viewType == FINISHED_ITEM_VIEW_TYPE) {
            FinishedItemViewHolder viewHolder = (FinishedItemViewHolder) holder;

            viewHolder.attendLabelImageView.setImageResource(item.isCompleted() ? R.drawable.label_attend : R.drawable.label_absent);

            viewHolder.progressPeriodTextView.setText(String.format("테스트 기간 : %s ~ %s", item.getOpenDate(), item.getCloseDate()));

            if (item.isCompleted()) {
                viewHolder.companySaysTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

            if (item.getAfterService() != null) {
                viewHolder.epilogueButton.setText(R.string.betatest_epilogue_opened);

                viewHolder.epilogueButtonTextView.setVisibility(View.GONE);
                viewHolder.epilogueButtonIcon.setVisibility(View.GONE);
                viewHolder.epilogueButton.setEnabled(true);
                viewHolder.companySaysTextView.setText(String.format(context.getString(R.string.betatest_company_says), item.getAfterService().getCompanySays()));

                viewHolder.epilogueButton.setOnClickListener(v -> {
                   Uri uri = Uri.parse(item.getAfterService().getEpilogue());

                   Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                   context.startActivity(intent);
                });
            }
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            Glide.with(context).load(item.getOverviewImageUrl())
                    .apply(new RequestOptions().override(76, 76)
                            .centerCrop()
                            .transform(new RoundedCorners(4))
                            .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                    .into(itemViewHolder.overviewImageView);

            itemViewHolder.projectStatusTextView.setText(projectStatus);
        }
    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (!betaTests.get(position).isOpened()) {
            return FINISHED_ITEM_VIEW_TYPE;
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
        TextView subTitleTextView;
//        TextView targetTextView;
//        TextView testTypeTextView;
        ImageView attendLabelImageView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
//            targetTextView = itemView.findViewById(R.id.betatest_target);
//            testTypeTextView = itemView.findViewById(R.id.betatest_test_type);
            attendLabelImageView = itemView.findViewById(R.id.betatest_label);
        }
    }

    class ItemViewHolder extends BaseViewHolder{
        ImageView overviewImageView;
        TextView progressTextView;
        ProgressBar progressBar;
        TextView projectStatusTextView;
        Button reportBugButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            overviewImageView = itemView.findViewById(R.id.betatest_overview_imageview);
            progressTextView = itemView.findViewById(R.id.betatest_progress_textview);
            progressBar = itemView.findViewById(R.id.betatest_progress_bar);
            projectStatusTextView = itemView.findViewById(R.id.betatest_project_status);
            reportBugButton = itemView.findViewById(R.id.betatest_bug_button);
        }
    }

    class FinishedItemViewHolder extends BaseViewHolder {
        TextView companySaysTextView;
        ImageView iconImageview;
        TextView progressPeriodTextView;
        ImageView epilogueButtonIcon;
        Button epilogueButton;
        TextView epilogueButtonTextView;

        public FinishedItemViewHolder(View itemView) {
            super(itemView);
            iconImageview = itemView.findViewById(R.id.betatest_icon_imageview);
            progressPeriodTextView = itemView.findViewById(R.id.betatest_period_textview);
            epilogueButton = itemView.findViewById(R.id.betatest_finished_epilogue_button);
            epilogueButtonIcon = itemView.findViewById(R.id.betatest_finished_epilogue_button_icon);
            epilogueButtonTextView = itemView.findViewById(R.id.betatest_finished_epilogue_button_text);
            companySaysTextView = itemView.findViewById(R.id.betatest_finished_company_says);
        }
    }
}
