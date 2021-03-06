package com.formakers.fomes.common.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.view.custom.adapter.NetworkImagePagerAdapter;
import com.formakers.fomes.common.model.AppInfo;

public class RecommendAppItemView extends ConstraintLayout {

    public static final String TAG = RecommendAppItemView.class.getSimpleName();

    private View installedLabelView;
    private ImageView iconImageView;
    private TextView nameTextView;
    private TextView categoryDeveloperTextView;
    private TextView labelTextView;
    private Group verboseGroup;
    private TextView reviewScoreTextView;
    private TextView downloadCountTextView;
    private TextView ageLimitTextView;
    private ViewPager imageViewPager;
    private ToggleButton wishListToggle;

    private int recommendType;
    private String recommendReason;
    private int baseAppIconSize;

    public RecommendAppItemView(Context context) {
        super(context);
    }

    public RecommendAppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        setTypeArray(context.obtainStyledAttributes(attrs, R.styleable.RecommendAppItemView));
    }

    public RecommendAppItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setTypeArray(context.obtainStyledAttributes(attrs, R.styleable.RecommendAppItemView, defStyleAttr, 0));
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_app_info, this, false);
        addView(view);

        installedLabelView = findViewById(R.id.item_app_installed_label);
        iconImageView = findViewById(R.id.item_app_icon_imageview);
        nameTextView = findViewById(R.id.item_app_name_textview);
        categoryDeveloperTextView = findViewById(R.id.item_app_genre_developer_textview);
        labelTextView = findViewById(R.id.item_app_label_textview);
        verboseGroup = findViewById(R.id.verbose_group);
        reviewScoreTextView = findViewById(R.id.item_app_review_score);
        downloadCountTextView = findViewById(R.id.item_app_download_count);
        ageLimitTextView = findViewById(R.id.item_app_age_limit);
        imageViewPager = findViewById(R.id.item_app_image_viewpager);
        wishListToggle = findViewById(R.id.app_info_wishlist_button);

        baseAppIconSize = iconImageView.getLayoutParams().width;
    }

    private void setTypeArray(TypedArray typedArray) {
        setIconImageDrawable(typedArray.getDrawable(R.styleable.RecommendAppItemView_app_iconDrawable));

        setNameText(typedArray.getString(R.styleable.RecommendAppItemView_app_nameText));

        setCategoryDeveloperText(
                typedArray.getString(R.styleable.RecommendAppItemView_app_categoryText),
                typedArray.getString(R.styleable.RecommendAppItemView_app_developerText));

        setRecommendType(typedArray.getInteger(R.styleable.RecommendAppItemView_app_recommendType, RecommendApp.RECOMMEND_TYPE_FAVORITE_APP));

        setVerbose(typedArray.getBoolean(R.styleable.RecommendAppItemView_app_verbose, false));

        typedArray.recycle();
    }

    public void bindAppInfo(AppInfo appInfo) {
        Glide.with(getContext()).load(appInfo.getIconUrl())
                .apply(new RequestOptions().override(70, 70)
                        .centerCrop()
                        .transform(new RoundedCorners(10)))
                .into(iconImageView);

        setNameText(appInfo.getAppName());
        setCategoryDeveloperText(appInfo.getCategoryName(), appInfo.getDeveloper());

        setVerboseGroup(appInfo.getStar(), appInfo.getInstallsMin(), appInfo.getContentsRating());

        if (appInfo.getImageUrls() != null) {
            imageViewPager.setAdapter(new NetworkImagePagerAdapter(appInfo.getImageUrls()));
        }

        setWishListChecked(appInfo.isWished());

        setVisibilityInstalledTextView(appInfo.isInstalled() ? View.VISIBLE : View.GONE);
    }

    private void setVerboseGroup(Double star, Long installsMin, String contentsRating) {
        if (isVerbose()) {
            reviewScoreTextView.setText(String.format(getContext().getString(R.string.format_app_info_star_score),
                    (float) Math.round(star * 10) / 10));
            downloadCountTextView.setText(String.format(getContext().getString(R.string.format_app_info_download_count),
                    installsMin));
            ageLimitTextView.setText(contentsRating);
        }
    }

    public void setIconImageDrawable(Drawable iconDrawable) {
        iconImageView.setImageDrawable(iconDrawable);
    }

    public void setNameText(String appName) {
        nameTextView.setText(appName);
    }

    public void setCategoryDeveloperText(String category, String developer) {
        categoryDeveloperTextView.setText(String.format("%s / %s", TextUtils.isEmpty(category) ? "" : category, TextUtils.isEmpty(developer) ? "" : developer));
    }

    public void setLabelText(String recommendReason) {
        this.recommendReason = recommendReason;

        refreshLabelTextView();
    }

    public void setRecommendType(int recommendType) {
        this.recommendType = recommendType;

        refreshLabelTextView();
    }

    public void setVerbose(boolean isVerbose) {
        verboseGroup.setVisibility(isVerbose ? View.VISIBLE : View.GONE);
    }

    public boolean isVerbose() {
        return verboseGroup.getVisibility() == View.VISIBLE;
    }

    public void setVisibilityInstalledTextView(int visibility) {
        installedLabelView.setVisibility(visibility);
    }

    private void resizeIconImageView(int size) {
        iconImageView.getLayoutParams().height = size;
        iconImageView.getLayoutParams().width = size;
    }

    public void setWishListChecked(boolean wishedByMe) {
        wishListToggle.setChecked(wishedByMe);
    }

    public void setOnWishListCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        wishListToggle.setOnCheckedChangeListener(listener);
    }

    // ???????????? map?????? ????????????...
    private void refreshLabelTextView() {
        int styleResId;
        int colorResId;

        Resources res = getContext().getResources();
        String format;

        switch (recommendType) {
            case RecommendApp.RECOMMEND_TYPE_FAVORITE_APP:
                styleResId = R.style.FomesTheme_TurquoiseItem;
                colorResId = R.color.colorPrimary;
                format = res.getString(R.string.recommend_label_format_favorite_game);
                break;
            case RecommendApp.RECOMMEND_TYPE_FAVORITE_DEVELOPER:
                styleResId = R.style.FomesTheme_BlushPinkItem;
                colorResId = R.color.fomes_blush_pink;
                format = res.getString(R.string.recommend_label_format_favorite_developer);
                break;
            case RecommendApp.RECOMMEND_TYPE_FAVORITE_CATEGORY:
                styleResId = R.style.FomesTheme_WarmGrayItem;
                colorResId = R.color.fomes_light_gray;
                format = res.getString(R.string.recommend_label_format_favorite_category);
                break;
            case RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC:
                styleResId = R.style.FomesTheme_SquashItem;
                colorResId = R.color.fomes_squash;
                format = res.getString(R.string.recommend_label_format_similar_demographic);
                break;
            default:
                styleResId = R.style.FomesTheme_TurquoiseItem;
                colorResId = R.color.colorPrimary;
                format = res.getString(R.string.recommend_label_format_favorite_game);
                break;
        }

        labelTextView.setBackground(res.getDrawable(R.drawable.item_app_label_background,
                new ContextThemeWrapper(getContext(), styleResId).getTheme()));
        labelTextView.setTextColor(res.getColor(colorResId));
        labelTextView.setText(String.format(format, getShortenRecommendReason()));
    }

    private String getShortenRecommendReason() {
        switch (recommendType) {
            case RecommendApp.RECOMMEND_TYPE_FAVORITE_APP:
            case RecommendApp.RECOMMEND_TYPE_FAVORITE_DEVELOPER:
            case RecommendApp.RECOMMEND_TYPE_FAVORITE_CATEGORY:
                if (recommendReason != null && recommendReason.length() > 10) {
                    return recommendReason.substring(0, 10) + getContext().getResources().getString(R.string.shorten_symbol);
                }

                break;
        }

        return recommendReason;
    }
}