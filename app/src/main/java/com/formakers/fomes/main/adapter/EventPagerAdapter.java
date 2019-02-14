package com.formakers.fomes.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.common.constant.Feature;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.WebViewActivity;
import com.formakers.fomes.main.view.EventDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class EventPagerAdapter extends PagerAdapter {

    private static final String TAG = "EventPagerAdapter";

    private List<EventPagerAdapter.Event> events = new ArrayList<>();

    @Deprecated
    public void addView(View view, @LayoutRes int layoutResId) {
        events.add(new Event(view, layoutResId));
    }

    public void addView(View view, String contents) {
        events.add(new Event(view, contents));
    }

    @Override
    public int getItemPosition(Object object) {
        return events.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.v(TAG, "instantiateItem) position=" + position);

        Event event = events.get(position);
        View view = event.view;

        if (Feature.PROMOTION_URL) {
            Context context = container.getContext();

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_CONTENTS, event.contents);
                context.startActivity(intent);
            });

        } else {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                intent.putExtra(EventDetailActivity.EXTRA_LAYOUT_RES_ID, event.destLayoutResId);
                v.getContext().startActivity(intent);
            });
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.v(TAG, "destroyItem) position=" + position);

        container.removeView(events.get(position).view);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    class Event {
        View view;
        @Deprecated @LayoutRes int destLayoutResId;
        String contents;

        @Deprecated
        public Event(View view, @LayoutRes int destLayoutResId) {
            this.view = view;
            this.destLayoutResId = destLayoutResId;
        }

        public Event(View view, String contents) {
            this.view = view;
            this.contents = contents;
        }
    }
}
