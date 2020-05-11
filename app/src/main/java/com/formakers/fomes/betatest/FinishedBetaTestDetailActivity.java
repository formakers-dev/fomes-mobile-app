package com.formakers.fomes.betatest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;

import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class FinishedBetaTestDetailActivity extends FomesBaseActivity implements FinishedBetaTestDetailContract.View {

    private static final String TAG = "FinishedBetaTestDetailActivity";

    @BindView(R.id.betatest_overview_imageview) ImageView coverIamgeView;
    @BindView(R.id.betatest_plan) TextView planTextView;
    @BindView(R.id.betatest_my_status) TextView myStatusTextView;
    @BindView(R.id.betatest_title_textview) TextView titleTextView;
    @BindView(R.id.betatest_subtitle_textview) TextView subTitleTextView;

    @BindView(R.id.betatest_company_image) ImageView companyImageView;
    @BindView(R.id.betatest_company_name) TextView companyNameTextView;
    @BindView(R.id.betatest_company_says) TextView companySaysTextView;
    @BindView(R.id.betatest_epilogue_button) Button epilogueButton;

    @BindView(R.id.betatest_awards_price) TextView awardsPriceTextView;
    @BindView(R.id.betatest_awards_nickname) TextView awardsNickNameTextView;
    @BindView(R.id.betatest_my_certificates_button) Button certificateButton;

    @Inject FinishedBetaTestDetailContract.Presenter presenter;

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

        Bundle bundle = getIntent().getExtras();
        bind(bundle);

        String betaTestId = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);

        this.presenter.requestEpilogue(betaTestId);
        this.presenter.requestAwardRecordOfBest(betaTestId);
    }

    public void bind(Bundle bundle) {
        String id = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);
        String title = bundle.getString(FomesConstants.BetaTest.EXTRA_TITLE);
        String subTitle = bundle.getString(FomesConstants.BetaTest.EXTRA_SUBTITLE);
        String coverImageUrl = bundle.getString(FomesConstants.BetaTest.EXTRA_COVER_IMAGE_URL);
        @StringRes int planStringResId = bundle.getInt(FomesConstants.BetaTest.EXTRA_PLAN);
        boolean isPremiumPlan = bundle.getBoolean(FomesConstants.BetaTest.EXTRA_IS_PREMIUM_PLAN, false);
        boolean isCompleted = bundle.getBoolean(FomesConstants.BetaTest.EXTRA_IS_COMPLETED, false);

        presenter.getImageLoader().loadImage(coverIamgeView, coverImageUrl);
        titleTextView.setText(title);
        subTitleTextView.setText(subTitle);

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

        awardsNickNameTextView.setSelected(true);

        certificateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BetaTestCertificateActivity.class);
            intent.putExtra(FomesConstants.BetaTest.EXTRA_ID, id);
            startActivity(intent);
        });
    }

    @Override
    public void bindEpilogue(BetaTest.Epilogue epilogue) {
        companyNameTextView.setText(epilogue.getCompanyName());
        companySaysTextView.setText(epilogue.getCompanySays());

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

        companySaysTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindAwards(AwardRecord bestAwardRecord) {
        awardsNickNameTextView.setText(bestAwardRecord.getNickName());
        awardsPriceTextView.setText(bestAwardRecord.getReward().getDescription());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return null;
    }

    @Override
    public void setPresenter(FinishedBetaTestDetailContract.Presenter presenter) {

    }
}
