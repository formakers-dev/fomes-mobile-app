package com.formakers.fomes.betatest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.FomesCharacterDialog;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.formakers.fomes.common.constant.FomesConstants.FomesEventLog.Code.BETA_TEST_DETAIL_ENTER;

public class BetaTestDetailActivity extends FomesBaseActivity implements BetaTestDetailContract.View {

    private static final String TAG = "BetaTestDetailActivity";

    public static final int REQUEST_CODE_MISSION = 1001;
    private static final int DEFAULT_REWARDS_MINIMUM_DELAY = 5;

    @BindView(R.id.action_bar) Toolbar actionBar;
    @BindView(R.id.loading) ProgressBar loadingProgressBar;
    @BindView(R.id.betatest_overview_info) ViewGroup betatestOverviewInfoViewGroup;
    @BindView(R.id.betatest_overview_imageview) ImageView overviewImageView;
//    @BindView(R.id.betatest_detail_app_icon) ImageView iconImageView;
    @BindView(R.id.betatest_reward_min) TextView minRewardTextView;
    @BindView(R.id.betatest_reward_max) TextView maxRewardTextView;
    @BindView(R.id.betatest_plan) TextView planTextView;
    @BindView(R.id.betatest_my_status) TextView myStatusTextView;
    @BindView(R.id.betatest_title_textview) TextView titleTextView;
    @BindView(R.id.betatest_subtitle_textview) TextView subTitleTextView;
    @BindView(R.id.betatest_tag_layout) ViewGroup tagViewGroup;
    @BindView(R.id.betatest_detail_period_textview) TextView periodTextView;
    @BindView(R.id.betatest_detail_d_day_textview) TextView dDayTextView;
    @BindView(R.id.betatest_contents_layout) ViewGroup contentsLayout;
    @BindView(R.id.betatest_game_description_group) Group gameDescriptionGroup;
    @BindView(R.id.betatest_detail_game_description_textview) TextView descriptionTextView;
    @BindView(R.id.betatest_reward_items_layout) ViewGroup rewardViewGroup;
    @BindView(R.id.betatest_reward_guide_textview) TextView rewardGuideTextView;
    @BindView(R.id.betatest_mission_list) RecyclerView missionRecyclerView;
    @BindView(R.id.betatest_purpose_group) Group purposeGroup;
    @BindView(R.id.betatest_purpose_title_textview) TextView purposeTitleTextView;
    @BindView(R.id.betatest_purpose_description_textview) TextView purposeDescriptionTextView;
    @BindView(R.id.betatest_purpose_from_textview) TextView purposeFromTextView;
    @BindView(R.id.betatest_howto_items_layout) ViewGroup howtoViewGroup;
    @BindView(R.id.betatest_howto_guide_textview) TextView howtoGuideTextView;

    @Inject
    BetaTestDetailContract.Presenter presenter;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private MissionListAdapterContract.View missionListAdapterView;

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
            Toast.makeText(this, "????????? ??????????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setActionBar();

        String id = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);
        this.presenter.load(id);

        presenter.sendEventLog(BETA_TEST_DETAIL_ENTER, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

        if (requestCode == REQUEST_CODE_MISSION) {
            if (data != null) {
                String missionId = data.getStringExtra(FomesConstants.WebView.EXTRA_MISSION_ID);
                Log.v(TAG, "onActivityResult missionId=" + missionId);
                this.missionListAdapterView.clickRefreshButton(missionId);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    private void setActionBar() {
        if (actionBar != null) {
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            setSupportActionBar(actionBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            actionBar.setNavigationIcon(R.drawable.ic_home_as_up);
            actionBar.setPadding(0, statusBarHeight, 0, 0);
            actionBar.getLayoutParams().height = actionBar.getLayoutParams().height + statusBarHeight;
            actionBar.setLayoutParams(actionBar.getLayoutParams());
        }
    }

    @Override
    public void bind(BetaTest betaTest) {
        Resources res = this.getResources();

        this.presenter.getImageLoader().loadImage(overviewImageView, betaTest.getCoverImageUrl(),
                new RequestOptions().centerCrop());

//        this.presenter.getImageLoader().loadImage(iconImageView, betaTest.getIconImageUrl(),
//                new RequestOptions().override(120, 120)
//                .centerCrop()
//                .transform(new RoundedCorners(16))
//        , false);

        if (actionBar != null) {
            actionBar.setTitle(betaTest.getTitle());
        }

        titleTextView.setText(betaTest.getTitle());
        subTitleTextView.setText(betaTest.getDisplayDescription());

        // ??????
        tagViewGroup.removeAllViews();
        for (String tag : betaTest.getTags()) {
            Chip tagView = (Chip) getLayoutInflater().inflate(R.layout.item_betatest_tag, null);
            tagView.setText(tag);
            tagViewGroup.addView(tagView);
        }

        // ?????? ?????? ?????? (???????????? : BetaTestListAdapter, BetaTestDetailActivity, FinishedBetaTestDetailActivity)
        planTextView.setVisibility(View.GONE);
        minRewardTextView.setVisibility(View.VISIBLE);
        maxRewardTextView.setVisibility(View.VISIBLE);

        try {
            BetaTest.Rewards.RewardItem minRewardItem = betaTest.getRewards().getMinReward();
            minRewardTextView.setText(String.format(res.getString(R.string.betatest_main_tag_min_reward), minRewardItem.getSummaryString()));

            BetaTest.Rewards.RewardItem maxRewardItem = betaTest.getRewards().getMaxReward();
            maxRewardTextView.setText(String.format(res.getString(R.string.betatest_main_tag_max_reward), maxRewardItem.getSummaryString()));
        } catch (Exception e) {
            minRewardTextView.setVisibility(View.GONE);
            maxRewardTextView.setVisibility(View.GONE);
        }

        // ???????????? ?????? ??????
        int myStatusVisibility = betaTest.isAttended() ? View.VISIBLE : View.GONE;
        @StyleRes int myStatusStyleResId = betaTest.isCompleted() ? R.style.BetaTestTheme_MyStatus_Completed : R.style.BetaTestTheme_MyStatus;
        @StringRes int myStatusStringId = betaTest.isCompleted() ? R.string.betatest_my_status_completed : R.string.betatest_my_status_attend;
        @ColorRes int myStatusTextColorId = betaTest.isCompleted() ? R.color.colorPrimary : R.color.fomes_white;

        myStatusTextView.setVisibility(myStatusVisibility);
        myStatusTextView.setText(myStatusStringId);
        myStatusTextView.setTextColor(getResources().getColor(myStatusTextColorId));
        myStatusTextView.setBackground(getResources().getDrawable(R.drawable.item_rect_rounded_corner_background,  new androidx.appcompat.view.ContextThemeWrapper(this, myStatusStyleResId).getTheme()));

        String description = betaTest.getDescription();
        if (!TextUtils.isEmpty(description)) {
            gameDescriptionGroup.setVisibility(View.VISIBLE);
            descriptionTextView.setText(betaTest.getDescription());
        } else {
            gameDescriptionGroup.setVisibility(View.GONE);
        }

        betatestOverviewInfoViewGroup.setVisibility(View.VISIBLE);

        // ????????? ??????
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.YYYY_DOT_MM_DOT_DD, Locale.getDefault());

        periodTextView.setText(String.format("%s ~ %s",
                dateFormat.format(betaTest.getOpenDate()),
                dateFormat.format(betaTest.getCloseDate())));

        // ?????????
        Bundle bundle = getIntent().getExtras();
        long remainDays = bundle.getLong(FomesConstants.BetaTest.EXTRA_REMAIN_DAYS, 0);

        String projectStatus;
        if (remainDays > 0) {
            projectStatus = String.format("%d??? ??????", remainDays + 1);
        } else {
            projectStatus = "?????? ??????";
        }

        dDayTextView.setText(projectStatus);


        // ????????? ??????
        if (TextUtils.isEmpty(betaTest.getPurpose())) {
            purposeGroup.setVisibility(View.GONE);
        } else {
            purposeGroup.setVisibility(View.VISIBLE);
            purposeDescriptionTextView.setText(betaTest.getPurpose());

            // ?????? ???????????? - ????????? ??????(?)??? ????????? ????????????
            if (!betaTest.getTitle().contains("?????? ?????????")) {
                purposeFromTextView.setText("");
            }
        }

        // ????????? ??????
        for (BetaTest.Rewards.RewardItem rewardItem : betaTest.getRewards().getList()) {
            View rewardItemView = getLayoutInflater().inflate(R.layout.item_betatest_reward, null);

            ImageView rewardItemIconImageView = rewardItemView.findViewById(R.id.betatest_reward_icon);
            TextView rewardItemTitleTextView = rewardItemView.findViewById(R.id.betatest_reward_title);
            TextView rewardItemDescriptionTextView = rewardItemView.findViewById(R.id.betatest_reward_description);

            this.presenter.getImageLoader().loadImage(rewardItemIconImageView, rewardItem.getIconImageUrl(),
                    new RequestOptions().fitCenter());

            rewardItemTitleTextView.setText(rewardItem.getTitle());

            String rewardCountString = (rewardItem.getCount() == null)? "????????? ??????" : rewardItem.getCount() + "??? ??????";
            rewardItemDescriptionTextView.setText(getString(R.string.betatest_detail_reward_description, rewardItem.getContent(), rewardCountString));

            rewardViewGroup.addView(rewardItemView);
        }

        int rewardDelayDays = betaTest.getRewards().getMinimumDelay() != null ? betaTest.getRewards().getMinimumDelay() : DEFAULT_REWARDS_MINIMUM_DELAY;

        // ????????? ?????? ????????? ?????????
        rewardGuideTextView.setText(String.format(getString(R.string.betatest_detail_reward_date_guide), rewardDelayDays));

        // ????????? ??????
        howtoGuideTextView.setText(String.format(getString(R.string.betatest_detail_howto_guide), rewardDelayDays));

        Observable.from(betaTest.getMissions())
                .subscribe(displayedMission -> {
                    View missionItemView = getLayoutInflater().inflate(R.layout.item_betatest_mission_item, null);

                    ImageView missionImageView = missionItemView.findViewById(R.id.mission_icon);
                    TextView missionTitleTextView = missionItemView.findViewById(R.id.mission_title);
                    TextView missionItemTitleTextView = missionItemView.findViewById(R.id.mission_item_title);

                    if (FomesConstants.BetaTest.Mission.TYPE_INSTALL.equals(displayedMission.getType())) {
                        missionImageView.setImageResource(R.drawable.icon_mission_type_play);
                    } else {
                        missionImageView.setImageResource(R.drawable.icon_mission_type_survey);
                    }

                    missionTitleTextView.setText(String.format(Locale.getDefault(), "%d?????? ??????", displayedMission.getOrder()));
                    missionItemTitleTextView.setText(displayedMission.getTitle());

                    howtoViewGroup.addView(missionItemView);
                }, e -> Log.e(TAG, String.valueOf(e)), () -> {
                    View missionItemView = getLayoutInflater().inflate(R.layout.item_betatest_mission_item, null);

                    ImageView missionImageView = missionItemView.findViewById(R.id.mission_icon);
                    TextView missionTitleTextView = missionItemView.findViewById(R.id.mission_title);
                    TextView missionItemTitleTextView = missionItemView.findViewById(R.id.mission_item_title);

                    missionImageView.setImageResource(R.drawable.icon_test_awards);
                    missionTitleTextView.setText("?????????");
                    missionItemTitleTextView.setText("????????? ?????? & ?????? ??????");

                    howtoViewGroup.addView(missionItemView);
                });


        missionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        missionRecyclerView.setHasFixedSize(true);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(this, R.style.FomesMainTabTheme_BetaTestDivider).getTheme()));
        missionRecyclerView.addItemDecoration(dividerItemDecoration);

        MissionListAdapter missionListAdapter = new MissionListAdapter(presenter);
        missionListAdapterView = missionListAdapter;
        this.presenter.setAdapterModel(missionListAdapter);
        missionRecyclerView.setAdapter(missionListAdapter);

        this.presenter.displayMissionList();

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
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void refreshMissionList() {
        Log.d(TAG, "refreshMissionList");
        this.missionListAdapterView.notifyDataSetChanged();
    }

    @Override
    public void refreshMissionBelowAllChanged(int missionPosition) {
        this.missionListAdapterView.notifyItemBelowAllChanged(missionPosition);
    }

    @Override
    public View inflate(int layoutResId) {
        return getLayoutInflater().inflate(layoutResId, null);
    }

    @Override
    public void startSurveyWebViewActivity(String missionId, String title, String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(FomesConstants.WebView.EXTRA_TITLE, title);
        intent.putExtra(FomesConstants.WebView.EXTRA_CONTENTS, url);
        intent.putExtra(FomesConstants.WebView.EXTRA_IS_PREVENT_BACK_PRESSED, true);
        intent.putExtra(FomesConstants.WebView.EXTRA_MISSION_ID, missionId);
        startActivityForResult(intent, REQUEST_CODE_MISSION);
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

    @Override
    public void showPlayTimeSuccessPopup(String playTimeString) {
        Bundle bundle = new Bundle();
        bundle.putString(FomesCharacterDialog.EXTRA_TITLE, "????????? ?????? : " + playTimeString);
        bundle.putString(FomesCharacterDialog.EXTRA_SUBTITLE, "<b>[????????? ?????? ??????]</b>\n?????? ???????????? ?????????!\n?????? ?????? ????????? ????????? ?????????!");
        bundle.putInt(FomesCharacterDialog.EXTRA_IMAGE_RES_ID, R.drawable.fomes_congratulation);
        bundle.putString(FomesCharacterDialog.EXTRA_BUTTON_TEXT, "??????, ?????????!");

        FomesCharacterDialog fomesCharacterDialog = new FomesCharacterDialog();
        fomesCharacterDialog.setArguments(bundle);
        fomesCharacterDialog.show(getSupportFragmentManager(), "PlayTimeSuccessPopup");
    }

    @Override
    public void showPlayTimeZeroPopup() {
        Bundle bundle = new Bundle();
        bundle.putString(FomesCharacterDialog.EXTRA_TITLE, "????????? ?????? : 0???");
        bundle.putString(FomesCharacterDialog.EXTRA_SUBTITLE, "????????? ???????????????\n?????? ??????????????????!");
        bundle.putInt(FomesCharacterDialog.EXTRA_IMAGE_RES_ID, R.drawable.fomes_happy);
        bundle.putString(FomesCharacterDialog.EXTRA_BUTTON_TEXT, "?????????!");
        bundle.putString(FomesCharacterDialog.EXTRA_GUIDE, "????????? ?????? ????????? ??? ???????????? <a href='https://www.notion.so/formakers/FAQ-c9e86f394eac4ff5870c6c71421137eb'>??? ??????</a>??? ???????????????");

        FomesCharacterDialog fomesCharacterDialog = new FomesCharacterDialog();
        fomesCharacterDialog.setArguments(bundle);
        fomesCharacterDialog.show(getSupportFragmentManager(), "PlayTimeZeroPopup");
    }

    @Override
    public void showPlayTimeErrorPopup(String missionId, String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(FomesCharacterDialog.EXTRA_TITLE, "????????? ?????? : 0???");
        bundle.putString(FomesCharacterDialog.EXTRA_SUBTITLE, "<b>[????????? ?????? ??????]</b>\n?????? ????????? ????????? ?????????????\n????????? ????????? ????????????\n????????? ?????????????????????!");
        bundle.putInt(FomesCharacterDialog.EXTRA_IMAGE_RES_ID, R.drawable.fomes_search);
        bundle.putString(FomesCharacterDialog.EXTRA_BUTTON_TEXT, "?????????! ?????? ?????????!");
        bundle.putString(FomesCharacterDialog.EXTRA_GUIDE, "????????? ????????? ????????? ??? ????????? ??????????????????!");

        FomesCharacterDialog fomesCharacterDialog = new FomesCharacterDialog();
        fomesCharacterDialog.setPositiveButtonClickListener(v -> startSurveyWebViewActivity(missionId, title, url));
        fomesCharacterDialog.setArguments(bundle);
        fomesCharacterDialog.show(getSupportFragmentManager(), "PlayTimeErrorPopup");
    }
}
