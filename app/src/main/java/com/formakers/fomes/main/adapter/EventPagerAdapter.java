package com.formakers.fomes.main.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EventPagerAdapter extends PagerAdapter {

    private List<EventPagerAdapter.Event> events = new ArrayList<>();

    public void addView(View view, Class destClass) {
        events.add(new Event(view, destClass));
    }

    @Override
    public int getItemPosition(Object object) {
        return events.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Event event = events.get(position);

        // it is temporary structure
        // TO-BE :
        //  - When item is clicked : WebViewActivity with event data (like url)
        event.view.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), event.destClass);
            view.getContext().startActivity(intent);
        });

        container.addView(event.view);

        return event.view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
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
        Class destClass;

        public Event(View view, Class destClass) {
            this.view = view;
            this.destClass = destClass;
        }
    }
}
