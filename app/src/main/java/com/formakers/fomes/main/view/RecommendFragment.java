package com.formakers.fomes.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;
import com.formakers.fomes.main.adapter.RecommendListAdapter;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.dagger.DaggerRecommendFragmentComponent;
import com.formakers.fomes.main.dagger.RecommendFragmentModule;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class RecommendFragment extends BaseFragment implements RecommendContract.View {

    @BindView(R.id.recommend_recyclerview) RecyclerView recommendRecyclerView;

    RecommendListAdapter recommendListAdapter;

    @Inject RecommendContract.Presenter presenter;

    @Override
    public void setPresenter(RecommendContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DaggerRecommendFragmentComponent.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .recommendFragmentModule(new RecommendFragmentModule(this))
                .build()
                .inject(this);

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
    public void onResume() {
        super.onResume();

        recommendListAdapter.clear();
        recommendListAdapter.notifyDataSetChanged();

        addCompositeSubscription(
                presenter.loadRecommendApps("GAME")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(recommendApps -> {
                            final List<String> packageNames = new ArrayList<>();

                            for (int i = 0; i < recommendApps.size(); ) {
                                final String packageName = recommendApps.get(i).getAppInfo().getPackageName();

                                if (packageNames.contains(packageName)) {
                                    recommendApps.remove(i);
                                } else {
                                    packageNames.add(packageName);
                                    i++;
                                }
                            }

                            recommendListAdapter.addAll(recommendApps);
                            recommendListAdapter.notifyDataSetChanged();
                        }, e -> Log.d("FOMES", e.toString()))
        );
    }

    @Override
    public void onShowDetailEvent(RecommendApp recommendApp) {
        AppInfoDetailDialogFragment appInfoDetailDialogFragment = new AppInfoDetailDialogFragment();

        appInfoDetailDialogFragment.setPresenter(this.presenter);

        Bundle bundle = new Bundle();
        bundle.putParcelable(FomesConstants.EXTRA.APPINFO, recommendApp.getAppInfo());
        bundle.putInt(FomesConstants.EXTRA.RECOMMEND_TYPE, recommendApp.getRecommendType());
        bundle.putStringArrayList(FomesConstants.EXTRA.RECOMMEND_CRITERIA, Lists.newArrayList(recommendApp.getCriteria()));
        appInfoDetailDialogFragment.setArguments(bundle);

        appInfoDetailDialogFragment.show(getChildFragmentManager(), AppInfoDetailDialogFragment.TAG);
    }

    @Override
    public void refreshWishedByMe(String packageName, boolean wishedByMe) {
        recommendListAdapter.updateWishedByMe(packageName, wishedByMe);
    }
}
