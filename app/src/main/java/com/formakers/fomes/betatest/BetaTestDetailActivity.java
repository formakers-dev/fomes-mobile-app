package com.formakers.fomes.betatest;

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
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.common.view.webview.WebViewActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_ENTER;

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
    @BindView(R.id.betatest_contents_layout) ViewGroup contentsLayout;
    @BindView(R.id.betatest_game_description_group) Group gameDescriptionGroup;
    @BindView(R.id.betatest_detail_game_description_textview) TextView descriptionTextView;
    @BindView(R.id.betatest_reward_items_layout) ViewGroup rewardViewGroup;
    @BindView(R.id.betatest_mission_list) RecyclerView missionRecyclerView;
    @BindView(R.id.betatest_purpose_group) Group purposeGroup;
    @BindView(R.id.betatest_purpose_title_textview) TextView purposeTitleTextView;
    @BindView(R.id.betatest_purpose_description_textview) TextView purposeDescriptionTextView;
    @BindView(R.id.betatest_howto_items_layout) ViewGroup howtoViewGroup;
    @BindView(R.id.betatest_howto_guide_textview) TextView howtoGuideTextView;

    @Inject
    BetaTestDetailContract.Presenter presenter;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private MissionListAdapter missionListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");

        this.setContentView(R.layout.activity_betatest_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        DaggerBetaTestDetailDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new BetaTestDetailDagger.Module(this))
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
            gameDescriptionGroup.setVisibility(View.VISIBLE);
            descriptionTextView.setText(betaTest.getDescription());
        } else {
            gameDescriptionGroup.setVisibility(View.GONE);
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
            purposeGroup.setVisibility(View.GONE);
        } else {
            purposeGroup.setVisibility(View.VISIBLE);
            purposeDescriptionTextView.setText(betaTest.getPurpose());
        }

        // 리워드 목록
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
        howtoGuideTextView.setText(String.format(getString(R.string.betatest_detail_howto_guide), betaTest.getRewards().getMinimumDelay() != null ? betaTest.getRewards().getMinimumDelay() : DEFAULT_REWARDS_MINIMUM_DELAY));
        Observable.from(betaTest.getMissions())
                .filter(mission -> !FomesConstants.BetaTest.Mission.TYPE_HIDDEN.equals(mission.getItem().getType()))
                .subscribe(displayedMission -> {
                    View missionItemView = getLayoutInflater().inflate(R.layout.item_betatest_mission_item, null);

                    ImageView missionImageView = missionItemView.findViewById(R.id.mission_icon);
                    TextView missionTitleTextView = missionItemView.findViewById(R.id.mission_title);
                    TextView missionItemTitleTextView = missionItemView.findViewById(R.id.mission_item_title);

                    Glide.with(this).load(displayedMission.getIconImageUrl())
                            .apply(new RequestOptions()
                                    .fitCenter())
                            .into(missionImageView);
                    missionTitleTextView.setText(displayedMission.getTitle());
                    missionItemTitleTextView.setText(displayedMission.getItem().getTitle());

                    howtoViewGroup.addView(missionItemView);
                }, e -> Log.e(TAG, String.valueOf(e)), () -> {
                    View missionItemView = getLayoutInflater().inflate(R.layout.item_betatest_mission_item, null);

                    ImageView missionImageView = missionItemView.findViewById(R.id.mission_icon);
                    TextView missionTitleTextView = missionItemView.findViewById(R.id.mission_title);
                    TextView missionItemTitleTextView = missionItemView.findViewById(R.id.mission_item_title);

                    missionImageView.setImageResource(R.drawable.icon_test_awards);
                    missionTitleTextView.setText("테스터 시상식");
                    missionItemTitleTextView.setText("대상사 선정 & 보상 지급");

                    howtoViewGroup.addView(missionItemView);
                });


        missionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(this, R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
        missionRecyclerView.addItemDecoration(dividerItemDecoration);
        missionListAdapter = new MissionListAdapter(presenter, this);
        missionRecyclerView.setAdapter(missionListAdapter);

        // TODO : [Adapter MVP] 리팩토링 후 Presenter 로 로직 이동 필요.. 이름은 아마도 refresh? 혹은 reset..?? set..??
        presenter.getDisplayedMissionList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(missionList -> {
                    missionListAdapter.setMissionList(missionList);
                    missionListAdapter.notifyDataSetChanged();
                });

        contentsLayout.setVisibility(View.VISIBLE);
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
    public void refreshMissionList() {
        Log.d(TAG, "refreshMissionList");

        // TODO : [Adapter MVP] 리팩토링 후 Presenter 로 로직 이동 필요.. 이름은 아마도 refresh? 혹은 reset..?? set..??
        presenter.getDisplayedMissionList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(missionList -> {
                    missionListAdapter.setMissionList(missionList);
                    missionListAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public View inflate(int layoutResId) {
        return getLayoutInflater().inflate(layoutResId, null);
    }

    @Override
    public void startWebViewActivity(String title, String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(FomesConstants.WebView.EXTRA_TITLE, title);
        intent.putExtra(FomesConstants.WebView.EXTRA_CONTENTS, url);
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
