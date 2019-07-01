package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.formakers.fomes.main.dagger.BetaTestDetailActivityModule;
import com.formakers.fomes.main.dagger.DaggerBetaTestDetailActivityComponent;

import javax.inject.Inject;

import butterknife.BindView;

public class BetaTestDetailActivity extends FomesBaseActivity implements BetaTestDetailContract.View {

    private static final String TAG = "BetaTestDetailActivity";

    @BindView(R.id.betatest_detail_title_textview) TextView titleTextView;
    @BindView(R.id.loading) ProgressBar loadingProgressBar;

    @Inject BetaTestDetailContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG,"onCreate");

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

        String id = bundle.getString(FomesConstants.BetaTest.EXTRA_GROUP_ID);
        titleTextView.setText(id);

        this.presenter.load(id);
    }


    @Override
    protected void onDestroy() {
        this.presenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void setPresenter(BetaTestDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void bind(BetaTest betaTest) {
        // 여기서 뷰를 셋팅하면 된다
    }

    @Override
    public void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }
}
