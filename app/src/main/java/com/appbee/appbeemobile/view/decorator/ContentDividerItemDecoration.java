package com.appbee.appbeemobile.view.decorator;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ContentDividerItemDecoration extends DividerItemDecoration {

    public ContentDividerItemDecoration(Context context, int orientation) {
        super(context, orientation);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            return;
        }

        super.getItemOffsets(outRect, view, parent, state);
    }
}
