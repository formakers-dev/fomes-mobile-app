package com.formakers.fomes.point.history;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;

import java.text.NumberFormat;

import javax.inject.Inject;

import butterknife.BindView;

public class PointHistoryActivity extends FomesBaseActivity
        implements PointHistoryContract.View {

    @BindView(R.id.available_point) TextView availablePointTextView;
    @BindView(R.id.total_point) TextView totalPointTextView;
    @BindView(R.id.point_history_recyclerview) RecyclerView historyRecyclerView;
    @BindView(R.id.point_history_recyclerview_placeholder) ShimmerFrameLayout loadingLayout;
    @BindView(R.id.point_history_empty) View pointHistoryEmptyView;


    @Inject PointHistoryContract.Presenter presenter;

    PointHistoryListAdapterContract.View adapterView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_point_history);

        getSupportActionBar().setTitle("나의 포인트 내역");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerPointHistoryDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new PointHistoryDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // set View
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        this.historyRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(this, R.style.FomesMainTabTheme_TransparentDivider).getTheme()));
        this.historyRecyclerView.addItemDecoration(dividerItemDecoration);

        PointHistoryListAdapter pointHistoryListAdapter = new PointHistoryListAdapter(this.presenter);
        adapterView = pointHistoryListAdapter;
        this.presenter.setAdapterModel(pointHistoryListAdapter);

        this.historyRecyclerView.setAdapter(pointHistoryListAdapter);

        // bind View
        this.presenter.bindAvailablePoint();
        this.presenter.bindTotalPoint();
        this.presenter.bindHistory();
    }

    @Override
    public void setPresenter(PointHistoryContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setAvailablePoint(long point) {
        availablePointTextView.setText(String.format("%s P", NumberFormat.getInstance().format(point)));
        availablePointTextView.startAnimation(getFadeInAnimation(300));
    }

    @Override
    public void setTotalPoint(long point) {
        totalPointTextView.setText(String.format("%s P", NumberFormat.getInstance().format(point)));
        totalPointTextView.startAnimation(getFadeInAnimation(300));
    }

    @Override
    public void showLoading() {
        loadingLayout.startShimmer();
    }

    @Override
    public void hideLoading() {
        loadingLayout.stopShimmer();
        loadingLayout.setVisibility(View.GONE);
        historyRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        pointHistoryEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshHistory() {
        this.adapterView.refresh();
    }
}
