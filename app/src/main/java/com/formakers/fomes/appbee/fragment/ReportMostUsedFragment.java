package com.formakers.fomes.appbee.fragment;

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
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.common.network.AppStatService;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;


public class ReportMostUsedFragment extends BaseFragment {

    public static final String TAG = "ReportMostUsedFragment";

    @Inject AppStatService appStatService;
    @Inject ImageLoader imageLoader;

    @BindView(R.id.app_recycler_view)       RecyclerView mostUsedAppRecyclerView;
    @BindView(R.id.category_recycler_view)  RecyclerView categoryRecyclerView;

    private Context context;
    private List<AppUsage> appUsageInfos = new ArrayList<>();
    private UsedAppAdapter mostUsedAppAdapter;

    private List<CategoryUsage> categoryUsages = new ArrayList<>();
    private CategoryUsageAdapter categoryUsageAdapter;

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

        appStatService.requestAppUsageByCategory("COMMUNICATION")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appUsages -> {
                    Log.d(TAG, appUsages.toString());
                    appUsageInfos.clear();
                    appUsageInfos.addAll(appUsages);
                    mostUsedAppAdapter.notifyDataSetChanged();
                });

        //////////////////////////////////////////////////////////////////////////////////

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        categoryUsageAdapter = new CategoryUsageAdapter(categoryUsages);
        categoryRecyclerView.setAdapter(categoryUsageAdapter);

        appStatService.requestCategoryUsage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    categoryUsages.clear();
                    categoryUsages.addAll(result);
                    categoryUsageAdapter.notifyDataSetChanged();
                });
    }

    class CategoryUsageAdapter extends RecyclerView.Adapter<CategoryUsageAdapter.CategoryItemImageHolder> {

        private List<CategoryUsage> categoryUsages;

        public CategoryUsageAdapter(List<CategoryUsage> categoryUsages) {
            this.categoryUsages = categoryUsages;
        }

        @Override
        public CategoryItemImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_app, parent, false);
            return new CategoryItemImageHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CategoryItemImageHolder holder, int position) {
            CategoryUsage categoryUsage = categoryUsages.get(position);

            TextView categoryNameTextView = holder.categoryNameTextView;
            TextView totalUsedTimeTextView = holder.totalUsedTimeTextView;

            categoryNameTextView.setText(categoryUsage.getCategoryName());
            totalUsedTimeTextView.setText(String.valueOf(categoryUsage.getTotalUsedTime()));
        }

        @Override
        public int getItemCount() {
            return categoryUsages.size();
        }

        class CategoryItemImageHolder extends RecyclerView.ViewHolder {
            TextView categoryNameTextView;
            TextView totalUsedTimeTextView;

            CategoryItemImageHolder(View itemView) {
                super(itemView);
                this.categoryNameTextView = ((TextView) itemView.findViewById(R.id.report_app_name_textview));
                this.totalUsedTimeTextView = ((TextView) itemView.findViewById(R.id.report_totalusedtime_textview));
            }
        }
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
