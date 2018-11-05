package com.formakers.fomes.main.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.formakers.fomes.R;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.custom.RecommendAppItemView;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.model.AppInfo;
import com.google.common.base.Joiner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;

public class AppInfoDetailDialogFragment extends BottomSheetDialogFragment implements BaseView<RecommendContract.Presenter> {

    public static final String TAG = AppInfoDetailDialogFragment.class.getSimpleName();

    @BindView(R.id.app_detail_view) RecommendAppItemView appDetailView;
    @BindView(R.id.download_button) Button downloadButton;

    Unbinder unbinder;

    private RecommendContract.Presenter presenter;

    @Override
    public void setPresenter(RecommendContract.Presenter presenter) {
        this.presenter = presenter;
    }

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
        int recommendType = bundle.getInt(FomesConstants.EXTRA.RECOMMEND_TYPE);
        List<String> recommendCriteria = bundle.getStringArrayList(FomesConstants.EXTRA.RECOMMEND_CRITERIA);

        Log.v(TAG, String.valueOf(appInfo));

        appDetailView.bindAppInfo(appInfo);
        appDetailView.setRecommendType(recommendType);
        appDetailView.setLabelText(Joiner.on(" ").join(recommendCriteria.toArray()));
        appDetailView.setOnWishListToggleButtonListener(v -> {
            if (!((ToggleButton) v).isChecked()) {
                this.presenter.emitRemoveFromWishList(appInfo.getPackageName())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    ((ToggleButton) v).setChecked(false);
                                    this.presenter.emitRefreshWishedByMe(appInfo.getPackageName(), false);
                                },
                                e -> Toast.makeText(this.getActivity(), "위시리스트 삭제에 실패하였습니다.", Toast.LENGTH_LONG).show());
            } else {
                this.presenter.emitSaveToWishList(appInfo.getPackageName())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    ((ToggleButton) v).setChecked(true);
                                    this.presenter.emitRefreshWishedByMe(appInfo.getPackageName(), true);

                                },
                                e -> Toast.makeText(this.getActivity(), "위시리스트 등록에 실패하였습니다.", Toast.LENGTH_LONG).show());
            }
        });

        downloadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + appInfo.getPackageName()));
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }
}
