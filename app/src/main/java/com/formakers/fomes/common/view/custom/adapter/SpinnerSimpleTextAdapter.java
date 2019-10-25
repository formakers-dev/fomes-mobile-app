package com.formakers.fomes.common.view.custom.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.formakers.fomes.R;

import java.util.List;

/**
 * 필수요소
 *  - TextView spinner_item_textview
 */
public class SpinnerSimpleTextAdapter extends BaseAdapter {

    private Context context;
    private int layoutResId;
    private int hintStringResId = R.string.common_empty_string;
    private List<String> data;

    public SpinnerSimpleTextAdapter(Context context, @LayoutRes int layoutResId, List<String> data) {
        this.context = context;
        this.layoutResId = layoutResId;
        this.data = data;
    }

    public void setHint(int hintStringResId) {
        this.hintStringResId = hintStringResId;
    }

    public int getHintPosition() {
        return this.data.size() + 1;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.layoutResId, parent, false);
        }

        if (data != null) {
            TextView itemTextView = convertView.findViewById(R.id.spinner_item_textview);

            if (position == getHintPosition()) {
                convertView.findViewById(R.id.spinner_divider).setVisibility(View.GONE);
                itemTextView.setGravity(Gravity.START);
                itemTextView.setText(R.string.common_empty_string);
                itemTextView.setHint(hintStringResId);
            } else {
                itemTextView.setText(data.get(position));
            }
        }

        return convertView;
    }
}
