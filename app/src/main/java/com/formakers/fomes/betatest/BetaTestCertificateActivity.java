package com.formakers.fomes.betatest;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class BetaTestCertificateActivity extends FomesBaseActivity implements BetaTestCertificateContract.View {
    private static final String TAG = "BetaTestCertificateActivity";

    @BindView(R.id.loading) ProgressBar loadingProgressBar;
    @BindView(R.id.betatest_certificate_layout) View betaTestCertificateLayout;
    @BindView(R.id.betatest_certificate_error_layout) View betaTestCertificateErrorLayout;
    @BindView(R.id.betatest_app_icon) ImageView betaTestAppIcon;
    @BindView(R.id.betatest_game_title) TextView betaTestGameTitle;
    @BindView(R.id.betatest_period) TextView betaTestPeriod;
    @BindView(R.id.betatest_awards_title) TextView betaTestAwardsTitle;
    @BindView(R.id.betatest_awards_nickname) TextView betaTestAwardsNickName;
    @BindView(R.id.betatest_certificate_description) TextView betaTestCertificateDescription;
    @BindView(R.id.betatest_certificate_fomes_feedback) TextView betaTestCertificateFomesFeedback;

    @Inject BetaTestCertificateContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate intent=" + getIntent().getStringExtra(FomesConstants.BetaTest.EXTRA_ID));

        this.setContentView(R.layout.activity_betatest_certificate);

        getSupportActionBar().setTitle(getString(R.string.betatest_certificate_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerBetaTestCertificateDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new BetaTestCertificateDagger.Module(this))
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

        String betaTestId = bundle.getString(FomesConstants.BetaTest.EXTRA_ID);

        this.presenter.requestBetaTestCertificate(betaTestId);
        this.presenter.requestUserNickName();
    }

    @Override
    public void bindBetaTestCertificate(BetaTest betaTest, AwardRecord awardRecord) {
        if (isUnavailableViewControl()) {
            return;
        }

        bindBetaTestDetail(betaTest);
        bindCertificate(betaTest, awardRecord);
        showCertificateView();
    }

    @Override
    public void bindUserNickName(String nickName) {
        if (isUnavailableViewControl()) {
            return;
        }

        betaTestAwardsNickName.setText(getString(R.string.betatest_certificate_nick_name, nickName));
    }

    private void bindBetaTestDetail(BetaTest betaTest) {
        if (isUnavailableViewControl()) {
            return;
        }

        this.presenter.getImageLoader().loadImage(betaTestAppIcon, betaTest.getIconImageUrl(),
                new RequestOptions().override(120, 120)
                        .centerCrop()
                        .transform(new RoundedCorners(8))
                , false);

        betaTestGameTitle.setText(betaTest.getTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.YYYY_DOT_MM_DOT_DD, Locale.getDefault());
        String formattedOpenDate = dateFormat.format(betaTest.getOpenDate());
        String formattedCloseDate = dateFormat.format(betaTest.getCloseDate());

        if (formattedCloseDate.startsWith(formattedOpenDate.substring(0, 4))) {
            SimpleDateFormat shortenDateFormat = new SimpleDateFormat(DateUtil.MM_DOT_DD, Locale.getDefault());
            betaTestPeriod.setText(String.format("%s ~ %s",
                    formattedOpenDate,
                    shortenDateFormat.format(betaTest.getCloseDate())));
        } else {
            betaTestPeriod.setText(String.format("%s ~ %s",
                    formattedOpenDate,
                    formattedCloseDate));
        }
    }

    private void bindCertificate(BetaTest betaTest, AwardRecord awardRecord) {
        if (isUnavailableViewControl()) {
            return;
        }

        int awardTypeCode = (awardRecord != null) ? awardRecord.getTypeCode().intValue() : 0;

        if (awardTypeCode >= FomesConstants.BetaTest.Award.TYPE_CODE_BEST) {
            betaTestAwardsTitle.setText(awardRecord.getCertificationTitle());
            betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
            betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description_for_best, betaTest.getTitle()));
            betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));

        } else if (awardTypeCode >= FomesConstants.BetaTest.Award.TYPE_CODE_GOOD) {
            betaTestAwardsTitle.setText(awardRecord.getCertificationTitle());
            betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_squash));
            betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description_for_good, betaTest.getTitle()));
            betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));

        } else if (awardTypeCode >= FomesConstants.BetaTest.Award.TYPE_CODE_NORMAL) {
            betaTestAwardsTitle.setText(awardRecord.getCertificationTitle());
            betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_blush_pink));
            betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description_for_normal, betaTest.getTitle()));
            betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));

        } else if (awardTypeCode >= FomesConstants.BetaTest.Award.TYPE_CODE_ETC) {
            betaTestAwardsTitle.setText(awardRecord.getCertificationTitle());
            betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_warm_gray));
            betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description, betaTest.getTitle()));
            betaTestCertificateFomesFeedback.setText(getString(R.string.betatest_certificate_fomes_feedback));

        } else {
            betaTestAwardsTitle.setText("테스터");
            betaTestAwardsTitle.setTextColor(getResources().getColor(R.color.fomes_warm_gray));
            betaTestCertificateDescription.setText(getString(R.string.betatest_certificate_description, betaTest.getTitle()));
            betaTestCertificateFomesFeedback.setText(
                    (betaTest.getEpilogue() != null) ?
                            getString(R.string.betatest_certificate_fomes_feedback_for_better_try) :
                            getString(R.string.betatest_certificate_fomes_feedback));
        }
    }

    @Override
    public void showLoading() {
        if (isUnavailableViewControl()) {
            return;
        }

        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (isUnavailableViewControl()) {
            return;
        }

        loadingProgressBar.setVisibility(View.GONE);
    }

    private void showCertificateView() {
        if (isUnavailableViewControl()) {
            return;
        }

        betaTestCertificateLayout.setVisibility(View.VISIBLE);
        betaTestCertificateErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void showErrorView() {
        if (isUnavailableViewControl()) {
            return;
        }

        betaTestCertificateErrorLayout.setVisibility(View.VISIBLE);
        betaTestCertificateLayout.setVisibility(View.GONE);
    }

    @Override
    public CompositeSubscription getCompositeSubscription() {
        return null;
    }

    @Override
    public void setPresenter(BetaTestCertificateContract.Presenter presenter) {

    }
}
