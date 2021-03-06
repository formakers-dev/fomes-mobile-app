package com.formakers.fomes.betatest;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.custom.adapter.listener.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_LOCK;
import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_ITEM;
import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_REFRESH;

public class MissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements MissionListAdapterContract.View, MissionListAdapterContract.Model {

    private static final String TAG = "MissionListAdapter";

    private List<Mission> missionList = new ArrayList<>();
    private OnRecyclerItemClickListener missionItemClickListener;
    private Context context;

    private BetaTestDetailContract.Presenter presenter;

    public MissionListAdapter(BetaTestDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest_mission, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Resources res = context.getResources();

        Mission mission = missionList.get(position);

        ViewHolder viewHolder = ((ViewHolder) holder);

        if (position <= 0 && mission.isLocked()) {
            viewHolder.lockView.setOnClickListener(v -> {
                viewHolder.lockView.setEnabled(false);
                presenter.requestToAttendBetaTest();
                presenter.sendEventLog(BETA_TEST_DETAIL_TAP_LOCK, mission.getId());
            });
        } else {
            viewHolder.lockView.setOnClickListener(null);
        }

        // ??? ??????
        viewHolder.lockLevelTextView.setText(String.format(context.getString(R.string.betatest_detail_mission_item_lock_level_format), position + 1));
        viewHolder.lockTitleTextView.setText(mission.getTitle());

        viewHolder.lockDescriptionTextView.setText(position <= 0 ? "??????????????? ????????? ?????????." : "?????? ????????? ??????????????? ????????????.");

        Log.d(TAG, "mission: " + mission);
        viewHolder.lockView.setVisibility(mission.isLocked() ? View.VISIBLE : View.GONE);

        viewHolder.lockView.setOnTouchListener((v, event) -> {
            if (position <= 0) {
                v.performClick();
            }

            return true;
        });

        if (FomesConstants.BetaTest.Mission.TYPE_INSTALL.equals(mission.getType())) {
            viewHolder.titleIconImageView.setImageResource(R.drawable.icon_mission_type_play);
        } else {
            viewHolder.titleIconImageView.setImageResource(R.drawable.icon_mission_type_survey);
        }

        viewHolder.titleTextView.setText(String.format(Locale.getDefault(), context.getString(R.string.betatest_detail_mission_item_lock_level_format), mission.getOrder()));
        viewHolder.descriptionTextView.setText(mission.getDescription());
        viewHolder.guideTextView.setText(mission.getGuide());
        viewHolder.guideTextView.setTextColor(res.getColor(R.color.colorPrimary));
        viewHolder.guideTextView.setVisibility(TextUtils.isEmpty(mission.getGuide()) ? View.GONE : View.VISIBLE);
        viewHolder.missionTitleTextView.setText(mission.getTitle());
        viewHolder.itemButton.setTextColor(res.getColor(R.color.fomes_white));

        // ??????????????? ????????????
        if (FomesConstants.BetaTest.Mission.TYPE_INSTALL.equals(mission.getType()) && mission.isCompleted()) {
            viewHolder.descriptionImageView.setVisibility(View.VISIBLE);
            viewHolder.descriptionImageView.setImageDrawable(res.getDrawable(R.drawable.mission_after_install, null));
        } else {
            if (TextUtils.isEmpty(mission.getDescriptionImageUrl())) {
                viewHolder.descriptionImageView.setVisibility(View.GONE);
            } else {
                viewHolder.descriptionImageView.setVisibility(View.VISIBLE);
                this.presenter.getImageLoader().loadImage(
                        viewHolder.descriptionImageView,
                        mission.getDescriptionImageUrl(), null, false);
            }
        }

        // ????????????
        viewHolder.missionCompletedImageView.setVisibility(mission.isCompleted() ? View.VISIBLE : View.GONE);
        viewHolder.itemButton.setEnabled(mission.isEnabled());
        viewHolder.guideTextView.setTextColor(viewHolder.itemButton.isEnabled() ?
                res.getColor(R.color.colorPrimary) : res.getColor(R.color.fomes_black_alpha_30));
        viewHolder.itemButton.setTextColor(viewHolder.itemButton.isEnabled() ?
                res.getColor(R.color.fomes_white) : res.getColor(R.color.fomes_greyish_brown));

        // ?????? ?????? ????????? ?????? ??????
        switch (mission.getType()) {
            case FomesConstants.BetaTest.Mission.TYPE_INSTALL: {
                viewHolder.itemButton.setText(mission.isCompleted() ? "?????????????????????" : "???????????? ??????");
                break;
            }
            case FomesConstants.BetaTest.Mission.TYPE_PLAY: {
                viewHolder.itemButton.setText(mission.isCompleted() ? "????????? ?????? ??????" : "????????? ????????????");
//                Long playtime = mission.getTotalPlayTime();

//                if (Feature.CALCULATE_PLAY_TIME_VIEW) {
//                    if (playtime != null) {
//                        viewHolder.missionPlayTimeLayout.setVisibility(View.VISIBLE);
//                        viewHolder.missionPlayTimeTextView.setText(DateUtil.convertDurationToString(playtime));
//                        viewHolder.missionPlayTimeDescriptionTextView.setText(playtime <= 0L ? R.string.betatest_detail_mission_play_time_desc_ready : R.string.betatest_detail_mission_play_time_desc_playing);
//                    }
//
//                    // ????????? ?????? ??????
//                    if (!mission.isLocked()) {
//                        refreshMissionProgress(mission);
//                    }
//                } else {
//                  viewHolder.missionPlayTimeLayout.setVisibility(View.GONE);
//                }

                break;
            }
            default: {
//                viewHolder.missionPlayTimeLayout.setVisibility(View.GONE);

                if (!mission.isCompleted()) {
                    viewHolder.itemButton.setText("????????????");
                } else {
                    if (mission.isRepeatable()) {
                        viewHolder.itemButton.setText("????????????");
                    } else {
                        viewHolder.itemButton.setText("?????? ??????");
                    }
                }
            }
        }

        // ?????? ?????? ???????????? ??????
        viewHolder.refreshButton.setOnClickListener(v -> {
            presenter.sendEventLog(BETA_TEST_DETAIL_TAP_MISSION_REFRESH, mission.getId());

            this.clickRefreshButton(mission.getId());
        });

        // ??????????????? ???????????? - Visibility ?????? (???????????? ?????????????????? ??????????????? ????????????)
        viewHolder.descriptionLayout.setVisibility((
                viewHolder.descriptionImageView.getVisibility() == View.VISIBLE
//                || viewHolder.missionPlayTimeLayout.getVisibility() == View.VISIBLE
        ) ? View.VISIBLE : View.GONE);

        // ?????? ????????? ??????
        viewHolder.itemButton.setOnClickListener(v -> {
            presenter.sendEventLog(BETA_TEST_DETAIL_TAP_MISSION_ITEM, mission.getId());
            presenter.processMissionItemAction(mission);
        });

        // ?????? ????????? ?????? ????????? ?????? (????????? ???????????? ??????)
//        viewHolder.itemView.setOnClickListener(v -> missionItemClickListener.onItemClick(position));

        if (mission.isLoading()) {
            viewHolder.itemButton.setEnabled(false);
            viewHolder.loadingShimmer.setVisibility(View.VISIBLE);
            viewHolder.loadingShimmer.startShimmer();
        } else {
            viewHolder.loadingShimmer.stopShimmer();
            viewHolder.loadingShimmer.setVisibility(View.GONE);
            viewHolder.itemButton.setEnabled(mission.isEnabled());
        }

        viewHolder.refreshButton.setVisibility(isDisableRefreshButton(mission) ? View.GONE : View.VISIBLE);
    }

    boolean isDisableRefreshButton(Mission mission) {
        return mission.isCompleted()
                || mission.isLoading();
    }

    private void refreshMission(Mission mission) {
        this.refreshMissionProgress(mission)
                .delay(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscription -> this.setLoading(mission.getId(), true))
                .doAfterTerminate(() -> this.setLoading(mission.getId(), false))
                .subscribe(() -> this.presenter.displayMission(mission.getId()),
                        e -> Log.e(TAG, String.valueOf(e)));
    }

    // TODO : Adapter Presenter ????????? ??????
    private Completable refreshMissionProgress(Mission mission) {
//        if (Feature.CALCULATE_PLAY_TIME_VIEW) {
//            if (FomesConstants.BetaTest.Mission.TYPE_PLAY.equals(mission.getType())) {
//                return presenter.updatePlayTime(mission.getId(), mission.getPackageName())
//                        .doOnSuccess(playTime -> Toast.makeText(context, "????????? ????????? ????????????! : " + playTime, Toast.LENGTH_SHORT).show())
//                        .doOnError(e -> {
//                            if (e instanceof IllegalStateException) {
//                                Toast.makeText(context, "?????? ????????? ????????? ?????????!????\n????????? ??????????????? ???????????? ????????? ????????????!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(context, "???????????? ??? ????????? ???????????????!????\n?????? ???????????? ???????????? ???????????????!????", Toast.LENGTH_SHORT).show();
//                            }
//                        }).toCompletable();
//            }
//        }

        Single<Mission> getMissionProgressSingle = presenter.getMissionProgress(mission.getId());

        if (FomesConstants.BetaTest.Mission.TYPE_INSTALL.equals(mission.getType())) {
            return getMissionProgressSingle.flatMapCompletable(newMission -> {
                if (!newMission.isCompleted()) {
                    if (this.presenter.getIntentIfAppIsInstalled(mission.getPackageName()) != null) {
                        return this.presenter.requestToCompleteMission(mission);
                    }
                }
                return Completable.complete();
            });
        }

        return getMissionProgressSingle
                .observeOn(Schedulers.io())
                .doOnSuccess(newMission -> {
                    if (mission.getId().equals(newMission.getId())) {
                        mission.setCompleted(newMission.isCompleted());
                    }
                }).toCompletable();
    }

    // ?????? ????????? ????????? ?????? ???????????? ????????????
    public void notifyItemBelowAllChanged(int position) {
        notifyItemRangeChanged(position, missionList.size() - position);
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    @Override
    public Mission getItem(int position) {
        return missionList.get(position);
    }

    @Override
    public List<Mission> getAllItems() {
        return this.missionList;
    }

    @Override
    public void add(Mission item) {
        this.missionList.add(item);
    }

    @Override
    public void addAll(List<Mission> items) {
        this.missionList.addAll(items);
    }

    @Override
    public void clear() {
        this.missionList.clear();
    }

    @Override
    public Mission getItemById(String missionId) {
        for (int position = 0; position < missionList.size(); position++) {
            Mission mission = missionList.get(position);
            if (missionId.equals(mission.getId())) {
                return mission;
            }
        }

        return null;
    }

    @Override
    public void setLoading(String missionId, boolean isLoading) {
        getItemById(missionId).setLoading(isLoading);

        // ?????? ?????? ?????? ????????? ???????????????....
        notifyItemChanged(getPositionById(missionId));
    }

    @Override
    public int getPositionById(String missionId) {
        for (int position = 0; position < missionList.size(); position++) {
            Mission mission = missionList.get(position);
            if (missionId.equals(mission.getId())) {
                return position;
            }
        }

        return -1;
    }

    @Override
    public void setPresenter(BetaTestDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.missionItemClickListener = listener;
    }

    @Override
    public void clickRefreshButton(String missionId) {
        this.refreshMission(this.getItemById(missionId));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView titleIconImageView;
        TextView titleTextView;
        TextView missionTitleTextView;
        TextView descriptionTextView;
        TextView guideTextView;
        Button itemButton;
        View refreshButton;
        View missionCompletedImageView;
        ShimmerFrameLayout loadingShimmer;

        // Description Layout
        ViewGroup descriptionLayout;
        ImageView descriptionImageView;
//        ViewGroup missionPlayTimeLayout;
//        TextView missionPlayTimeTextView;
//        TextView missionPlayTimeDescriptionTextView;

        // Lock Layout
        View lockView;
        TextView lockLevelTextView;
        TextView lockTitleTextView;
        TextView lockDescriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleIconImageView = itemView.findViewById(R.id.mission_title_icon);
            titleTextView = itemView.findViewById(R.id.mission_title);
            missionTitleTextView = itemView.findViewById(R.id.mission_item_title);
            descriptionTextView = itemView.findViewById(R.id.mission_description);
            descriptionLayout = itemView.findViewById(R.id.mission_description_layout);
            descriptionImageView = itemView.findViewById(R.id.mission_description_image);
//            missionPlayTimeLayout = itemView.findViewById(R.id.mission_play_time_layout);
//            missionPlayTimeTextView = itemView.findViewById(R.id.mission_play_time_textview);
//            missionPlayTimeDescriptionTextView = itemView.findViewById(R.id.mission_play_time_desc_textview);
            missionCompletedImageView = itemView.findViewById(R.id.mission_completed_imageview);
            guideTextView = itemView.findViewById(R.id.mission_guide);
            lockView = itemView.findViewById(R.id.betatest_lock_layout);
            lockLevelTextView = itemView.findViewById(R.id.betatest_mission_lock_level_textview);
            lockTitleTextView = itemView.findViewById(R.id.betatest_mission_lock_title_textview);
            lockDescriptionTextView = itemView.findViewById(R.id.betatest_mission_lock_description_textview);
            itemButton = itemView.findViewById(R.id.mission_item_button);
            refreshButton = itemView.findViewById(R.id.mission_refresh_button);
            loadingShimmer = itemView.findViewById(R.id.shimmer);
        }
    }
}