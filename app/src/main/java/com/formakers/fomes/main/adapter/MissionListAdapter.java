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
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.BetaTestDetailContract;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.formakers.fomes.common.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_LOCK;
import static com.formakers.fomes.common.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_ITEM;
import static com.formakers.fomes.common.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_REFRESH;

public class MissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MissionListAdapter";

    List<Mission> missionList = new ArrayList<>();
    View.OnClickListener missionItemClickListener;

    BetaTestDetailContract.Presenter presenter;
    BetaTestDetailContract.View view;

    public MissionListAdapter(BetaTestDetailContract.Presenter presenter, BetaTestDetailContract.View view) {
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

    public MissionListAdapter setMissionItemClickListener(View.OnClickListener missionItemClickListener) {
        this.missionItemClickListener = missionItemClickListener;
        return this;
    }

    public MissionListAdapter setMissionList(List<Mission> missionList) {
        this.missionList = missionList;
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
                .apply(new RequestOptions()
                        .placeholder(new ColorDrawable(context.getResources().getColor(R.color.fomes_deep_gray)))
                        .fitCenter())
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

        viewHolder.lockLevelTextView.setText(String.format(context.getString(R.string.betatest_detail_mission_item_lock_level_format), position + 1));
        viewHolder.lockTitleTextView.setText(mission.getItems().get(0).getTitle());

        viewHolder.lockDescriptionTextView.setText(position <= 0 ? "참여하려면 터치해 주세요." : "이전 단계를 완료하시면 열립니다.");

        Log.d(TAG, "mission: " + mission);
        viewHolder.lockView.setVisibility(mission.isLocked() ? View.VISIBLE : View.GONE);

        if (position <= 0 && mission.isLocked()) {
            viewHolder.lockView.setClickable(true);
            viewHolder.lockView.setOnClickListener(v -> {
                for (Mission lockedMission : missionList) {
                    for (Mission.MissionItem missionItem : lockedMission.getItems()) {
                        if ("play".equals(missionItem.getType())
                                || "hidden".equals(missionItem.getType())) {
                            presenter.requestCompleteMissionItem(missionItem.getId());
                        }
                    }
                }

                presenter.sendEventLog(BETA_TEST_DETAIL_TAP_LOCK, mission.getId());
            });
        } else {
            viewHolder.lockView.setClickable(false);
        }
        viewHolder.lockView.setOnTouchListener((v, event) -> {
            if (position <= 0) {
                v.performClick();
            }

            return true;
        });

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
                long playtime = 0L;

                missionItemProgressStatusTextView.setText("참여 가능");
//                viewHolder.missionPlayTimeLayout.setVisibility(View.VISIBLE);
//                viewHolder.missionPlayTimeTextView.setText(DateUtil.convertDurationToString(playtime));
//                viewHolder.missionPlayTimeDescriptionTextView.setText(playtime <= 0L ? R.string.betatest_detail_mission_play_time_desc_ready : R.string.betatest_detail_mission_play_time_desc_playing);
            } else {
//                viewHolder.missionPlayTimeLayout.setVisibility(View.GONE);
                missionItemProgressStatusTextView.setText(missionItem.isCompleted() ? "참여 완료" : "");
                missionItemView.setEnabled(missionItem.isRepeatable() || !missionItem.isCompleted());
            }

            int missionItemOrder = missionItem.getOrder();
            missionItemOrderTextView.setText(String.format("%d단계", missionItem.getOrder()));
            if (missionItemOrder <= 0) {
                missionItemOrderTextView.setVisibility(View.INVISIBLE);
            }
            missionItemOrderTextView.setVisibility(View.GONE);

            missionItemTitleTextView.setText(missionItem.getTitle());

            missionItemView.setOnClickListener(v -> {
                presenter.sendEventLog(BETA_TEST_DETAIL_TAP_MISSION_ITEM, missionItem.getId());
                presenter.processMissionItemAction(missionItem);
            });

            viewHolder.itemViewGroup.addView(missionItemView);
        }

        viewHolder.descriptionLayout.setVisibility((viewHolder.descriptionImageView.getVisibility() == View.VISIBLE
                || viewHolder.missionPlayTimeLayout.getVisibility() == View.VISIBLE) ? View.VISIBLE : View.GONE);

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
                    }, e -> Log.e(TAG, String.valueOf(e)), () -> {
                        presenter.getMissionList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(missionList -> {
                                    setMissionList(missionList);
                                    notifyDataSetChanged();
                                });
                    }));
        });

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
        ViewGroup descriptionLayout;
        ImageView descriptionImageView;
        ViewGroup missionPlayTimeLayout;
        TextView missionPlayTimeTextView;
        TextView missionPlayTimeDescriptionTextView;
        TextView guideTextView;
        View lockView;
        TextView lockLevelTextView;
        TextView lockTitleTextView;
        TextView lockDescriptionTextView;
        ViewGroup itemViewGroup;
        View refreshButton;
        View refreshProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleIconImageView = itemView.findViewById(R.id.mission_title_icon);
            titleTextView = itemView.findViewById(R.id.mission_title);
            descriptionTextView = itemView.findViewById(R.id.mission_description);
            descriptionLayout = itemView.findViewById(R.id.mission_description_layout);
            descriptionImageView = itemView.findViewById(R.id.mission_description_image);
            missionPlayTimeLayout = itemView.findViewById(R.id.mission_play_time_layout);
            missionPlayTimeTextView = itemView.findViewById(R.id.mission_play_time_textview);
            missionPlayTimeDescriptionTextView = itemView.findViewById(R.id.mission_play_time_desc_textview);
            guideTextView = itemView.findViewById(R.id.mission_guide);
            lockView = itemView.findViewById(R.id.betatest_lock_layout);
            lockLevelTextView = itemView.findViewById(R.id.betatest_mission_lock_level_textview);
            lockTitleTextView = itemView.findViewById(R.id.betatest_mission_lock_title_textview);
            lockDescriptionTextView = itemView.findViewById(R.id.betatest_mission_lock_description_textview);
            itemViewGroup = itemView.findViewById(R.id.mission_items_layout);
            refreshButton = itemView.findViewById(R.id.mission_refresh_button);
            refreshProgress = itemView.findViewById(R.id.mission_refresh_progress);
        }
    }
}