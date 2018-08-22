package com.formakers.fomes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.network.AppStatService;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yenar on 2018-08-22.
 */

public class ReportMostUsedFragment extends BaseFragment {

    public static final String TAG = "ReportMostUsedFragment";

    @Inject AppStatService appStatService;
    @Inject ImageLoader imageLoader;

    @BindView(R.id.app_recycler_view) RecyclerView mostUsedAppRecyclerView;

    private Context context;
    private List<AppUsage> appUsageInfos = new ArrayList<>();
    private UsedAppAdapter mostUsedAppAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppBeeApplication) getActivity().getApplication()).getComponent().inject(this);
        return inflater.inflate(R.layout.fragment_report_app_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mostUsedAppRecyclerView.setLayoutManager(horizontalLayoutManager);
        mostUsedAppAdapter = new UsedAppAdapter(appUsageInfos, imageLoader);
        mostUsedAppRecyclerView.setAdapter(mostUsedAppAdapter);

        appStatService.requestAppUsageByCategory("GAME")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appUsages -> {
                    Log.d(TAG, appUsages.toString());
                    appUsageInfos.clear();
                    appUsageInfos.addAll(appUsages);
                    mostUsedAppAdapter.notifyDataSetChanged();
                });
    }

    class UsedAppAdapter extends RecyclerView.Adapter<UsedAppAdapter.AppItemImageHolder> {

        private List<AppUsage> appUsageInfos;
        private ImageLoader imageLoader;

        public UsedAppAdapter(List<AppUsage> appUsageInfos, ImageLoader imageLoader) {
            this.appUsageInfos = appUsageInfos;
            this.imageLoader = imageLoader;
        }

        @Override
        public AppItemImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_app, parent, false);
            return new AppItemImageHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AppItemImageHolder holder, int position) {
            AppUsage appUsageInfo = appUsageInfos.get(position);

            ImageView iconImageView = holder.iconImageView;
            TextView appNameTextView = holder.appNameTextView;
            TextView categoryTextView = holder.categoryTextView;
            TextView developerTextView = holder.developerTextView;
            TextView totalUsedTimeTextView = holder.totalUsedTimeTextView;

            imageLoader.loadImage(iconImageView, appUsageInfo.getAppInfo().getIconUrl(), new RequestOptions().override(1300, 1000));

            appNameTextView.setText(appUsageInfo.getAppInfo().getAppName());
            categoryTextView.setText(appUsageInfo.getAppInfo().getCategoryName1());
            developerTextView.setText(appUsageInfo.getAppInfo().getDeveloper());
            totalUsedTimeTextView.setText(String.valueOf(appUsageInfo.getTotalUsedTime()));
        }

        @Override
        public int getItemCount() {
            return appUsageInfos.size();
        }

        class AppItemImageHolder extends RecyclerView.ViewHolder {
            ImageView iconImageView;
            TextView appNameTextView;
            TextView categoryTextView;
            TextView developerTextView;
            TextView totalUsedTimeTextView;

            AppItemImageHolder(View itemView) {
                super(itemView);
                this.iconImageView = ((ImageView) itemView.findViewById(R.id.report_app_icon_imageview));
                this.appNameTextView = ((TextView) itemView.findViewById(R.id.report_app_name_textview));
                this.categoryTextView = ((TextView) itemView.findViewById(R.id.report_category_textview));
                this.developerTextView = ((TextView) itemView.findViewById(R.id.report_developer_textview));
                this.totalUsedTimeTextView = ((TextView) itemView.findViewById(R.id.report_totalusedtime_textview));
            }
        }
    }
}
