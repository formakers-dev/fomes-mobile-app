package com.formakers.fomes.main.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
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
import com.formakers.fomes.common.view.WebViewActivity;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.adapter.MissionListAdapter;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.dagger.BetaTestDetailActivityModule;
import com.formakers.fomes.main.dagger.DaggerBetaTestDetailActivityComponent;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.formakers.fomes.common.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_ENTER;

public class BetaTestDetailActivity extends FomesBaseActivity implements BetaTestDetailContract.View {

    private static final String TAG = "BetaTestDetailActivity";
    private static final int DEFAULT_REWARDS_MINIMUM_DELAY = 7;

    @BindView(R.id.loading) ProgressBar loadingProgressBar;
    @BindView(R.id.betatest_detail_overview_image) ImageView overviewImageView;
    @BindView(R.id.betatest_detail_app_icon) ImageView iconImageView;
    @BindView(R.id.betatest_detail_title) TextView titleTextView;
    @BindView(R.id.betatest_detail_subtitle) TextView subTitleTextView;
    @BindView(R.id.betatest_detail_period) TextView periodTextView;
    @BindView(R.id.betatest_detail_d_day) TextView dDayTextView;
    @BindView(R.id.betatest_detail_description_textview) TextView descriptionTextView;
    @BindView(R.id.betatest_reward_description_textview) TextView rewardDescriptionTextView;
    @BindView(R.id.betatest_reward_items_layout) ViewGroup rewardViewGroup;
    @BindView(R.id.betatest_mission_list) RecyclerView missionRecyclerView;
    @BindView(R.id.betatest_purpose_title_textview) TextView purposeTitleTextView;
    @BindView(R.id.betatest_purpose_description_textview) TextView purposeDescriptionTextView;
    @BindView(R.id.betatest_howto_items_layout) ViewGroup howtoViewGroup;

    @Inject BetaTestDetailContract.Presenter presenter;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

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

        presenter.sendEventLog(BETA_TEST_DETAIL_ENTER, id);
    }


    @Override
    protected void onDestroy() {
        compositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(FomesConstants.BetaTest.EXTRA_ID, getIntent().getStringExtra(FomesConstants.BetaTest.EXTRA_ID));
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
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
                .apply(new RequestOptions().override(120, 120)
                        .centerCrop()
                        .transform(new RoundedCorners(16))
                        .placeholder(new ColorDrawable(getResources().getColor(R.color.fomes_deep_gray))))
                .into(iconImageView);

        titleTextView.setText(betaTest.getTitle());

        String tagsString = betaTest.getTagsString();
        if (!TextUtils.isEmpty(tagsString)) {
            subTitleTextView.setText(tagsString);
        } else {
            subTitleTextView.setVisibility(View.GONE);
        }

        String description = betaTest.getDescription();
        if (!TextUtils.isEmpty(description)) {
            descriptionTextView.setText(betaTest.getDescription());
        } else {
            descriptionTextView.setVisibility(View.GONE);
        }

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

        // 테스트 목적
        if (TextUtils.isEmpty(betaTest.getPurpose())) {
            purposeTitleTextView.setVisibility(View.GONE);
            purposeDescriptionTextView.setVisibility(View.GONE);
        } else {
            purposeTitleTextView.setVisibility(View.VISIBLE);
            purposeDescriptionTextView.setVisibility(View.VISIBLE);
            purposeDescriptionTextView.setText(betaTest.getPurpose());
        }

        // 리워드 목록
        rewardDescriptionTextView.setText(String.format(getString(R.string.betatest_detail_rewards_description), betaTest.getRewards().getMinimumDelay() != null ? betaTest.getRewards().getMinimumDelay() : DEFAULT_REWARDS_MINIMUM_DELAY));

        for (BetaTest.Rewards.RewardItem rewardItem : betaTest.getRewards().getList()) {
            View rewardItemView = getLayoutInflater().inflate(R.layout.item_betatest_reward, null);

            ImageView rewardItemIconImageView = rewardItemView.findViewById(R.id.betatest_reward_icon);
            TextView rewardItemTitleTextView = rewardItemView.findViewById(R.id.betatest_reward_title);
            TextView rewardItemDescriptionTextView = rewardItemView.findViewById(R.id.betatest_reward_description);

            Glide.with(this).load(rewardItem.getIconImageUrl())
                    .apply(new RequestOptions()
                            .fitCenter()
                            .placeholder(new ColorDrawable(getResources().getColor(R.color.fomes_deep_gray))))
                    .into(rewardItemIconImageView);

            rewardItemTitleTextView.setText(rewardItem.getTitle());
            rewardItemDescriptionTextView.setText(rewardItem.getContent());

            rewardViewGroup.addView(rewardItemView);
        }

        // 테스트 방법
        Observable.from(betaTest.getMissions())
                .concatMap(mission -> Observable.from(mission.getItems()))
                .toList()
                .subscribe(displayedMissionItems -> {
                    displayedMissionItems.add(new Mission.MissionItem().setTitle("보상 받기"));

                    for (Mission.MissionItem missionItem : displayedMissionItems) {
                        View missionItemView = getLayoutInflater().inflate(R.layout.item_betatest_mission_item, null);

                        TextView missionItemOrderTextView = missionItemView.findViewById(R.id.mission_item_order);
                        TextView missionItemTitleTextView = missionItemView.findViewById(R.id.mission_item_title);

                        missionItemOrderTextView.setText(String.format("%d단계", displayedMissionItems.indexOf(missionItem) + 1));
                        missionItemTitleTextView.setText(missionItem.getTitle());

                        howtoViewGroup.addView(missionItemView);
                    }
                });


        missionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(this, R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
        missionRecyclerView.addItemDecoration(dividerItemDecoration);

        boolean isLocked = betaTest.getCompletedItemCount() <= 0;
        MissionListAdapter missionListAdapter = new MissionListAdapter(betaTest, presenter, this);
        missionListAdapter.setLocked(isLocked);

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

    @Override
    public View inflate(int layoutResId) {
        return getLayoutInflater().inflate(layoutResId, null);
    }

    @Override
    public void startWebViewActivity(String title, String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_TITLE, title);
        intent.putExtra(WebViewActivity.EXTRA_CONTENTS, url);
        startActivity(intent);
    }

    @Override
    public void startByDeeplink(Uri deeplinkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(deeplinkUri);
        startActivity(intent);
    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }
}
