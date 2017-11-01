package com.appbee.appbeemobile.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appbee.appbeemobile.R;

public class InterviewInfoView extends LinearLayout {

    TextView textView;
    ImageView imageView;

    public InterviewInfoView(Context context) {
        super(context);
    }

    public InterviewInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        setTypeArray(context.obtainStyledAttributes(attrs, R.styleable.InterviewInfo));
    }

    public InterviewInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setTypeArray(context.obtainStyledAttributes(attrs, R.styleable.InterviewInfo));
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_interview_item, this, false);
        addView(view);

        textView = ((TextView) findViewById(R.id.interview_text));
        imageView = ((ImageView) findViewById(R.id.interview_icon));
    }

    private void setTypeArray(TypedArray typedArray) {
        textView.setText(typedArray.getString(R.styleable.InterviewInfo_text));
        imageView.setImageDrawable(typedArray.getDrawable(R.styleable.InterviewInfo_iconDrawable));
    }
}