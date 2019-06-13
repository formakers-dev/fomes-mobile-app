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

        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.titleTextView.setText(item.getTitle());
        viewHolder.subTitleTextView.setText(item.getSubTitle());
        viewHolder.periodTextView.setText(String.format("%s ~ %s", item.getOpenDate(), item.getCloseDate()));

        Glide.with(context).load(item.getOverviewImageUrl())
                .apply(new RequestOptions().override(76, 76)
                .centerCrop()
                .transform(new RoundedCorners(4))
                .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                .into(viewHolder.iconImageView);

        viewHolder.labelImageView.setImageResource(item.isCompleted() ? R.drawable.label_attend : R.drawable.label_absent);

        viewHolder.epilogueButton.setText("자세히 보기");

        BetaTest.AfterService afterService = item.getAfterService();

        if (afterService != null) {
            viewHolder.companySaysTextView.setText(afterService.getCompanySays());
            viewHolder.epilogueButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(afterService.getEpilogue());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            });
            viewHolder.epilogueButton.setEnabled(true);
        } else {
            viewHolder.companySaysTextView.setText(R.string.betatest_company_says_not_collected);
            viewHolder.epilogueButton.setOnClickListener(null);
            viewHolder.epilogueButton.setEnabled(false);
        }
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView companySaysTextView;
        ImageView labelImageView;
        ImageView iconImageView;
        TextView titleTextView;
        TextView subTitleTextView;
        TextView periodTextView;
        Button epilogueButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companySaysTextView = itemView.findViewById(R.id.betatest_finished_company_says);
            labelImageView = itemView.findViewById(R.id.betatest_label);
            iconImageView = itemView.findViewById(R.id.betatest_icon_imageview);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            periodTextView = itemView.findViewById(R.id.betatest_period_textview);
            epilogueButton = itemView.findViewById(R.id.betatest_finished_epilogue_button);
        }
    }
}
