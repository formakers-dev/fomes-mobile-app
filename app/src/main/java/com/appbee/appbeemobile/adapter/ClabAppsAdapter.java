package com.appbee.appbeemobile.adapter;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClabAppsAdapter extends CommonRecyclerViewAdapter{
    @Inject
    public ClabAppsAdapter(Context context) {
        super(context);
    }
}