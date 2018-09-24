package com.formakers.fomes.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakers.fomes.R;

public class RankAppItemView extends ConstraintLayout {
    TextView numberTextView;
    ImageView iconImageView;
    TextView titleTextView;
    TextView descTextView;

    public RankAppItemView(Context context) {
        super(context);
    }

    public RankAppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        setTypeArray(context.obtainStyledAttributes(attrs, R.styleable.RankAppItemView));
    }

    public RankAppItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setTypeArray(context.obtainStyledAttributes(attrs, R.styleable.RankAppItemView, defStyleAttr, 0));
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_rank_app, this, false);
        addView(view);

        numberTextView = findViewById(R.id.number_textview);
        iconImageView = findViewById(R.id.icon_imageview);
        titleTextView = findViewById(R.id.title_textview);
        descTextView = findViewById(R.id.desc_textview);
    }

    private void setTypeArray(TypedArray typedArray) {
        numberTextView.setText(typedArray.getString(R.styleable.RankAppItemView_rank_number));
        numberTextView.getBackground().setTint(typedArray.getColor(R.styleable.RankAppItemView_rank_numberBackgroundTint, getResources().getColor(R.color.colorPrimary)));
        iconImageView.setImageDrawable(typedArray.getDrawable(R.styleable.RankAppItemView_rank_appIconDrawable));
        titleTextView.setText(typedArray.getString(R.styleable.RankAppItemView_rank_title));
        descTextView.setText(typedArray.getString(R.styleable.RankAppItemView_rank_description));

        typedArray.recycle();
    }

    public String getNumberText() {
        return String.valueOf(numberTextView.getText());
    }

    public void setNumberText(String text) {
        numberTextView.setText(text);
    }

    public void setNumberText(@StringRes int stringResId) {
        titleTextView.setText(stringResId);
    }

    public void setNumberBackgroundTint(@ColorRes int colorResId) {
        numberTextView.getBackground().setTint(getResources().getColor(colorResId));
    }

    public void setIconImageDrawable(@DrawableRes int resId) {
        iconImageView.setImageResource(resId);
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
