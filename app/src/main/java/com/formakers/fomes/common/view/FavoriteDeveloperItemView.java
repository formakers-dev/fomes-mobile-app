package com.formakers.fomes.common.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.formakers.fomes.R;

public class FavoriteDeveloperItemView extends ConstraintLayout {
    public FavoriteDeveloperItemView(Context context) {
        super(context);
    }

    public FavoriteDeveloperItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(R.layout.item_analysis_developer);
    }

    public FavoriteDeveloperItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(R.layout.item_analysis_developer);
    }

    private void initView(@LayoutRes int layoutResId) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResId, this, false);
        addView(view);
    }
}
