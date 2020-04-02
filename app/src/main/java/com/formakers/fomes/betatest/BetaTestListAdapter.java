package com.formakers.fomes.betatest;

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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Resources res = context.getResources();
        BetaTest betaTest = betaTests.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.titleTextView.setText(betaTest.getTitle());

        viewHolder.subTitleTextView.setText(betaTest.getDisplayDescription());

        viewHolder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(position));

        // 디데이
        viewHolder.projectStatusTextView.setBackgroundResource(R.drawable.label_betatest_dday);
        long remainDays = betaTest.getRemainDays();

        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format("%d 일\n남음", remainDays + 1);
        } else {
            projectStatus = "오늘\n마감";
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

        viewHolder.completedLabelImageView.setVisibility(betaTest.isAttended() && betaTest.isCompleted() ? View.VISIBLE : View.GONE);

        viewHolder.titleTextView.setTextColor(betaTest.isAttended() || betaTest.isCompleted() ? res.getColor(R.color.colorPrimary) : res.getColor(R.color.fomes_white));
        viewHolder.subTitleTextView.setTextColor(betaTest.isCompleted() ? res.getColor(R.color.colorPrimary) : res.getColor(R.color.fomes_light_gray));

        // NOTE : 프리미엄 뱃지 표시 정책 - standard, simple plan인 경우만 표시
        if ("standard".equals(betaTest.getPlan()) || "simple".equals(betaTest.getPlan())) {
            viewHolder.premiumBadgeImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.premiumBadgeImageView.setVisibility(View.GONE);
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
        ImageView premiumBadgeImageView;
        ImageView overviewImageView;
        ImageView completedLabelImageView;
        TextView reportBugButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.betatest_title_textview);
            subTitleTextView = itemView.findViewById(R.id.betatest_subtitle_textview);
            projectStatusTextView = itemView.findViewById(R.id.betatest_project_status);
            premiumBadgeImageView = itemView.findViewById(R.id.betatest_premium_badge);
            overviewImageView = itemView.findViewById(R.id.betatest_overview_imageview);
            completedLabelImageView = itemView.findViewById(R.id.betatest_label);
            reportBugButton = itemView.findViewById(R.id.betatest_bug_button);
        }
    }
}
