package com.formakers.fomes.betatest;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class FinishedBetaTestDetailActivity extends FomesBaseActivity implements FinishedBetaTestDetailContract.View {

    private static final String TAG = "FinishedBetaTestDetailActivity";

    @BindView(R.id.action_bar) Toolbar actionBar;
    @BindView(R.id.betatest_overview_imageview) ImageView coverImageView;
    @BindView(R.id.betatest_reward_min) TextView minRewardTextView;
    @BindView(R.id.betatest_reward_max) TextView maxRewardTextView;
    @BindView(R.id.betatest_plan) TextView planTextView;
    @BindView(R.id.betatest_my_status) TextView myStatusTextView;
    @BindView(R.id.betatest_title_textview) TextView titleTextView;
    @BindView(R.id.betatest_subtitle_textview) TextView subTitleTextView;
    @BindView(R.id.betatest_tag_layout) ViewGroup tagViewGroup;

    @BindView(R.id.betatest_company_image) ImageView companyImageView;
    @BindView(R.id.betatest_company_name) TextView companyNameTextView;
    @BindView(R.id.betatest_company_says) TextView companySaysTextView;
    @BindView(R.id.betatest_epilogue_button) Button epilogueButton;

    @BindView(R.id.betatest_awards_group) Group awardGroup;

    @BindView(R.id.item_awards_viewpager) ViewPager awardsViewPager;
    @BindView(R.id.item_awards_view_pager_indicator) TabLayout awardsViewPagerIndicator;
    @BindView(R.id.betatest_awards_wonder) TextView awardsWonderTextView;

    @BindView(R.id.betatest_subtitle_my_results) TextView myResultSubTitleTextView;
    @BindView(R.id.layout_recheck_my_answers) ViewGroup recheckMyAnswerLayout;
    @BindView(R.id.betatest_my_certificates_button) Button certificateButton;

    @Inject FinishedBetaTestDetailContract.Presenter presenter;

    private FomesNoticeDialog noticeDialog = new FomesNoticeDialog();
    private FinishedBetaTestAwardPagerAdapter awardPagerAdapter = null;
    private FinishedBetaTestAwardPagerAdapterContract.View awardPagerAdapterView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate intent=" + getIntent().getStringExtra(FomesConstants.BetaTest.EXTRA_TITLE));

        this.setContentView(R.layout.activity_finished_betatest_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        DaggerFinishedBetaTestDetailDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new FinishedBetaTestDetailDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (getIntent() == null || getIntent().getExtras() == null) {
            return;
        }

        setAwardPagerAdapter();

        Bundle bundle = getIntent().getExtras();
        BetaTest betaTest = bundle.getParcelable(FomesConstants.BetaTest.EXTRA_BETA_TEST);

        if (betaTest == null) {
            Log.e(TAG, "betaTest is null!");
            return;
        }

        bind(betaTest);

        setActionBar();
        initViews();

        this.presenter.setBetaTest(betaTest);
        this.presenter.requestEpilogueAndAwards(betaTest.getId());
        this.presenter.requestRecheckableMissions(betaTest.getId());
    }

    private void setAwardPagerAdapter() {
        awardPagerAdapter = new FinishedBetaTestAwardPagerAdapter(this) ;
        awardPagerAdapter.setPresenter(presenter);
        awardsViewPager.setAdapter(awardPagerAdapter);
        this.presenter.setFinishedBetaTestAwardPagerAdapterModel(awardPagerAdapter);
        this.awardPagerAdapterView = awardPagerAdapter;

        awardsViewPagerIndicator.setupWithViewPager(awardsViewPager, true);
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

    private void initViews() {
        disableMyAnswersView();
    }

    public void bind(BetaTest betaTest) {
        if (isUnavailableViewControl()) {
            return;
        }

        Resources res = this.getResources();

        if (actionBar != null) {
            actionBar.setTitle(betaTest.getTitle());
        }

        presenter.getImageLoader().loadImage(coverImageView, betaTest.getCoverImageUrl());
        titleTextView.setText(betaTest.getTitle());
        subTitleTextView.setText(betaTest.getDisplayDescription());

        // 태그
        tagViewGroup.removeAllViews();
        for (String tag : betaTest.getTags()) {
            Chip tagView = (Chip) getLayoutInflater().inflate(R.layout.item_betatest_tag, null);
            tagView.setText(tag);
            tagViewGroup.addView(tagView);
        }

        if (this.presenter.isActivatedPointSystem()) {
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

        } else {
            minRewardTextView.setVisibility(View.GONE);
            maxRewardTextView.setVisibility(View.GONE);

            @StyleRes int planStyleResId;
            @ColorRes int planNameColorId;
            if (betaTest.isPremiumPlan()) {
                planStyleResId = R.style.BetaTestTheme_Plan_Premium;
                planNameColorId = R.color.fomes_orange;
            } else {
                planStyleResId = R.style.BetaTestTheme_Plan_Lite;
                planNameColorId = R.color.colorPrimary;
            }

            planTextView.setText(betaTest.getPlanStringResId());
            planTextView.setTextColor(getResources().getColor(planNameColorId));
            planTextView.setBackground(getResources().getDrawable(R.drawable.item_rect_rounded_corner_background, new ContextThemeWrapper(this, planStyleResId).getTheme()));
        }

        myStatusTextView.setVisibility(betaTest.isCompleted() ? View.VISIBLE : View.GONE);
        myStatusTextView.setText(R.string.betatest_my_status_completed);
        myStatusTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        myStatusTextView.setBackground(getResources().getDrawable(R.drawable.item_rect_rounded_corner_background,  new androidx.appcompat.view.ContextThemeWrapper(this,  R.style.BetaTestTheme_MyStatus_Completed).getTheme()));

        myResultSubTitleTextView.setText(String.format(getString(betaTest.isCompleted() ? R.string.finished_betatest_detail_my_results_subtitle : R.string.finished_betatest_detail_my_results_subtitle_not_completed), betaTest.getTitle()));

        certificateButton.setEnabled(betaTest.isCompleted());

        certificateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BetaTestCertificateActivity.class);
            intent.putExtra(FomesConstants.BetaTest.EXTRA_ID, betaTest.getId());
            startActivity(intent);
        });
    }

    @Override
    public void bindEpilogueView(BetaTest.Epilogue epilogue) {
        if (isUnavailableViewControl()) {
            return;
        }

        boolean isEnabledEpilogue = !TextUtils.isEmpty(epilogue.getDeeplink());
        epilogueButton.setEnabled(isEnabledEpilogue);
        epilogueButton.setOnClickListener(isEnabledEpilogue ? v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(epilogue.getDeeplink()));
            startActivity(intent);
        } : null);

        String imageUrl = epilogue.getCompanyImageUrl();

        if (!TextUtils.isEmpty(imageUrl)) {
            this.presenter.getImageLoader().loadImage(companyImageView, imageUrl,
                    RequestOptions.circleCropTransform(), false, true);
        } else {
            companyImageView.setImageDrawable(getDrawable(R.drawable.fomes_profile_default));
        }

        companySaysTextView.setText(epilogue.getCompanySays());
        companySaysTextView.setVisibility(View.VISIBLE);
        companySaysTextView.startAnimation(getFadeInAnimation(1000));

        companyNameTextView.setText(epilogue.getCompanyName());
        companyNameTextView.startAnimation(getFadeInAnimation(1000));
    }

    @Override
    public void bindMyAnswersView(List<Mission> missions) {
        if (isUnavailableViewControl()) {
            return;
        }

        recheckMyAnswerLayout.removeAllViews();
        for (Mission mission : missions) {
            if (mission.isCompleted() && mission.isRecheckable()) {
                Button recheckableButton = (Button) LayoutInflater.from(this).inflate(R.layout.item_button, null);
                recheckableButton.setText(String.format(getString(R.string.finished_betatest_recheck_my_answer_button_text_format), mission.getTitle()));
                recheckableButton.setOnClickListener(v -> this.presenter.emitRecheckMyAnswer(mission));
                recheckMyAnswerLayout.addView(recheckableButton);
            }
        }
    }

    @Override
    public void bindAwardRecordsWithRewardItems(List<BetaTest.Rewards.RewardItem> rewardItemList) {
        if (isUnavailableViewControl()) {
            return;
        }

        awardPagerAdapter.addAllFromRewardItems(rewardItemList);

        refreshAwardPagerView();
    }

    @Override
    public void showNoticePopupView(int titleResId, int subTitleResId, int imageResId,
                                    int positiveButtonTextResId, View.OnClickListener positiveButtonClickListener) {
        if (isUnavailableViewControl()) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(FomesNoticeDialog.EXTRA_TITLE, getString(titleResId));
        bundle.putString(FomesNoticeDialog.EXTRA_SUBTITLE, getString(subTitleResId));
        bundle.putInt(FomesNoticeDialog.EXTRA_IMAGE_RES_ID, imageResId);

        noticeDialog.setArguments(bundle);
        noticeDialog.setPositiveButton(getString(positiveButtonTextResId), positiveButtonClickListener);
        noticeDialog.show(getSupportFragmentManager(), "Test");
    }

    @Override
    public void startWebViewActivity(String title, String url) {
        if (isUnavailableViewControl()) {
            return;
        }

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
    public void disableEpilogueView() {
        if (isUnavailableViewControl()) {
            return;
        }

        epilogueButton.setEnabled(false);

        awardsWonderTextView.setVisibility(View.GONE);

        companyNameTextView.setText("게임사 소감 준비중");
        companySaysTextView.setText("조금만 기다려주세요 🙏");
        companySaysTextView.setVisibility(View.VISIBLE);
        companyImageView.setImageDrawable(getDrawable(R.drawable.fomes_profile_default));

        companySaysTextView.startAnimation(getFadeInAnimation(1000));
        companyNameTextView.startAnimation(getFadeInAnimation(1000));
        companyImageView.startAnimation(getFadeInAnimation(1000));
    }

    @Override
    public void disableMyAnswersView() {
        if (isUnavailableViewControl()) {
            return;
        }

        recheckMyAnswerLayout.removeAllViews();
        Button recheckableButton = (Button) LayoutInflater.from(this).inflate(R.layout.item_button, null);
        recheckableButton.setText(R.string.finished_betatest_recheck_my_answer_title);
        recheckableButton.setEnabled(false);
        recheckMyAnswerLayout.addView(recheckableButton);
    }

    @Override
    public void refreshAwardPagerView() {
        if (isUnavailableViewControl()) {
            return;
        }

        this.awardPagerAdapterView.notifyDataSetChanged();
    }

    @Override
    public void hideAwardsView() {
        if (isUnavailableViewControl()) {
            return;
        }

        awardGroup.setVisibility(View.GONE);
    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return null;
    }

    @Override
    public void setPresenter(FinishedBetaTestDetailContract.Presenter presenter) {

    }
}
