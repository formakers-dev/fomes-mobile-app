package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.formakers.fomes.R;
import com.formakers.fomes.fragment.BaseFragment;
import com.formakers.fomes.main.adapter.RecommendListAdapter;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.presenter.RecommendPresenter;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.view.decorator.ContentDividerItemDecoration;

import butterknife.BindView;

public class RecommendFragment extends BaseFragment implements RecommendContract.View {

    @BindView(R.id.recommend_recyclerview) RecyclerView recommendRecyclerView;

    RecommendListAdapter recommendListAdapter;

    RecommendContract.Presenter presenter;

    @Override
    public void setPresenter(RecommendContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.setPresenter(new RecommendPresenter(this));
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommendRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(getContext(), ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(getContext(), R.style.FomesMainTabTheme_RecommendDivider).getTheme()));
        recommendRecyclerView.addItemDecoration(dividerItemDecoration);

        recommendListAdapter = new RecommendListAdapter();
        recommendListAdapter.setPresenter(presenter);
        recommendRecyclerView.setAdapter(recommendListAdapter);
        presenter.setAdapterModel(recommendListAdapter);
    }

    @Override
    public void onShowDetailEvent(AppInfo appInfo) {
        Toast.makeText(getContext(), appInfo.getPackageName(), Toast.LENGTH_LONG).show();
    }
}
