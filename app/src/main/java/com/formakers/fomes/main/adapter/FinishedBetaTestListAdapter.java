package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.ContextThemeWrapper;
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

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_finished_betatest, parent, false));
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

        ViewHolder viewHolder = (ViewHolder) holder;
//        viewHolder.subTitleTextView.setText(item.getSubTitle());

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
            viewHolder.awardTextView.setText(afterService.getAwards());
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

        if (item.isCompleted()) {
            viewHolder.disableTitleLayout.setVisibility(View.GONE);
            viewHolder.companySaysLayout.setBackground(context.getResources().getDrawable(R.drawable.item_rect_rounded_corner_background
                    , new ContextThemeWrapper(context, R.style.BetaTestTheme_FinishedCompanySaysBackground_Attend).getTheme()));
        } else {
            viewHolder.disableTitleLayout.setVisibility(View.VISIBLE);
            viewHolder.companySaysLayout.setBackground(context.getResources().getDrawable(R.drawable.item_rect_rounded_corner_background
                    , new ContextThemeWrapper(context, R.style.BetaTestTheme_FinishedCompanySaysBackground).getTheme()));
        }

        if (item.getAfterService() == null) {
            viewHolder.awardGroup.setVisibility(View.GONE);
            viewHolder.progressLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.awardGroup.setVisibility(View.VISIBLE);
            viewHolder.progressLayout.setVisibility(View.GONE);
        }

        viewHolder.subTitleTextView.setVisibility(View.VISIBLE);
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
        @Deprecated View progressLayout;
        @Deprecated TextView progressTitleTextView;
        @Deprecated TextView progressSubTitleTextView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            targetTextView = itemView.findViewById(R.id.betatest_target);
            testTypeTextView = itemView.findViewById(R.id.betatest_test_type);
            progressLayout = itemView.findViewById(R.id.betatest_finished_my_feedback);
            progressTitleTextView = itemView.findViewById(R.id.betatest_finished_progress_title);
            progressSubTitleTextView = itemView.findViewById(R.id.betatest_finished_progress_subtitle);
        }
    }

    class ViewHolder extends BaseViewHolder {
        TextView companySaysTextView;
        View companySaysLayout;
        ImageView labelImageView;
        ImageView iconImageView;
        TextView subTitleTextView;
        Group awardGroup;
        TextView awardTextView;
        Button epilogueButton;
        View disableTitleLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companySaysTextView = itemView.findViewById(R.id.betatest_finished_company_says);
            companySaysLayout = itemView.findViewById(R.id.betatest_finished_company_says_background);
            labelImageView = itemView.findViewById(R.id.betatest_label);
            iconImageView = itemView.findViewById(R.id.betatest_icon_imageview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            awardGroup = itemView.findViewById(R.id.betatest_award_group);
            awardTextView = itemView.findViewById(R.id.betatest_award_contents);
            epilogueButton = itemView.findViewById(R.id.betatest_finished_epilogue_button);
            disableTitleLayout = itemView.findViewById(R.id.betatest_title_layout_disable_background);
        }
    }
}
