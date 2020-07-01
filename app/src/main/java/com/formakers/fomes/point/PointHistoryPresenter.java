package com.formakers.fomes.point;

import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@PointHistoryDagger.Scope
public class PointHistoryPresenter implements PointHistoryContract.Presenter {
    public static final String TAG = "PointHistoryPresenter";

    private PointHistoryContract.View view;
    private PointService pointService;

    @Inject
    public PointHistoryPresenter(PointHistoryContract.View view, PointService pointService) {
        this.view = view;
        this.pointService = pointService;
    }

    @Override
    public void bindAvailablePoint() {
        this.pointService.getAvailablePoint()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(point -> view.setAvailablePoint(point), e -> Log.e(TAG, String.valueOf(e)));
    }
}
