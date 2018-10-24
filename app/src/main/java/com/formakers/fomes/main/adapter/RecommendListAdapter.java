package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements RecommendListAdapterContract.View, RecommendListAdapterContract.Model {

    private static final String TAG = "RecommendListAdapter";

    private Context context;
    private final List<RecommendApp> recommendApps = new ArrayList<>();

    private RecommendContract.Presenter presenter;
//    private RecommendContract.View view;

    public RecommendListAdapter() {
    }

    @Override
    public void setPresenter(RecommendContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_app, parent, false);
        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecommendApp recommendApp = recommendApps.get(position);

        AppViewHolder viewHolder = (AppViewHolder) holder;
        viewHolder.recommendAppItemView.bindAppInfo(recommendApp.getAppInfo());
        viewHolder.recommendAppItemView.setRecommendType(recommendApp.getRecommendType());
        viewHolder.recommendAppItemView.setLabelText(Joiner.on(" ").join(recommendApp.getCriteria()), position + 1);

        viewHolder.itemView.setOnClickListener(v -> this.presenter.emitShowDetailEvent(recommendApp, position + 1));
    }

    @Override
    public int getItemCount() {
        return this.recommendApps.size();
    }

    @Override
    public void add(RecommendApp item) {
        recommendApps.add(item);
    }

    @Override
    public void addAll(List<RecommendApp> items) {
        recommendApps.addAll(items);
    }

    @Override
    public void clear() {
        recommendApps.clear();
    }

    @Override
    public RecommendApp getItem(int position) {
        if (position == 0) {
            throw new IllegalArgumentException("this is a header!");
        }

        return recommendApps.get(position);
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        RecommendAppItemView recommendAppItemView;

        public AppViewHolder(View itemView) {
            super(itemView);
            recommendAppItemView = itemView.findViewById(R.id.recommend_app_item_view);
        }
    }
}