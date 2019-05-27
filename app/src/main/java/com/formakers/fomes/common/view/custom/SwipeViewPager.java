package com.formakers.fomes.common.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

public class SwipeViewPager extends ViewPager {
    private boolean isEnabledSwipe;

    public SwipeViewPager(Context context) {
        super(context);
    }

    public SwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEnabledSwipe) {
            return super.onInterceptTouchEvent(ev);
        } else {
            if (ev.getAction() != MotionEvent.ACTION_MOVE) {
                if (super.onInterceptTouchEvent(ev)) {
                    super.onTouchEvent(ev);
                }
            }

            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabledSwipe) {
            return super.onTouchEvent(ev);
        } else {
            return MotionEventCompat.getActionMasked(ev) != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev);
        }
    }

    public void setEnableSwipe(boolean enabled) {
        this.isEnabledSwipe = enabled;
    }
}