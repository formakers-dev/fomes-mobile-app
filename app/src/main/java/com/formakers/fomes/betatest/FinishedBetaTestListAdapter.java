package com.formakers.fomes.betatest;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class FinishedBetaTestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements FinishedBetaTestListAdapterContract.Model, FinishedBetaTestListAdapterContract.View {

    Context context;

    List<BetaTest> displayedList = new ArrayList<>();

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
        BetaTest item = displayedList.get(position);
        Resources res = context.getResources();

        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.titleTextView.setText(item.getTitle());

        this.presenter.getImageLoader().loadImage(
                viewHolder.iconImageView,
                item.getIconImageUrl(),
                new RequestOptions().override(120, 120)
                        .centerCrop()
                        .transform(new RoundedCorners(16))
        );

        viewHolder.subTitleTextView.setText(item.getDisplayDescription());


        int completedTextColor = res.getColor(R.color.colorPrimary);
        int normalTextColor = res.getColor(R.color.fomes_white_alpha_60);

        if (item.isRegisteredEpilogue()) {
//            viewHolder.itemView.setEnabled(true);
//            viewHolder.itemView.setOnClickListener(v -> {
//                presenter.sendEventLog(FINISHED_BETA_TEST_TAP_EPILOGUE, item.getId());
//
//                Uri uri = Uri.parse(item.getEpilogue().getDeeplink());
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                context.startActivity(intent);
//            });

            viewHolder.progressDivider.setBackgroundColor(completedTextColor);
            viewHolder.progressEndImageView.setImageResource(R.drawable.round_check);
            viewHolder.progressEndTextView.setTextColor(completedTextColor);
        } else {
//            viewHolder.itemView.setEnabled(false);
//            viewHolder.itemView.setOnClickListener(null);

            viewHolder.progressDivider.setBackgroundColor(normalTextColor);
            viewHolder.progressEndImageView.setImageResource(R.drawable.round_uncheck);
            viewHolder.progressEndTextView.setTextColor(normalTextColor);
        }

        if (item.isCompleted()) {
            viewHolder.myStatusTextView.setVisibility(View.VISIBLE);
            viewHolder.titleTextView.setTextColor(completedTextColor);
            viewHolder.subTitleTextView.setTextColor(completedTextColor);
        } else {
            viewHolder.myStatusTextView.setVisibility(View.GONE);
            viewHolder.titleTextView.setTextColor(res.getColor(R.color.fomes_white));
            viewHolder.subTitleTextView.setTextColor(normalTextColor);
        }

        @StyleRes int planStyleResId;
        @ColorRes int planNameColorId;
        if (item.isPremiumPlan()) {
            planStyleResId = R.style.BetaTestTheme_Plan_Premium;
            planNameColorId = R.color.fomes_orange;
        } else {
            planStyleResId = R.style.BetaTestTheme_Plan_Lite;
            planNameColorId = R.color.colorPrimary;
        }

        viewHolder.planTextView.setText(item.getPlanStringResId());
        viewHolder.planTextView.setTextColor(res.getColor(planNameColorId));
        viewHolder.planTextView.setBackground(res.getDrawable(R.drawable.item_rect_rounded_corner_background,
                new ContextThemeWrapper(context, planStyleResId).getTheme()));

        viewHolder.itemView.setOnClickListener(v -> {
            itemClickListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return displayedList.size();
    }

    @Override
    public Object getItem(int position) {
        return displayedList.get(position);
    }

    @Override
    public List<BetaTest> getAllItems() {
        return displayedList;
    }

    @Override
    public void add(BetaTest item) {
        displayedList.add(item);
    }

    @Override
    public void addAll(List<BetaTest> items) {
        displayedList.addAll(items);
    }

    @Override
    public void clear() {
        displayedList.clear();
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
        ImageView iconImageView;
        TextView titleTextView;
        TextView subTitleTextView;
        TextView planTextView;
        TextView myStatusTextView;
        View progressDivider;
        ImageView progressEndImageView;
        TextView progressEndTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            iconImageView = itemView.findViewById(R.id.betatest_icon_imageview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            planTextView = itemView.findViewById(R.id.betatest_plan);
            myStatusTextView = itemView.findViewById(R.id.betatest_my_status);
            progressDivider = itemView.findViewById(R.id.betatest_finished_progress_divider);
            progressEndImageView = itemView.findViewById(R.id.betatest_finished_progress_end_check_image);
            progressEndTextView = itemView.findViewById(R.id.betatest_finished_progress_end);
        }
    }
}
