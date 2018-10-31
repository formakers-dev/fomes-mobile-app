package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecommendListAdapterContract.View, RecommendListAdapterContract.Model {

    private static final String TAG = "RecommendListAdapter";

    private Context context;
    private final List<RecommendApp> recommendApps = new ArrayList<>();

    private RecommendContract.Presenter presenter;

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

        viewHolder.recommendAppItemView.setOnWishListToggleButtonListener(v -> {
            if (!((ToggleButton) v).isChecked()) {
                this.presenter.emitRemoveFromWishList(recommendApp.getAppInfo().getPackageName())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> updateWishedByMe(position, false),
                                e -> Toast.makeText(this.context, "위시리스트 삭제에 실패하였습니다.", Toast.LENGTH_LONG).show());
            } else {
                this.presenter.emitSaveToWishList(recommendApp.getAppInfo().getPackageName())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> updateWishedByMe(position, true),
                                e -> Toast.makeText(this.context, "위시리스트 등록에 실패하였습니다.", Toast.LENGTH_LONG).show());
            }
        });
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
        return recommendApps.get(position);
    }

    public void updateWishedByMe(String packageName, boolean wishedByMe) {
        for(int position=0; position<getItemCount(); position++) {
            if (packageName.equals(recommendApps.get(position).getAppInfo().getPackageName())) {
                updateWishedByMe(position, wishedByMe);
                break;
            }
        }
    }

    private void updateWishedByMe(int position, boolean wishedByMe) {
        getItem(position).getAppInfo().setWishedByMe(wishedByMe);
        notifyItemChanged(position);
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        RecommendAppItemView recommendAppItemView;

        public AppViewHolder(View itemView) {
            super(itemView);
            recommendAppItemView = itemView.findViewById(R.id.recommend_app_item_view);
        }
    }
}