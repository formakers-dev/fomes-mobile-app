package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestDetailContract;

import rx.android.schedulers.AndroidSchedulers;

import static com.formakers.fomes.common.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_LOCK;
import static com.formakers.fomes.common.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_ITEM;
import static com.formakers.fomes.common.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_REFRESH;

public class MissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MissionListAdapter";

    BetaTest betaTest;
    @Deprecated boolean isLocked = false;
    View.OnClickListener missionItemClickListener;

    BetaTestDetailContract.Presenter presenter;
    BetaTestDetailContract.View view;

    public MissionListAdapter(BetaTest betaTest, BetaTestDetailContract.Presenter presenter, BetaTestDetailContract.View view) {
        this.betaTest = betaTest;
        this.presenter = presenter;
        this.view = view;
    }

    public MissionListAdapter setPresenter(BetaTestDetailContract.Presenter presenter) {
        this.presenter = presenter;
        return this;
    }

    public MissionListAdapter setView(BetaTestDetailContract.View view) {
        this.view = view;
        return this;
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
        Mission mission = betaTest.getMissions().get(position);

        ViewHolder viewHolder = ((ViewHolder) holder);
        Context context = viewHolder.itemView.getContext();

        Glide.with(context).load(mission.getIconImageUrl())
                .apply(new RequestOptions()
                        .fitCenter()
                        .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                .into(viewHolder.titleIconImageView);

        viewHolder.titleTextView.setText(mission.getTitle());
        viewHolder.descriptionTextView.setText(mission.getDescription());

        if (TextUtils.isEmpty(mission.getDescriptionImageUrl())) {
            viewHolder.descriptionImageView.setVisibility(View.GONE);
        } else {
            viewHolder.descriptionImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(mission.getDescriptionImageUrl())
                    .apply(new RequestOptions().placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray))))
                    .into(viewHolder.descriptionImageView);
        }

        viewHolder.guideTextView.setText(mission.getGuide());

        viewHolder.lockView.setVisibility(isLocked ? View.VISIBLE : View.GONE);
        if (isLocked) {
            viewHolder.lockView.setOnClickListener(v -> {
                for (Mission lockedMission : betaTest.getMissions()) {
                    for (Mission.MissionItem missionItem : lockedMission.getItems()) {
                        if ("play".equals(missionItem.getType())
                                || "hidden".equals(missionItem.getType())) {
                            presenter.requestCompleteMissionItem(missionItem.getId());
                        }
                    }
                }

                presenter.sendEventLog(BETA_TEST_DETAIL_TAP_LOCK, betaTest.getId());
            });
        }

        viewHolder.itemViewGroup.removeAllViews();
        for (Mission.MissionItem missionItem: mission.getItems()) {

            if ("hidden".equals(missionItem.getType())) {
                continue;
            }

            View missionItemView = view.inflate(R.layout.item_betatest_mission_item);

            TextView missionItemOrderTextView = missionItemView.findViewById(R.id.mission_item_order);
            TextView missionItemTitleTextView = missionItemView.findViewById(R.id.mission_item_title);
            TextView missionItemProgressStatusTextView = missionItemView.findViewById(R.id.mission_item_progress_status);

            if ("play".equals(missionItem.getType())) {
                missionItemProgressStatusTextView.setText("참여 중");
            } else {
                missionItemProgressStatusTextView.setText(missionItem.isCompleted() ? "참여 완료" : "");
                missionItemView.setEnabled(!missionItem.isCompleted());
            }

            int missionItemOrder = missionItem.getOrder();
            missionItemOrderTextView.setText(String.format("%d)", missionItem.getOrder()));
            if (missionItemOrder <= 0) {
                missionItemOrderTextView.setVisibility(View.INVISIBLE);
            }
            missionItemTitleTextView.setText(missionItem.getTitle());

            missionItemView.setOnClickListener(v -> {
                presenter.sendEventLog(BETA_TEST_DETAIL_TAP_MISSION_ITEM, missionItem.getId());
                presenter.processMissionItemAction(missionItem);
            });

            viewHolder.itemViewGroup.addView(missionItemView);
        }

        viewHolder.refreshButton.setOnClickListener(v -> {
            presenter.sendEventLog(BETA_TEST_DETAIL_TAP_MISSION_REFRESH, mission.getId());

            view.getCompositeSubscription().add(
                    presenter.refreshMissionProgress(mission.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(() -> {
                        viewHolder.refreshButton.setVisibility(View.INVISIBLE);
                        viewHolder.refreshProgress.setVisibility(View.VISIBLE);
                    })
                    .doAfterTerminate(() -> {
                        viewHolder.refreshButton.setVisibility(View.VISIBLE);
                        viewHolder.refreshProgress.setVisibility(View.GONE);
                    })
                    .subscribe(missionItem -> {
                        for (Mission.MissionItem item : mission.getItems()) {
                            if (item.getId().equals(missionItem.getId())) {
                                item.setCompleted(missionItem.isCompleted());
                            }
                        }
                    }, e -> Log.e(TAG, String.valueOf(e)), () -> notifyItemChanged(position)));
        });

        viewHolder.itemView.setOnClickListener(missionItemClickListener);
    }

    @Override
    public int getItemCount() {
        return betaTest.getMissions().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView titleIconImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView descriptionImageView;
        TextView guideTextView;
        View lockView;
        ViewGroup itemViewGroup;
        View refreshButton;
        View refreshProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleIconImageView = itemView.findViewById(R.id.mission_title_icon);
            titleTextView = itemView.findViewById(R.id.mission_title);
            descriptionTextView = itemView.findViewById(R.id.mission_description);
            descriptionImageView = itemView.findViewById(R.id.mission_description_image);
            guideTextView = itemView.findViewById(R.id.mission_guide);
            lockView = itemView.findViewById(R.id.betatest_lock_layout);
            itemViewGroup = itemView.findViewById(R.id.mission_items_layout);
            refreshButton = itemView.findViewById(R.id.mission_refresh_button);
            refreshProgress = itemView.findViewById(R.id.mission_refresh_progress);
        }
    }
}