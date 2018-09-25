package com.formakers.fomes.common.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.R;

public class RankItemView extends ConstraintLayout {
    View lineView;
    View arrowView;
    TextView titleTextView;
    TextView descTextView;

    public RankItemView(Context context) {
        super(context);
    }

    public RankItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RankItemView);
        if (typedArray.getBoolean(R.styleable.RankItemView_rank_item_content_isTop, false)) {
            initView(R.layout.item_analysis_rank_reverse);
        } else {
            initView(R.layout.item_analysis_rank);
        }
        setTypeArray(typedArray);
    }

    public RankItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RankItemView, defStyleAttr, 0);
        if (typedArray.getBoolean(R.styleable.RankItemView_rank_item_content_isTop, false)) {
            initView(R.layout.item_analysis_rank_reverse);
        } else {
            initView(R.layout.item_analysis_rank);
        }
        setTypeArray(typedArray);
    }

    private void initView(@LayoutRes int layoutResId) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResId, this, false);
        addView(view);

        lineView = findViewById(R.id.line_view);
        arrowView = findViewById(R.id.arrow_view);
        titleTextView = findViewById(R.id.title_textview);
        descTextView = findViewById(R.id.desc_textview);
    }

    private void setTypeArray(TypedArray typedArray) {
        Resources res = getResources();
        titleTextView.setText(typedArray.getString(R.styleable.RankItemView_rank_item_title));
        descTextView.setText(typedArray.getString(R.styleable.RankItemView_rank_item_description));

        int color = typedArray.getColor(R.styleable.RankItemView_rank_item_color, res.getColor(R.color.colorPrimary));
        arrowView.getBackground().setTint(color);
        lineView.setBackgroundColor(color);
        titleTextView.setTextColor(color);

        typedArray.recycle();
    }

    public void setRankColor(@ColorRes int colorResId) {
        int color = getResources().getColor(colorResId);
        arrowView.getBackground().setTint(getResources().getColor(colorResId));
        lineView.setBackgroundColor(color);
        titleTextView.setTextColor(color);
    }

    public String getTitleText() {
        return String.valueOf(titleTextView.getText());
    }

    public void setTitleText(String text) {
        titleTextView.setText(text);
    }

    public void setTitleText(@StringRes int stringResId) {
        titleTextView.setText(stringResId);
    }

    public String getDescriptionText() {
        return String.valueOf(descTextView.getText());
    }

    public void setDescriptionText(String text) {
        descTextView.setText(text);
    }

    public void setDescriptionText(@StringRes int stringResId) {
        descTextView.setText(stringResId);
    }
}
