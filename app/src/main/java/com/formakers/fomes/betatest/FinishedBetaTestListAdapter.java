package com.formakers.fomes.betatest;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.FINISHED_BETA_TEST_TAP_EPILOGUE;

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

        ViewHolder viewHolder = (ViewHolder) holder;
//        viewHolder.subTitleTextView.setText(item.getSubTitle());

        this.presenter.getImageLoader().loadImage(
                viewHolder.iconImageView,
                item.getIconImageUrl(),
                new RequestOptions().override(120, 120)
                        .centerCrop()
                        .transform(new RoundedCorners(16))
        );

        viewHolder.labelImageView.setImageResource(item.isCompleted() ? R.drawable.label_attend : R.drawable.label_absent);

        viewHolder.subTitleTextView.setText(item.getDisplayDescription());


        BetaTest.AfterService afterService = item.getAfterService();

        if (afterService != null) {
            viewHolder.awardTextView.setText(afterService.getAwards());
            viewHolder.companySaysTextView.setText(afterService.getCompanySays());
            viewHolder.itemView.setEnabled(true);
            viewHolder.itemView.setOnClickListener(v -> {
                presenter.sendEventLog(FINISHED_BETA_TEST_TAP_EPILOGUE, item.getId());

                Uri uri = Uri.parse(afterService.getEpilogue());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            });
        } else {
            viewHolder.awardGroup.setVisibility(View.GONE);
            viewHolder.companySaysTextView.setText(R.string.betatest_company_says_not_collected);
            viewHolder.itemView.setEnabled(false);
            viewHolder.itemView.setOnClickListener(null);
        }

        if (item.isCompleted()) {
            viewHolder.disableTitleLayout.setVisibility(View.GONE);
        } else {
            viewHolder.disableTitleLayout.setVisibility(View.VISIBLE);
        }

        // 레거시 코드 - UX 업데이트 후 사라질 로직들
        if (item.getAfterService() == null) {
            viewHolder.awardGroup.setVisibility(View.GONE);
            viewHolder.progressLayout.setVisibility(View.VISIBLE);

            if (item.isCompleted()) {
                // 제출 완료
                viewHolder.progressTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_white));
                viewHolder.progressSubTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_warm_gray_2));
                viewHolder.progressTitleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_racing_flag, 0, R.drawable.icon_racing_flag_reverse, 0);
                viewHolder.progressTitleTextView.setText(R.string.betatest_submitted_my_feedback);
            } else {
                // 미제출
                viewHolder.progressTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_warm_gray));
                viewHolder.progressSubTitleTextView.setTextColor(context.getResources().getColor(R.color.fomes_warm_gray));

                viewHolder.progressTitleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_grey_flag, 0, R.drawable.icon_grey_flag_reverse, 0);
                viewHolder.progressTitleTextView.setText(R.string.betatest_not_submitted_my_feedback);
            }

            viewHolder.progressSubTitleTextView.setText(R.string.betatest_collecting_results);
        } else {
            viewHolder.awardGroup.setVisibility(View.VISIBLE);
            viewHolder.progressLayout.setVisibility(View.GONE);
        }

        viewHolder.companySaysLayout.setEnabled(item.getAfterService() != null && item.isCompleted());

        viewHolder.subTitleTextView.setVisibility(View.VISIBLE);

        viewHolder.RecheckMyAnswerLayout.removeAllViews();
        for (Mission mission : item.getMissions()) {
            if (mission.getItem().isCompleted() && mission.getItem().isRecheckable()) {
                Button recheckableButton = (Button) LayoutInflater.from(context).inflate(R.layout.item_button, null);
                recheckableButton.setText(String.format(context.getString(R.string.finished_betatest_recheck_my_answer_button_text_format), mission.getItem().getTitle()));
                recheckableButton.setOnClickListener(v -> {
                    this.presenter.emitRecheckMyAnswer(mission.getItem());
                });
                viewHolder.RecheckMyAnswerLayout.addView(recheckableButton);
            }
        }

        viewHolder.RecheckMyAnswerLayout.setVisibility(viewHolder.RecheckMyAnswerLayout.getChildCount() > 0 ? View.VISIBLE : View.GONE);
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
        @Deprecated
        View progressLayout;
        @Deprecated
        TextView progressTitleTextView;
        @Deprecated
        TextView progressSubTitleTextView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
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
        View disableTitleLayout;
        ViewGroup RecheckMyAnswerLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companySaysTextView = itemView.findViewById(R.id.betatest_finished_company_says);
            companySaysLayout = itemView.findViewById(R.id.betatest_finished_company_says_background);
            labelImageView = itemView.findViewById(R.id.betatest_label);
            iconImageView = itemView.findViewById(R.id.betatest_icon_imageview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            awardGroup = itemView.findViewById(R.id.betatest_award_group);
            awardTextView = itemView.findViewById(R.id.betatest_award_contents);
            disableTitleLayout = itemView.findViewById(R.id.betatest_title_layout_disable_background);
            RecheckMyAnswerLayout = itemView.findViewById(R.id.layout_recheck_my_answer);
        }
    }
}
