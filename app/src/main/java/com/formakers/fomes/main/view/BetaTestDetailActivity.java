package com.formakers.fomes.main.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.dagger.BetaTestDetailActivityModule;
import com.formakers.fomes.main.dagger.DaggerBetaTestDetailActivityComponent;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;

public class BetaTestDetailActivity extends FomesBaseActivity implements BetaTestDetailContract.View {

    private static final String TAG = "BetaTestDetailActivity";
    private static final int DEFAULT_REWARDS_MINIMUM_DELAY = 7;

    @BindView(R.id.loading) ProgressBar loadingProgressBar;
    @BindView(R.id.betatest_detail_overview_image) ImageView overviewImageView;
    @BindView(R.id.betatest_detail_app_icon) ImageView iconImageView;
    @BindView(R.id.betatest_detail_title) TextView titleTextView;
    @BindView(R.id.betatest_detail_subtitle) TextView descriptionTextView;
    @BindView(R.id.betatest_detail_period) TextView periodTextView;
    @BindView(R.id.betatest_detail_d_day) TextView dDayTextView;
    @BindView(R.id.betatest_reward_description_textview) TextView rewardDescriptionTextView;
    @BindView(R.id.betatest_reward_items_layout) ViewGroup rewardViewGroup;
    @BindView(R.id.betatest_mission_list) RecyclerView missionRecyclerView;

    @Inject BetaTestDetailContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");

        this.setContentView(R.layout.activity_betatest_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        DaggerBetaTestDetailActivityComponent.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .betaTestDetailActivityModule(new BetaTestDetailActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            Toast.makeText(this, "문제가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String id = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);
        this.presenter.load(id);
    }


    @Override
    protected void onDestroy() {
        this.presenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void setPresenter(BetaTestDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void bind(BetaTest betaTest) {
        Glide.with(this).load(betaTest.getOverviewImageUrl())
                .apply(new RequestOptions().centerCrop()
                        .placeholder(new ColorDrawable(getResources().getColor(R.color.fomes_deep_gray))))
                .into(overviewImageView);

        Glide.with(this).load(betaTest.getIconImageUrl())
                .apply(new RequestOptions().override(60, 60)
                        .centerCrop()
                        .transform(new RoundedCorners(8))
                        .placeholder(new ColorDrawable(getResources().getColor(R.color.fomes_deep_gray))))
                .into(iconImageView);

        titleTextView.setText(betaTest.getTitle());
        descriptionTextView.setText(betaTest.getDisplayDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.YY_DOT_MM_DOT_DD, Locale.getDefault());

        periodTextView.setText(String.format("%s ~ %s",
                dateFormat.format(betaTest.getOpenDate()),
                dateFormat.format(betaTest.getCloseDate())));

        // 디데이
        Bundle bundle = getIntent().getExtras();
        long remainDays = bundle.getLong(FomesConstants.BetaTest.EXTRA_REMAIN_DAYS, 0);

        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format("D - %d", remainDays);
        } else if (remainDays == 0) {
            projectStatus = "오늘 마감";
        } else {
            projectStatus = getString(R.string.common_close);
        }

        dDayTextView.setVisibility(View.VISIBLE);
        dDayTextView.setText(projectStatus);

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

        dDayTextView.setVisibility(View.VISIBLE);
        dDayTextView.setBackground(getResources().getDrawable(R.drawable.item_rect_rounded_corner_background,
                new ContextThemeWrapper(this, projectStatusStyleId).getTheme()));
        dDayTextView.setTextColor(getResources().getColor(projectStatusColorId));

        // 리워드 목록
        rewardDescriptionTextView.setText(String.format(getString(R.string.betatest_detail_rewards_description), betaTest.getRewards().getMinimumDelay() != null ? betaTest.getRewards().getMinimumDelay() : DEFAULT_REWARDS_MINIMUM_DELAY));

        for (BetaTest.Rewards.RewardItem rewardItem : betaTest.getRewards().getList()) {
            View rewardItemView = getLayoutInflater().inflate(R.layout.item_betatest_reward, null);

            ImageView rewardItemIconImageView = rewardItemView.findViewById(R.id.betatest_reward_icon);
            TextView rewardItemTitleTextView = rewardItemView.findViewById(R.id.betatest_reward_title);
            TextView rewardItemDescriptionTextView = rewardItemView.findViewById(R.id.betatest_reward_description);

            Glide.with(this).load(rewardItem.getIconImageUrl())
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(new ColorDrawable(getResources().getColor(R.color.fomes_deep_gray))))
                    .into(rewardItemIconImageView);

            rewardItemTitleTextView.setText(rewardItem.getTitle());
            rewardItemDescriptionTextView.setText(rewardItem.getContent());

            rewardViewGroup.addView(rewardItemView);
        }

        missionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(this, R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
        missionRecyclerView.addItemDecoration(dividerItemDecoration);

        boolean isLocked = bundle.getInt(FomesConstants.BetaTest.EXTRA_COMPLETED_ITEM_COUNT, 0) <= 0;
        MissionListAdapter missionListAdapter = new MissionListAdapter(betaTest.getMissions());
        missionListAdapter.setLocked(isLocked);

        if (isLocked) {
            missionListAdapter.setMissionItemClickListener(v -> {
                for (Mission mission : betaTest.getMissions()) {
                    for (Mission.MissionItem missionItem : mission.getItems()) {
                        if ("play".equals(missionItem.getType())) {
                            presenter.requestCompleteMissionItem(missionItem.getId());
                        }
                    }
                }
            });
        }
        missionRecyclerView.setAdapter(missionListAdapter);
    }

    @Override
    public void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void unlockMissions() {
        if (missionRecyclerView.getAdapter() instanceof MissionListAdapter) {
            MissionListAdapter adapter = (MissionListAdapter) missionRecyclerView.getAdapter();
            adapter.setLocked(false);
            adapter.notifyDataSetChanged();
        }
    }

    public class MissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Mission> missionList;
        @Deprecated boolean isLocked = false;
        View.OnClickListener missionItemClickListener;

        public MissionListAdapter(List<Mission> missionList) {
            this.missionList = missionList;
        }

        public boolean isLocked() {
            return isLocked;
        }

        public MissionListAdapter setLocked(boolean locked) {
            isLocked = locked;
            return this;
        }

        public MissionListAdapter setMissionItemClickListener(View.OnClickListener missionItemClickListener) {
            this.missionItemClickListener = missionItemClickListener;
            return this;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest_mission, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Mission mission = missionList.get(position);

            ViewHolder viewHolder = ((ViewHolder) holder);
            Context context = viewHolder.itemView.getContext();

            Glide.with(context).load(mission.getIconImageUrl())
                    .apply(new RequestOptions().placeholder(new ColorDrawable(getResources().getColor(R.color.fomes_deep_gray))))
                    .into(viewHolder.titleIconImageView);

            viewHolder.titleTextView.setText(mission.getTitle());
            viewHolder.descriptionTextView.setText(mission.getDescription());

            if (TextUtils.isEmpty(mission.getDescriptionImageUrl())) {
                viewHolder.descriptionImageView.setVisibility(View.GONE);
            } else {
                viewHolder.descriptionImageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(mission.getDescriptionImageUrl())
                        .apply(new RequestOptions().placeholder(new ColorDrawable(getResources().getColor(R.color.fomes_deep_gray))))
                        .into(viewHolder.descriptionImageView);
            }

            viewHolder.guideTextView.setText(mission.getGuide());

            viewHolder.lockView.setVisibility(isLocked ? View.VISIBLE : View.GONE);

            viewHolder.itemViewGroup.removeAllViews();
            for (Mission.MissionItem missionItem: mission.getItems()) {
                View missionItemView = getLayoutInflater().inflate(R.layout.item_mission_item, null);

                TextView missionItemOrderTextView = missionItemView.findViewById(R.id.mission_item_order);
                TextView missionItemTitleTextView = missionItemView.findViewById(R.id.mission_item_title);
                TextView missionItemProgressStatusTextView = missionItemView.findViewById(R.id.mission_item_progress_status);

                missionItemOrderTextView.setText(String.format("%d)", missionItem.getOrder()));
                missionItemTitleTextView.setText(missionItem.getTitle());

                if ("play".equals(missionItem.getType())) {
                    missionItemProgressStatusTextView.setText("참여 중");
                } else {
                    missionItemProgressStatusTextView.setText(missionItem.isCompleted() ? "참여 완료" : "");
                }

                viewHolder.itemViewGroup.addView(missionItemView);
            }

            viewHolder.itemView.setOnClickListener(missionItemClickListener);
        }

        @Override
        public int getItemCount() {
            return missionList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView titleIconImageView;
            TextView titleTextView;
            TextView descriptionTextView;
            ImageView descriptionImageView;
            TextView guideTextView;
            View lockView;
            ViewGroup itemViewGroup;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                titleIconImageView = itemView.findViewById(R.id.mission_title_icon);
                titleTextView = itemView.findViewById(R.id.mission_title);
                descriptionTextView = itemView.findViewById(R.id.mission_description);
                descriptionImageView = itemView.findViewById(R.id.mission_description_image);
                guideTextView = itemView.findViewById(R.id.mission_guide);
                lockView = itemView.findViewById(R.id.betatest_lock_layout);
                itemViewGroup = itemView.findViewById(R.id.mission_items_layout);
            }
        }
    }
}
