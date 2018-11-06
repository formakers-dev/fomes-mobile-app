package com.formakers.fomes.main.view;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;

import butterknife.OnClick;

public class EventFragment extends BaseFragment {

    private static final String TAG = EventFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_temp_event, container, false);
    }

    private void showEventProgressDialog(String title, String subTitle, String description, Drawable image, boolean isActivated) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_temp_event_progress, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView titleTextView = view.findViewById(R.id.event_dialog_title_textview);
        titleTextView.setText(title);

        if (!isActivated) {
            titleTextView.setBackground(getResources().getDrawable(R.drawable.event_dialog_rounded_stroke_rect_background, null));
            titleTextView.setTextColor(getResources().getColor(R.color.fomes_light_warm_gray));
        }

        ((TextView) view.findViewById(R.id.event_dialog_subtitle_textview)).setText(subTitle);
        ((TextView) view.findViewById(R.id.event_dialog_description_textview)).setText(description);
        ((ImageView) view.findViewById(R.id.event_dialog_imageview)).setImageDrawable(image);

        view.findViewById(R.id.event_dialog_ok_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @OnClick(R.id.progress_background_1)
    public void onProgress1Clicked() {
        showEventProgressDialog(getString(R.string.temp_event_progress_1_title)
                , getString(R.string.temp_event_progress_1_subtitle)
                , getString(R.string.temp_event_progress_1_description)
                , getResources().getDrawable(R.drawable.event_analysis_capture, null),
                true);
    }

    @OnClick(R.id.progress_background_2)
    public void onProgress2Clicked() {
        showEventProgressDialog(getString(R.string.temp_event_progress_2_title)
                , getString(R.string.temp_event_progress_2_subtitle)
                , getString(R.string.temp_event_progress_2_description)
                , getResources().getDrawable(R.drawable.event_recommend_capture, null)
                , false);
    }

    @OnClick(R.id.progress_background_3)
    public void onProgress3Clicked() {
        showEventProgressDialog(getString(R.string.temp_event_progress_3_title)
                , getString(R.string.temp_event_progress_3_subtitle)
                , getString(R.string.temp_event_progress_3_description)
                , getResources().getDrawable(R.drawable.event_feedback_capture, null)
                , false);
    }
}