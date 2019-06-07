package com.formakers.fomes.main.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecommendListAdapterContract.View, RecommendListAdapterContract.Model {

    private static final String TAG = "RecommendListAdapter";

    private Context context;
    private final List<RecommendApp> recommendApps = new ArrayList<>();

    private RecommendContract.Presenter presenter;

    public RecommendListAdapter() {
    }

    /**
     * View
     */

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
        viewHolder.recommendAppItemView.setLabelText(Joiner.on(" ").join(recommendApp.getCriteria()));

        viewHolder.itemView.setOnClickListener(v -> this.presenter.emitShowDetailEvent(recommendApp));

        viewHolder.recommendAppItemView.setOnWishListCheckedChangeListener((v, isChecked) -> {
            String packageName = recommendApp.getAppInfo().getPackageName();

            Completable requestUpdateWishList = isChecked ? this.presenter.requestSaveToWishList(packageName) : this.presenter.requestRemoveFromWishList(packageName);

            requestUpdateWishList.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> updateWishedStatus(packageName, isChecked)
                            , e -> Toast.makeText(context, (isChecked ? R.string.wish_list_add_fail : R.string.wish_list_remove_fail), Toast.LENGTH_LONG).show());
        });
    }

    @Override
    public void notifyItemChanged(String pacakgeName) {
        this.notifyItemChanged(getPosition(getItem(pacakgeName)));
    }

    /**
     * Model
     */

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
    public boolean contains(String packageName) {
        for (RecommendApp app : recommendApps) {
            if (app.getAppInfo().getPackageName().equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public RecommendApp getItem(int position) {
        return recommendApps.get(position);
    }

    @Override
    public List<RecommendApp> getAllItems() {
        return recommendApps;
    }

    public int getPosition(RecommendApp app) {
        return recommendApps.indexOf(app);
    }

    @Override
    public void updateWishedStatus(String packgeName, boolean wishedByMe) {
        getItem(packgeName).getAppInfo().setWished(wishedByMe);
    }

    private RecommendApp getItem(String packageName) {
        for (RecommendApp app : recommendApps) {
            if (packageName.equals(app.getAppInfo().getPackageName())) {
                return app;
            }
        }

        throw new IllegalArgumentException("There isn't any item with the packageName=" + packageName);
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        RecommendAppItemView recommendAppItemView;

        public AppViewHolder(View itemView) {
            super(itemView);
            recommendAppItemView = itemView.findViewById(R.id.recommend_app_item_view);
        }
    }
}