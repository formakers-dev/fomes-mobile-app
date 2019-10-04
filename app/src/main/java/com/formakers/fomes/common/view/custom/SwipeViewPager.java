package com.formakers.fomes.common.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

import com.formakers.fomes.common.util.Log;

/**
 * SwipeViewPager
 * - 터치 스와이프 여부를 설정할 수 있는 뷰 페이저 (`setEnableSwipe()`)
 * - 터치 스와이프 콜백 설정 가능
 */
public class SwipeViewPager extends ViewPager {
    private static final String TAG = "SwipeViewPager";

    private boolean isEnabledSwipe;
    private SwipeListener swipeListener;

    private boolean isStartMove;

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
        Log.v(TAG, "onTouchEvent) ev.getAction() " + ev.getAction());

        if (isEnabledSwipe) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    isStartMove = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isStartMove) {
                        swipeListener.onSwipe();
                    }
                    isStartMove = false;
                    break;
            }

            return super.onTouchEvent(ev);
        } else {
            return MotionEventCompat.getActionMasked(ev) != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev);
        }
    }

    public void setEnableSwipe(boolean enabled) {
        this.isEnabledSwipe = enabled;
    }

    public void setOnSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    public interface SwipeListener {
        void onSwipe();
    }
}