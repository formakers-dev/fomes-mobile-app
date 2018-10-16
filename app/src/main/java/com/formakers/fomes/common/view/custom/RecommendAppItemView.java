package com.formakers.fomes.common.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.model.AppInfo;

public class RecommendAppItemView extends ConstraintLayout {

    public static final int RECOMMEND_TYPE_FAVORITE_GAME = 1;
    public static final int RECOMMEND_TYPE_FAVORITE_DEVELOPER = 2;
    public static final int RECOMMEND_TYPE_FAVORITE_CATEGORY = 3;
    public static final int RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC = 4;

    private ImageView iconImageView;
    private TextView nameTextView;
    private TextView categoryDeveloperTextView;
    private TextView labelTextView;

    private int recommendType;
    private String recommendReason;
    private int rank;

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

        iconImageView = findViewById(R.id.item_app_icon_imageview);
        nameTextView = findViewById(R.id.item_app_name_textview);
        categoryDeveloperTextView = findViewById(R.id.item_app_genre_developer_textview);
        labelTextView = findViewById(R.id.item_app_label_textview);
    }

    private void setTypeArray(TypedArray typedArray) {
        setIconImageDrawable(typedArray.getDrawable(R.styleable.RecommendAppItemView_app_iconDrawable));

        setNameText(typedArray.getString(R.styleable.RecommendAppItemView_app_nameText));

        setCategoryDeveloperText(
                typedArray.getString(R.styleable.RecommendAppItemView_app_categoryText),
                typedArray.getString(R.styleable.RecommendAppItemView_app_developerText));

        setRecommendType(typedArray.getInteger(R.styleable.RecommendAppItemView_app_recommendType, RECOMMEND_TYPE_FAVORITE_GAME));

        boolean isVerbose = typedArray.getBoolean(R.styleable.RecommendAppItemView_app_verbose, false);
        if (isVerbose) {
            // detail visible
        }

        typedArray.recycle();
    }

    public void bindAppInfo(AppInfo appInfo) {
        Glide.with(getContext()).load(appInfo.getIconUrl())
                .apply(new RequestOptions().override(70, 70).centerCrop())
                .into(iconImageView);

        setNameText(appInfo.getAppName());
        setCategoryDeveloperText(appInfo.getCategoryName1(), appInfo.getDeveloper());
    }

    public ImageView getIconImageView() {
        return iconImageView;
    }

    public void setIconImageDrawable(Drawable iconDrawable) {
        iconImageView.setImageDrawable(iconDrawable);
    }

    public void setNameText(String appName) {
        nameTextView.setText(appName);
    }

    public void setCategoryDeveloperText(String category, String developer) {
        categoryDeveloperTextView.setText(String.format("%s / %s", category, developer));
    }

    public void setLabelText(String recommendReason, int rank) {
        this.recommendReason = recommendReason;
        this.rank = rank;

        refreshLabelTextView();
    }

    public void setRecommendType(int recommendType) {
        this.recommendType = recommendType;

        refreshLabelTextView();
    }

    // 고민되네 map으로 처리할까...
    private void refreshLabelTextView() {
        int styleResId;
        int colorResId;

        Resources res = getContext().getResources();
        String format;

        switch (recommendType) {
            case RECOMMEND_TYPE_FAVORITE_GAME:
                styleResId = R.style.FomesTheme_TurquoiseItem;
                colorResId = R.color.colorPrimary;
                format = res.getString(R.string.recommend_label_format_favorite_game);
                break;
            case RECOMMEND_TYPE_FAVORITE_DEVELOPER:
                styleResId = R.style.FomesTheme_BlushPinkItem;
                colorResId = R.color.fomes_blush_pink;
                format = res.getString(R.string.recommend_label_format_favorite_developer);
                break;
            case RECOMMEND_TYPE_FAVORITE_CATEGORY:
                styleResId = R.style.FomesTheme_WarmGrayItem;
                colorResId = R.color.fomes_warm_gray;
                format = res.getString(R.string.recommend_label_format_favorite_category);
                break;
            case RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC:
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
        labelTextView.setText(String.format(format, recommendReason, rank));
    }
}