package com.formakers.fomes.main.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.model.AppInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AppInfoDetailDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = AppInfoDetailDialogFragment.class.getSimpleName();

    @BindView(R.id.collection_button) Button saveCollectionButton;
    @BindView(R.id.block_button) Button blockButton;
    @BindView(R.id.item_app_icon_imageview) ImageView iconImageView;
    @BindView(R.id.item_app_name_textview) TextView nameTextView;
    @BindView(R.id.item_app_genre_developer_textview) TextView genreDeveloperTextView;
    @BindView(R.id.item_app_label_textview) TextView labelTextView;
    @BindView(R.id.download_button) Button downloadButton;

    Unbinder unbinder;

    public AppInfoDetailDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.RecommendTheme_BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_appinfo_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle == null) {
            return;
        }

        AppInfo appInfo = bundle.getParcelable(FomesConstants.EXTRA.APPINFO);
        Log.d(TAG, String.valueOf(appInfo));

        Glide.with(getContext()).load(appInfo.getIconUrl())
                .apply(new RequestOptions().override(70, 70).centerCrop())
                .into(iconImageView);
        nameTextView.setText(appInfo.getAppName());
        genreDeveloperTextView.setText(String.format("%s / %s", appInfo.getCategoryName1(), appInfo.getDeveloper()));

        downloadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + appInfo.getPackageName()));
            startActivity(intent);
        });

        // temp
        labelTextView.setBackground(getContext().getResources().getDrawable(R.drawable.item_app_label_background, new ContextThemeWrapper(getContext(), R.style.FomesTheme_TurquoiseItem).getTheme()));
        labelTextView.setText("배틀그라운드 게이머들이 많이 하는 게임 1위");
        saveCollectionButton.setOnClickListener(v -> Toast.makeText(getContext(), "컬렉션 저장 버튼 클릭함", Toast.LENGTH_LONG).show());
        blockButton.setOnClickListener(v -> Toast.makeText(getContext(), "컬렉션 저장 버튼 클릭함", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }
}
