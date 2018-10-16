package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.formakers.fomes.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements RecommendListAdapterContract.View, RecommendListAdapterContract.Model {

    private Context context;
    private final List<AppInfo> appList = new ArrayList<>();

    private RecommendContract.Presenter presenter;
//    private RecommendContract.View view;

    public RecommendListAdapter() {
        // start of temporary code
        AppInfo testAppInfo = new AppInfo("com.formakers.fomes", "포메스", "카테고리ID", "카테고리", "카테고리ID2", "카테고리2", "https://lh3.googleusercontent.com/AHQJwkSC1J602KgQq0d3oMB-waafBrbaw9wAS80HGXQSSaEem4-zMowrGpbHIUuyyw=s360-rw");
        testAppInfo.setDeveloper("포메스");
        appList.add(testAppInfo);
        AppInfo testAppInfo2 = new AppInfo("com.appbeemobile.appbee", "앱비", "카테고리ID", "카테고리", "카테고리ID2", "카테고리2", "https://lh3.googleusercontent.com/AHQJwkSC1J602KgQq0d3oMB-waafBrbaw9wAS80HGXQSSaEem4-zMowrGpbHIUuyyw=s360-rw");
        testAppInfo2.setDeveloper("앱비");
        appList.add(testAppInfo2);
        // end of temporary code
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
        final AppInfo appInfo = appList.get(position);

        AppViewHolder viewHolder = (AppViewHolder) holder;

        // TODO : Glide Dagger로 빼기
        Glide.with(context).load(appInfo.getIconUrl())
                .apply(new RequestOptions().override(70, 70).centerCrop())
                .into(viewHolder.iconImageView);
        viewHolder.appNameTextView.setText(appInfo.getAppName());
        viewHolder.genreDeveloperTextView.setText(String.format("%s / %s", appInfo.getCategoryName1(), appInfo.getDeveloper()));
        viewHolder.itemView.setOnClickListener(v -> this.presenter.emitShowDetailEvent(appInfo));

        // temp
        viewHolder.labelTextView.setBackground(context.getResources().getDrawable(R.drawable.item_app_label_background, new ContextThemeWrapper(context, R.style.FomesTheme_TurquoiseItem).getTheme()));
        viewHolder.labelTextView.setText("배틀그라운드 게이머들이 많이 하는 게임 1위");
    }

    @Override
    public int getItemCount() {
        return this.appList.size();
    }

    @Override
    public void add(AppInfo appInfo) {
        appList.add(appInfo);
    }

    @Override
    public void addAll(List<AppInfo> appInfos) {
        appList.addAll(appInfos);
    }

    @Override
    public void clear() {
        appList.clear();
    }

    @Override
    public AppInfo getItem(int position) {
        if (position == 0) {
            throw new IllegalArgumentException("this is a header!");
        }

        return appList.get(position);
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView appNameTextView;
        TextView genreDeveloperTextView;
        TextView labelTextView;

        public AppViewHolder(View itemView) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.item_app_icon_imageview);
            appNameTextView = itemView.findViewById(R.id.item_app_name_textview);
            genreDeveloperTextView = itemView.findViewById(R.id.item_app_genre_developer_textview);
            labelTextView = itemView.findViewById(R.id.item_app_label_textview);
        }
    }
}