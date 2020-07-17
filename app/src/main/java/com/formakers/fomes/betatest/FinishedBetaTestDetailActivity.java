package com.formakers.fomes.betatest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class FinishedBetaTestDetailActivity extends FomesBaseActivity implements FinishedBetaTestDetailContract.View {

    private static final String TAG = "FinishedBetaTestDetailActivity";

    @BindView(R.id.action_bar) Toolbar actionBar;
    @BindView(R.id.betatest_overview_imageview) ImageView coverImageView;
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

//    @BindView(R.id.betatest_title_awards_best) TextView bestAwardsTitleTextView;
//    @BindView(R.id.betatest_awards_price) TextView awardsPriceTextView;
//    @BindView(R.id.betatest_awards_nickname) TextView awardsNickNameTextView;
//    @BindView(R.id.betatest_awards_nickname_end) TextView awardsNickNameEndTextView;

    @BindView(R.id.item_awards_viewpager) ViewPager awardsViewPager;
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
        bind(bundle);

        setActionBar();

        String betaTestId = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);

        initViews();

        this.presenter.requestEpilogue(betaTestId);
        this.presenter.requestAwardRecords(betaTestId, bundle.getParcelableArrayList(FomesConstants.BetaTest.EXTRA_REWARD_ITEMS));
        this.presenter.requestRecheckableMissions(betaTestId);
    }

    private void setAwardPagerAdapter() {
        awardPagerAdapter = new FinishedBetaTestAwardPagerAdapter(this) ;
        awardPagerAdapter.setPresenter(presenter);
        awardsViewPager.setAdapter(awardPagerAdapter);
        this.presenter.setFinishedBetaTestAwardPagerAdapterModel(awardPagerAdapter);
        this.awardPagerAdapterView = awardPagerAdapter;
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

    public void bind(Bundle bundle) {
        if (isUnavailableViewControl()) {
            return;
        }

        // 프레젠터로 데이터 넘기고 뷰에서 표출해줘야 하지 않으려나.. 싶긴함
        String id = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);
        String title = bundle.getString(FomesConstants.BetaTest.EXTRA_TITLE);
        String subTitle = bundle.getString(FomesConstants.BetaTest.EXTRA_SUBTITLE);
        String coverImageUrl = bundle.getString(FomesConstants.BetaTest.EXTRA_COVER_IMAGE_URL);
        @StringRes int planStringResId = bundle.getInt(FomesConstants.BetaTest.EXTRA_PLAN);
//        int topRewardTypeCode = bundle.getInt(FomesConstants.BetaTest.EXTRA_TOP_REWARD_TYPE_CODE);
//        String topRewardDescription = bundle.getString(FomesConstants.BetaTest.EXTRA_TOP_REWARD_DESCRIPTION);
        boolean isPremiumPlan = bundle.getBoolean(FomesConstants.BetaTest.EXTRA_IS_PREMIUM_PLAN, false);
        boolean isCompleted = bundle.getBoolean(FomesConstants.BetaTest.EXTRA_IS_COMPLETED, false);
        ArrayList<String> tagList = bundle.getStringArrayList(FomesConstants.BetaTest.EXTRA_TAG_LIST);

        if (actionBar != null) {
            actionBar.setTitle(title);
        }

        presenter.getImageLoader().loadImage(coverImageView, coverImageUrl);
        titleTextView.setText(title);
        subTitleTextView.setText(subTitle);

        // 태그
        tagViewGroup.removeAllViews();
        for (String tag : tagList) {
            Chip tagView = (Chip) getLayoutInflater().inflate(R.layout.item_betatest_tag, null);
            tagView.setText(tag);
            tagViewGroup.addView(tagView);
        }

        @StyleRes int planStyleResId;
        @ColorRes int planNameColorId;
        if (isPremiumPlan) {
            planStyleResId = R.style.BetaTestTheme_Plan_Premium;
            planNameColorId = R.color.fomes_orange;
        } else {
            planStyleResId = R.style.BetaTestTheme_Plan_Lite;
            planNameColorId = R.color.colorPrimary;
        }

        planTextView.setText(planStringResId);
        planTextView.setTextColor(getResources().getColor(planNameColorId));
        planTextView.setBackground(getResources().getDrawable(R.drawable.item_rect_rounded_corner_background, new ContextThemeWrapper(this, planStyleResId).getTheme()));

        myStatusTextView.setVisibility(isCompleted ? View.VISIBLE : View.GONE);

//        setBestAwardsTitleView(topRewardTypeCode);

//        awardsNickNameTextView.setSelected(true);
//        awardsPriceTextView.setText(topRewardDescription);

        myResultSubTitleTextView.setText(String.format(getString(isCompleted ? R.string.finished_betatest_detail_my_results_subtitle : R.string.finished_betatest_detail_my_results_subtitle_not_completed), title));

        certificateButton.setEnabled(isCompleted);

        certificateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BetaTestCertificateActivity.class);
            intent.putExtra(FomesConstants.BetaTest.EXTRA_ID, id);
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
        }

        companySaysTextView.setText(epilogue.getCompanySays());
        companySaysTextView.setVisibility(View.VISIBLE);
        companySaysTextView.startAnimation(getFadeInAnimation(1000));

        companyNameTextView.setText(epilogue.getCompanyName());
        companyNameTextView.startAnimation(getFadeInAnimation(1000));
    }

    @Override
    public void bindAwardsView(List<AwardRecord> awardRecords) {
        if (isUnavailableViewControl()) {
            return;
        }

        if (awardRecords != null && !awardRecords.isEmpty()) {
//            if (awardRecords.size() > 1) {
//                awardsNickNameEndTextView.setText(getString(R.string.finished_betatest_detail_awards_nickname_sir_and_count, awardRecords.size() - 1));
//            }
//
//            setBestAwardsTitleView(awardRecords.get(0).getTypeCode());
//
//            awardsNickNameTextView.setText(awardRecords.get(0).getNickName());
//            awardsNickNameEndTextView.setVisibility(View.VISIBLE);
        }
    }

//    private void setBestAwardsTitleView(int typeCode) {
//        if (isUnavailableViewControl()) {
//            return;
//        }
//
//        if (AwardRecord.TYPE_BEST.equals(typeCode)) {
//            bestAwardsTitleTextView.setText(getString(R.string.finished_betatest_detail_awards_best));
//        }
//    }

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

    private Animation getFadeInAnimation(long durationMills) {
        Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(durationMills);
        return in;
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

        companySaysTextView.startAnimation(getFadeInAnimation(1000));
        companyNameTextView.startAnimation(getFadeInAnimation(1000));
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
        this.awardPagerAdapterView.notifyDataSetChanged();
    }

    @Override
    public void showLoadingView() {
        if (isUnavailableViewControl()) {
            return;
        }
    }

    @Override
    public void hideLoadingView() {
        if (isUnavailableViewControl()) {
            return;
        }
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
