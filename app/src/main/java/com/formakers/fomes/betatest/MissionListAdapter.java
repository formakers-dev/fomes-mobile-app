package com.formakers.fomes.betatest;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.Feature;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_LOCK;
import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_ITEM;
import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_TAP_MISSION_REFRESH;

public class MissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MissionListAdapter";

    private List<Mission> missionList = new ArrayList<>();
    private View.OnClickListener missionItemClickListener;
    private Context context;

    private BetaTestDetailContract.Presenter presenter;
    private BetaTestDetailContract.View view;

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
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_betatest_mission, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mission mission = missionList.get(position);
        Mission.MissionItem missionItem = mission.getItem();

        ViewHolder viewHolder = ((ViewHolder) holder);

        // 락 화면
        viewHolder.lockLevelTextView.setText(String.format(context.getString(R.string.betatest_detail_mission_item_lock_level_format), position + 1));
        viewHolder.lockTitleTextView.setText(mission.getItem().getTitle());

        viewHolder.lockDescriptionTextView.setText(position <= 0 ? "참여하려면 터치해 주세요." : "이전 단계를 완료하시면 열립니다.");

        Log.d(TAG, "mission: " + mission);
        viewHolder.lockView.setVisibility(mission.isLocked() ? View.VISIBLE : View.GONE);

        if (position <= 0 && mission.isLocked()) {
            viewHolder.lockView.setClickable(true);
            viewHolder.lockView.setOnClickListener(v -> {
                presenter.requestToAttendBetaTest();
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

        // 미션 카드 공통 내용
        this.presenter.getImageLoader().loadImage(viewHolder.titleIconImageView, mission.getIconImageUrl(), new RequestOptions().fitCenter());

        viewHolder.titleTextView.setText(mission.getTitle());
        viewHolder.descriptionTextView.setText(mission.getDescription());
        viewHolder.guideTextView.setText(mission.getGuide());
        viewHolder.guideTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        viewHolder.missionTitleTextView.setText(missionItem.getTitle());
        viewHolder.itemButton.setTextColor(context.getResources().getColor(R.color.fomes_white));

        // 디스크립션 레이아웃
        if (TextUtils.isEmpty(mission.getDescriptionImageUrl())) {
            viewHolder.descriptionImageView.setVisibility(View.GONE);
        } else {
            viewHolder.descriptionImageView.setVisibility(View.VISIBLE);
            this.presenter.getImageLoader().loadImage(viewHolder.descriptionImageView, mission.getDescriptionImageUrl());
        }

        // 미션 카드 타입별 분기 로직
        switch (missionItem.getType()) {
            case FomesConstants.BetaTest.Mission.TYPE_PLAY: {
                long playtime = missionItem.getTotalPlayTime();

                viewHolder.itemButton.setText("다운로드 & 플레이");
                if (Feature.CALCULATE_PLAY_TIME) {
                    viewHolder.missionPlayTimeLayout.setVisibility(View.VISIBLE);
                    viewHolder.missionPlayTimeTextView.setText(DateUtil.convertDurationToString(playtime));
                    viewHolder.missionPlayTimeDescriptionTextView.setText(playtime <= 0L ? R.string.betatest_detail_mission_play_time_desc_ready : R.string.betatest_detail_mission_play_time_desc_playing);
                } else {
                    viewHolder.missionPlayTimeLayout.setVisibility(View.GONE);
                }

                // 참여상태
                viewHolder.missionCompletedImageView.setVisibility(View.GONE);
                viewHolder.refreshButton.setVisibility(Feature.CALCULATE_PLAY_TIME ? View.VISIBLE : View.GONE);
                viewHolder.itemButton.setEnabled(true);

                // 플레이 시간 측정
                if (!mission.isLocked()) {
                    updatePlayTime(viewHolder, missionItem);
                }

                break;
            }
            default: {
                viewHolder.missionPlayTimeLayout.setVisibility(View.GONE);

                // 참여상태
                viewHolder.missionCompletedImageView.setVisibility(missionItem.isCompleted() ? View.VISIBLE : View.GONE);
                viewHolder.refreshButton.setVisibility(missionItem.isCompleted() ? View.GONE : View.VISIBLE);
                viewHolder.itemButton.setEnabled(missionItem.isRepeatable() || !missionItem.isCompleted());

                if (!missionItem.isCompleted()) {
                    viewHolder.itemButton.setText("참여하기");
                } else {
                    if (missionItem.isRepeatable()) {
                        viewHolder.itemButton.setText("수정하기");
                    } else {
                        viewHolder.guideTextView.setTextColor(context.getResources().getColor(R.color.fomes_black_alpha_30));
                        viewHolder.itemButton.setText("참여 완료");
                        viewHolder.itemButton.setTextColor(context.getResources().getColor(R.color.fomes_greyish_brown));
                    }
                }
            }
        }

        // 미션 카드 새로고침 버튼
        viewHolder.refreshButton.setOnClickListener(v -> {
            presenter.sendEventLog(BETA_TEST_DETAIL_TAP_MISSION_REFRESH, mission.getItem().getId());

            if (FomesConstants.BetaTest.Mission.TYPE_PLAY.equals(missionItem.getType())) {
                updatePlayTime(viewHolder, missionItem);
            } else {
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
                                .subscribe(newMissionItem -> {
                                    Mission.MissionItem item = mission.getItem();

                                    if (item.getId().equals(newMissionItem.getId())) {
                                        item.setCompleted(newMissionItem.isCompleted());
                                    }
                                }, e -> Log.e(TAG, String.valueOf(e)), () -> {

                                    // TODO : [Adapter MVP] 리팩토링 후 Presenter 로 로직 이동 필요.. 이름은 아마도 refresh? 혹은 reset..?? set..??
                                    presenter.getDisplayedMissionList()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(missionList -> {
                                                setMissionList(missionList);
                                                notifyItemRangeChanged(position, missionList.size() - 1 - position);
                                            });
                                }));
            }
        });

        // 디스크립션 레이아웃 - Visibility 처리 (이미지나 플레이타임이 보여질때만 보여진다)
        viewHolder.descriptionLayout.setVisibility((viewHolder.descriptionImageView.getVisibility() == View.VISIBLE
                || viewHolder.missionPlayTimeLayout.getVisibility() == View.VISIBLE) ? View.VISIBLE : View.GONE);

        // 미션 아이템 버튼
        viewHolder.itemButton.setOnClickListener(v -> {
            presenter.sendEventLog(BETA_TEST_DETAIL_TAP_MISSION_ITEM, missionItem.getId());
            presenter.processMissionItemAction(missionItem);
        });

        // 미션 아이템 클릭 리스너 등록 (보일러 플레이트 코드)
        viewHolder.itemView.setOnClickListener(missionItemClickListener);
    }

    private void updatePlayTime(ViewHolder viewHolder, Mission.MissionItem missionItem) {
        if (Feature.CALCULATE_PLAY_TIME) {
            view.getCompositeSubscription().add(
                    presenter.updatePlayTime(missionItem.getId(), missionItem.getPackageName())
                            .doOnSubscribe(() -> {
                                viewHolder.refreshButton.setVisibility(View.INVISIBLE);
                                viewHolder.refreshProgress.setVisibility(View.VISIBLE);
                            })
                            .doAfterTerminate(() -> {
                                viewHolder.refreshButton.setVisibility(View.VISIBLE);
                                viewHolder.refreshProgress.setVisibility(View.GONE);
                            })
                            .subscribe(playTime -> {
                                Toast.makeText(context, "플레이한 시간이 더해졌다리~~~ : " + playTime, Toast.LENGTH_SHORT).show();
                            }, e -> {
                                if (e instanceof IllegalStateException) {
                                    Toast.makeText(context, "추가 플레이 시간이 없어요~~~~~~ 게임을 플레이하고 새로고침 버튼을 눌러라ㅏ라ㅏ라!!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "새로고침 시 문제가 발생했다멍!🐶\n계속 발생하면 우체통에 문의주라멍!📮", Toast.LENGTH_SHORT).show();
                                }
                                Log.e(TAG, String.valueOf(e));
                            }));
        }
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    public int getPositionByMissionItemId(String missionItemId) {
        for (int position = 0; position < missionList.size(); position++) {
            Mission mission = missionList.get(position);
            if (missionItemId.equals(mission.getItem().getId())) {
                return position;
            }
        }

        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView titleIconImageView;
        TextView titleTextView;
        TextView missionTitleTextView;
        TextView descriptionTextView;
        TextView guideTextView;
        Button itemButton;
        View refreshButton;
        View refreshProgress;
        View missionCompletedImageView;

        // Description Layout
        ViewGroup descriptionLayout;
        ImageView descriptionImageView;
        ViewGroup missionPlayTimeLayout;
        TextView missionPlayTimeTextView;
        TextView missionPlayTimeDescriptionTextView;

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
            missionPlayTimeLayout = itemView.findViewById(R.id.mission_play_time_layout);
            missionPlayTimeTextView = itemView.findViewById(R.id.mission_play_time_textview);
            missionPlayTimeDescriptionTextView = itemView.findViewById(R.id.mission_play_time_desc_textview);
            missionCompletedImageView = itemView.findViewById(R.id.mission_completed_imageview);
            guideTextView = itemView.findViewById(R.id.mission_guide);
            lockView = itemView.findViewById(R.id.betatest_lock_layout);
            lockLevelTextView = itemView.findViewById(R.id.betatest_mission_lock_level_textview);
            lockTitleTextView = itemView.findViewById(R.id.betatest_mission_lock_title_textview);
            lockDescriptionTextView = itemView.findViewById(R.id.betatest_mission_lock_description_textview);
            itemButton = itemView.findViewById(R.id.mission_item_button);
            refreshButton = itemView.findViewById(R.id.mission_refresh_button);
            refreshProgress = itemView.findViewById(R.id.mission_refresh_progress);
        }
    }
}